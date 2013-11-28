package org.haferslick.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.haferslick.gui.event.GUIEvent;
import org.haferslick.gui.event.GUIEventGenerator;
import org.haferslick.gui.event.GUIEventListener;
import org.haferslick.gui.event.ResizeEvent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * An editor for several strings, allowing strings to be added and removed.
 * Expands in height up to a max height, at which point uses a scroll bar to
 * display the string fields. Like ArrayEditor, supports autocompleting.
 * 
 * @author John Werner
 */

public class ListEditor extends GUISubcontext implements GUIEventListener, GUIEventGenerator {
	
	private static final String[] EMPTY_STRING_ARRAY = {};

	private int maxHeight;
	private final Set<GUIEventListener> listeners;
	private final String title;
	private final Font font;
	private final Color textColor, fieldColor, backgroundColor, searchColor;
	private final TextButton<String> addButton;
	//private final TextButton<String> clearButton;
	private final ScrollableListFrame fieldBarList;
	private final List<SearchField> fields;
	private String defaultFieldContents, defaultFieldMessage;
	private String[] defaultFieldSearchStrings;
	
	public ListEditor(int x, int y, int width, int maxHeight, int depth, int ySpacing,
			String title, Font font, Color textColor, Color fieldColor, Color backgroundColor, Color searchColor) {
		super(x, y, width, maxHeight, depth);
		
		// Check that input is valid.
		if (title == null)
			throw new IllegalArgumentException("title cannot be null.");
		if (font == null)
			throw new IllegalArgumentException("font cannot be null.");
		if (textColor == null)
			throw new IllegalArgumentException("textColor cannot be null.");
		
		// Assign values.
		this.maxHeight = maxHeight;
		this.title = title;
		this.font = font;
		this.textColor = textColor;
		this.fieldColor = fieldColor;
		this.backgroundColor = backgroundColor;
		this.searchColor = searchColor;
		defaultFieldContents = null;
		defaultFieldMessage = null;
		defaultFieldSearchStrings = null;
		
		// Create the listener set.
		listeners = new HashSet<GUIEventListener>();
		
		// Create the add button.
		String addButtonString = "Add";
		int addButtonWidth = font.getWidth(addButtonString);
		int addButtonHeight = font.getLineHeight();
		int addButtonX = x1 + font.getWidth(title);
		int addButtonY = y1;
		addButton = new TextButton<String>(null, addButtonX, addButtonY, addButtonWidth, addButtonHeight, 0,
				fieldColor, searchColor, Input.KEY_ENTER, addButtonString, textColor, font);
		addButton.addListener(this);
		subcontext.addElement(addButton);
		
		// Create the field bar list.
		fields = new ArrayList<SearchField>();
		int listX = x1;
		int listY = y1 + font.getLineHeight();
		int listWidth = width;
		int listHeight = height - font.getLineHeight();
		int listScrollbarWidth = 10;
		int listXOffset = listScrollbarWidth;
		fieldBarList = new ScrollableListFrame(listX, listY, listWidth, listHeight, 0,
				listScrollbarWidth, textColor, ListFrame.XALIGN_LEFT, listXOffset, ySpacing);
		subcontext.addElement(fieldBarList);
		
		subcontext.addAndRemoveElements();
		
		// Recalc the height.
		recalcHeight();
	}
	
	public String[] getFieldContents() {
		String[] out = new String[fields.size()];
		int i = 0;
		for (SearchField f : fields) {
			out[i] = f.toString();
			i++;
		}
		return out;
	}
	
	private void recalcHeight() {
		int newHeight = font.getLineHeight() * (1 + fields.size()); // Add up field heights.
		newHeight += fieldBarList.getYSpacing() * (fields.size() - 1); // Add in the ySpacing.
		
		if (newHeight == height)
			return;
		
		if (newHeight > maxHeight)
			newHeight = maxHeight;
		
		super.setHeight(newHeight);
		fieldBarList.setHeight(newHeight - font.getLineHeight());
		
		notifyListeners(new ResizeEvent(this));
	}
	
