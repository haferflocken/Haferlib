package org.haferlib.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * An editor for several strings. Allows autocompleting fields and uses
 * a scroll bar for the fields if they need more room.
 * 
 * @author John Werner
 */

public class ArrayEditor extends GUISubcontext {
	
	private static final String[] EMPTY_STRING_ARRAY = {};

	private final String title;
	private final Font font;
	private final Color textColor, backgroundColor;
	private final ScrollableListFrame fieldList;
	private final SearchField[] fields;
	
	public ArrayEditor(int x, int y, int width, int height, int depth, int ySpacing,
			String title, Font font, Color textColor, Color fieldColor, Color backgroundColor, Color searchColor,
			int numFields, String[] fieldContents, String[] fieldMessages, String[][] fieldSearchStrings) {
		super(x, y, width, height, depth);
		
		// Check that input is valid.
		if (title == null)
			throw new IllegalArgumentException("title cannot be null.");
		if (font == null)
			throw new IllegalArgumentException("font cannot be null.");
		if (textColor == null)
			throw new IllegalArgumentException("textColor cannot be null.");
		if (numFields <= 0)
			throw new IllegalArgumentException("numFields must be greater than 0.");
		if (fieldContents != null && fieldContents.length != numFields)
			throw new IllegalArgumentException("fieldContents must either be null or have length numFields.");
		if (fieldMessages != null && fieldMessages.length != numFields)
			throw new IllegalArgumentException("fieldMessages must either be null or have length numFields.");
		if (fieldSearchStrings != null && fieldSearchStrings.length != numFields)
			throw new IllegalArgumentException("fieldSearchStrings must either be null or have length numFields.");
		
		// Assign values.
		this.title = title;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		fields = new SearchField[numFields];
		
		// Create the field list.
		int listX = x1;
		int listY = y1 + font.getLineHeight();
		int listWidth = width;
		int listHeight = height - font.getLineHeight();
		int listScrollbarWidth = 10;
		int listXOffset = listScrollbarWidth;
		fieldList = new ScrollableListFrame(listX, listY, listWidth, listHeight, 0,
				listScrollbarWidth, textColor, ListFrame.XALIGN_LEFT, listXOffset, ySpacing);
		subcontext.addElement(fieldList);
		
		// Create the fields.
		int fieldWidth = listWidth - listXOffset - listScrollbarWidth;
		int fieldHeight = font.getLineHeight();
		for (int i = 0; i < numFields; i++) {
			String startText = (fieldContents == null)? null : fieldContents[i];
			String backgroundMessage = (fieldMessages == null)? null : fieldMessages[i];
			String[] searchStrings = (fieldSearchStrings == null || fieldSearchStrings[i] == null)? EMPTY_STRING_ARRAY : fieldSearchStrings[i];
			fields[i] = new SearchField(0, 0, fieldWidth, fieldHeight, 0,
					startText, backgroundMessage, searchStrings, font,
					textColor, textColor, searchColor, fieldColor);
		}
		fieldList.addElements(fields);
		
		subcontext.addAndRemoveElements();
	}
	
	public String[] getFieldContents() {
		String[] out = new String[fields.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = fields[i].toString();
		}
		return out;
	}
	
	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		fieldList.setWidth(width);
		int newFieldWidth = fieldList.getWidth() - fieldList.getXAlignOffset() - fieldList.getScrollBarWidth();
		for (SearchField f : fields)
			f.setWidth(newFieldWidth);
	}
	
	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		fieldList.setHeight(height - font.getLineHeight());
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
	
}
