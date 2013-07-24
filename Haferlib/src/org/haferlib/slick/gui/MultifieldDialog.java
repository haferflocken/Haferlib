// TODO: A big overhaul of this class.

//A dialog is basically a window. It is a box with a title bar.
//A MultifieldDialog is a dialog with two buttons (Submit and Cancel) and some number of fields contained in a scrollable frame.

package org.haferlib.slick.gui;

import java.util.HashSet;

import org.newdawn.slick.Input;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

public class MultifieldDialog extends GUISubcontext implements GUIEventGenerator, GUIEventListener {

	private static final String SUBMIT_NAME = "Submit";
	private static final String CANCEL_NAME = "Cancel";

	private String title;
	private int titleX;
	private int titleY;
	private int yTitleBorder;
	private Button<Object> submitButton;
	private Button<Object> cancelButton;
	private TextField[] textFields;
	private Color textColor;
	private Color backgroundColor;
	private int x2;
	private int y2;
	private int width;
	private int height;
	private HashSet<GUIEventListener> listeners;
	private boolean dead;

	//Constructors
	public MultifieldDialog(String title, String[] fieldNames, int x, int y, int width, int height, int fieldHeight) {
		this(title, fieldNames, Color.white, Color.black, Color.white, Color.black, Color.blue, x, y, width, height, fieldHeight);
	}

	public MultifieldDialog(String title, String[] fieldNames, Color textColor, Color backgroundColor, Color fieldTextColor, Color fieldBackgroundColor, Color highlightColor, int x, int y, int width, int height, int fieldHeight) {
		super(x, y);
		setWidth(width);
		setHeight(height);

		//Set the title and colors
		this.title = title;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;

		//Title position
		yTitleBorder = y1 + fieldHeight;
		titleX = x1 + width / 2;
		titleY = y1 + fieldHeight / 2;

		//Make the submit and cancel buttons.
		submitButton = new Button<Object>(SUBMIT_NAME, textColor, x1 + width / 2, y2 - fieldHeight, width / 2, fieldHeight, 0, backgroundColor, highlightColor, Input.KEY_ENTER);
		cancelButton = new Button<Object>(CANCEL_NAME, textColor, x1, y2 - fieldHeight, width / 2 - 1, fieldHeight, 0, backgroundColor, highlightColor, Input.KEY_ESCAPE);
		submitButton.addListener(this);
		cancelButton.addListener(this);

		//Make the text fields.
		textFields = new TextField[fieldNames.length];
		int fieldX = x1 + (int)(width * 0.05);
		int fieldWidth = (int)(width * 0.9);
		Color fieldBgTextColor = fieldTextColor.darker();
		for (int i = 0; i < textFields.length; i++) {
			textFields[i] = new TextField(fieldX, y1 + fieldHeight * (i + 1) * 3 / 2, fieldWidth, fieldHeight, "", fieldNames[i], fieldTextColor, fieldBgTextColor, fieldTextColor, fieldBackgroundColor);
		}

		//Make the scrollable frame and put the text fields in it.
		ScrollableFrame fieldFrame = new ScrollableFrame(x1, yTitleBorder, width, height - fieldHeight * 2, 0, 8, textColor);
		fieldFrame.addElements(textFields);

		//Add the scrollable frame and buttons to the subcontext.
		subcontext.addElement(fieldFrame);
		subcontext.addElement(submitButton);
		subcontext.addElement(cancelButton);

		//Make the listener set.
		listeners = new HashSet<>();

		//Is this dead? Of course not, we just constructed it.
		dead = false;
	}

	//@see GUIElement.render(Graphics)
	public void render(Graphics g) {
		//Render the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		//Draw the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);

		//Render the title bar and frame border.
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);
		g.drawLine(x1, yTitleBorder, x2, yTitleBorder);
		g.drawString(title, titleX - g.getFont().getWidth(title)/2, titleY - g.getFont().getLineHeight()/2);
	}

	//@see GUIElement.setX(int)
	public void setX(int x) {
		super.setX(x);
		x2 = x + width;
		titleX = x1 + width / 2;
	}


	//@see GUIElement.setY(int)
	public void setY(int y) {
		int dY = y - y1;
		super.setY(y);
		y2 = y1 + height;
		yTitleBorder += dY;
		titleY += dY;
	}

	//Set and get the width.
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}

	public int getWidth() {
		return width;
	}

	//Set and get the height.
	public void setHeight(int h) {
		height = h;
		y2 = y1 + height;
	}

	public int getHeight() {
		return height;
	}

	//@see GUIElement.pointIsWithin(int, int)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	//@see GUIElement.getDepth()
	public int getDepth() {
		return 10;
	}

	//@see GUIEventListener.guiEvent(GUIEvent)
	@SuppressWarnings("rawtypes")
	public void guiEvent(GUIEvent event) {
		//If sumbit...
		if (event.getGenerator() == submitButton) {
			//Build an array of strings
			String[] fieldData = new String[textFields.length];
			for (int i = 0; i < textFields.length; i++)
				fieldData[i] = textFields[i].toString();
			//And send it off
			for (GUIEventListener l : listeners)
				l.guiEvent(new GUIEvent<String[]>(this, fieldData));
			//Kill this element
			dead = true;
		}
		//If cancel...
		else if (event.getGenerator() == cancelButton) {
			//Notify the listeners
			for (GUIEventListener l : listeners)
				l.guiEvent(new GUIEvent(this));
			//Kill this element
			dead = true;

		}
		//If an invalid generator...
		else {
			System.out.println("Invalid GUIEvent given to MultifieldDialog.");
		}
	}

	//@see GUIEventGenerator.addListener(UIEventListener)
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	//@see GUIEventGenerator.removeListener(UIEventListener)
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

	//@see GUIElement.dead()
	public boolean dead() {
		return dead;
	}
}