	private void notifyListeners(GUIEvent<?> event) {
		for (GUIEventListener l : listeners)
			l.guiEvent(event);
	}
	
	public void setDefaultFieldContents(String contents) {
		defaultFieldContents = contents;
	}
	
	public void setDefaultFieldMessage(String message) {
		defaultFieldMessage = message;
	}
	
	public void setDefaultFieldSearchStrings(String[] searchStrings) {
		defaultFieldSearchStrings = searchStrings;
	}
	
	public void addField() {
		addField(defaultFieldContents, defaultFieldMessage, defaultFieldSearchStrings);
	}
	
	public void addField(String contents, String backgroundMessage, String[] searchStrings) {
		// Calc some dimensions.
		int buttonSize = font.getLineHeight();
		int fieldBarWidth = fieldBarList.getWidth() - fieldBarList.getXAlignOffset() - fieldBarList.getScrollBarWidth();
		int fieldWidth = fieldBarWidth - buttonSize;
		int fieldHeight = font.getLineHeight();
		
		// Make the field.
		if (searchStrings == null)
			searchStrings = EMPTY_STRING_ARRAY;
		SearchField field = new SearchField(0, 0, fieldWidth, fieldHeight, 0,
				contents, backgroundMessage, searchStrings, font,
				textColor, textColor, searchColor, fieldColor);
		fields.add(field);
		
		// Make the horizontal list for the button and field to go into.
		HorizontalListFrame fieldBar = new HorizontalListFrame(0, 0, font.getLineHeight(), 0);
		
		// Make the delete button.
		GUIElement[] barFieldPair = new GUIElement[] { field, fieldBar };
		TextButton<GUIElement[]> deleteButton = new TextButton<GUIElement[]>(barFieldPair, 0, 0, buttonSize, buttonSize, 0,
				Color.red, Color.magenta, Input.KEY_ENTER, "[X]", textColor, font, TextButton.CENTER, 0);
		deleteButton.addListener(this);
		
		// Add the field and the button to the field bar.
		fieldBar.addElement(field);
		fieldBar.addElement(deleteButton);
		
		// Add the field bar to the field bar list.
		fieldBarList.addElement(fieldBar);
		
		// Recalc height.
		recalcHeight();
	}
	
	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		fieldBarList.setWidth(width);
		int newFieldBarWidth = fieldBarList.getWidth() - fieldBarList.getXAlignOffset() - fieldBarList.getScrollBarWidth();
		int newFieldWidth = newFieldBarWidth - font.getLineHeight();
		for (SearchField f : fields)
			f.setWidth(newFieldWidth);
	}
	
	@Override
	public void setHeight(int h) {
		maxHeight = h;
		recalcHeight();
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		if (backgroundColor != null) {
			g.setColor(backgroundColor);
			g.fillRect(x1, y1, width, height);
		}
		
		// Draw the title string.
		g.setColor(textColor);
		g.setFont(font);
		g.drawString(title, x1, y1);
		
		// Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If the event is from a TextButton and contains an array of a 
		// SearchField and a HorizontalListFrame, remove the SearchField from
		// fields, remove the HorizontalListFrame from fieldBarList.
		Object obGen = event.getGenerator();
		Object obData = event.getData();
		if (obGen instanceof TextButton && obData instanceof GUIElement[]) {
			GUIElement[] data = (GUIElement[])obData;
			if (data.length == 2 && data[0] instanceof SearchField && data[1] instanceof HorizontalListFrame) {
				SearchField field = (SearchField)data[0];
				HorizontalListFrame fieldBar = (HorizontalListFrame)data[1];
				fields.remove(field);
				fieldBarList.removeElement(fieldBar);
				recalcHeight();
			}
		}
		// If the event is from the add button, add a field!
		else if (obGen == addButton) {
			addField();
		}
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}
	
}
