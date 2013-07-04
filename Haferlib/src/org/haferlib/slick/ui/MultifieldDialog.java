//A dialog is basically a window. It is a box with a title bar.
//A MultifieldDialog is a dialog with two buttons (Submit and Cancel) and some number of fields.

package org.haferlib.slick.ui;

import java.util.HashSet;

import org.newdawn.slick.Input;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

public class MultifieldDialog implements UIElement, UIEventGenerator, UIEventListener {

	private static final String SUBMIT_NAME = "Submit";
	private static final String CANCEL_NAME = "Cancel";

	private String title;
	private int titleX;
	private int titleY;
	private int yTitleBorder;
	private Button submitButton;
	private Button cancelButton;
	private TextField[] fields;
	private Color textColor;
	private Color backgroundColor;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int width;
	private int height;
	private int subHeight;
	private UIElementGroup subElements;
	private HashSet<UIEventListener> listeners;
	private boolean dead;

	//Constructors
	private MultifieldDialog() {
	}

	public MultifieldDialog(String t, String[] fNames, int x, int y, int w, int subH) {
		this(t, fNames, Color.white, Color.black, Color.white, Color.black, Color.blue, x, y, w, subH);
	}

	public MultifieldDialog(String t, String[] fNames, Color tColor, Color bgColor, Color pFieldColor, Color bFieldColor, Color hlightColor, int x, int y, int w, int subH) {
		//Define the sub height
		subHeight = subH;

		//Half subH will be useful
		int halfSubH = subH / 2;

		//Assign positions
		int frameHeight = subH + subH * fNames.length + halfSubH * (fNames.length - 1);
		height = subH * 2 + frameHeight; //Admittedly, this line is gross. Oh well.
		width = w;
		x1 = x;
		x2 = x + width;
		y1 = y;
		y2 = y + height;

		//Set the title and colors
		title = t;
		textColor = tColor;
		backgroundColor = bgColor;

		//Title position
		yTitleBorder = y1 + subH;
		titleX = x1 + width / 2;
		titleY = y1 + halfSubH;

		//Make the buttons
		submitButton = new Button(SUBMIT_NAME, tColor, x1 + w/2, y2 - subH, w/2, subH, bgColor, hlightColor, Input.KEY_ENTER);
		cancelButton = new Button(CANCEL_NAME, tColor, x1, y2 - subH, w/2 - 1, subH, bgColor, hlightColor, Input.KEY_ESCAPE);
		submitButton.addListener(this);
		cancelButton.addListener(this);

		//Make the fields
		fields = new TextField[fNames.length];
		UIElement[] scrollableElements = new UIElement[fields.length];
		int fieldWidth = (int)(w * 0.9);
		Color fieldBgTextColor = pFieldColor.darker();
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new TextField(0, 0, fieldWidth, subH, "", fNames[i], pFieldColor, fieldBgTextColor, pFieldColor, bFieldColor);
			scrollableElements[i] = fields[i];
		}

		//Make the UIElementGroup
		UIElement[] subEs = new UIElement[3];
		subEs[0] = new ScrollableFrame(scrollableElements, x1, y1 + subH, width, frameHeight, 0, 10, halfSubH, (int)(w * 0.05), halfSubH, 0, pFieldColor, bgColor);
		subEs[1] = cancelButton;
		subEs[2] = submitButton;
		subElements = new UIElementGroup(subEs, x1, y1);

		//Make the listener set
		listeners = new HashSet<UIEventListener>();

		//Is this dead? Of course not, we just constructed it.
		dead = false;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics g)
	public void render(Graphics g) {
		//Render the frame
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);
		//g.drawLine(x1, yTitleBorder, x2, yTitleBorder);

		//Draw the title
		g.drawString(title, titleX - g.getFont().getWidth(title)/2, titleY - g.getFont().getLineHeight()/2);

		//Draw the fields and buttons
		subElements.render(g);
	}

	//@see UIElement.setX(int x)
	public void setX(int x) {
		x1 = x;
		x2 = x + width;
		titleX = x1 + width / 2;
		subElements.setX(x);
	}

	public int getX() {
		return x1;
	}

	//@see UIElement.setY(int y)
	public void setY(int y) {
		y1 = y;
		y2 = y + height;
		yTitleBorder = y1 + subHeight;
		titleY = y1 + subHeight / 2;
		subElements.setY(y);
	}

	public int getY() {
		return y1;
	}

	//Get the width
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	//@see UIElement.pointIsWithin(int x, int y)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	//@see UIElement.click(int x, int y)
	public void click(int x, int y) {
		subElements.click(x, y);
	}

	//@see UIElement.hover(int x, int y)
	public void hover(int x, int y) {
		subElements.hover(x, y);
	}

	//@see UIElement.clickedElsewhere()
	public void clickedElsewhere() {
		subElements.clickedElsewhere();
	}

	//@see UIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
		subElements.hoveredElsewhere();
	}

	//@see UIElement.getDepth()
	public int getDepth() {
		return 10;
	}

	//@see UIElement.keyInputDone()
	public void keyPressed(int key, char c) {
		subElements.keyPressed(key, c);
	}

	//@see UIElement.keyInputDone()
	public void keyInputDone() {
		subElements.keyInputDone();
	}

	//@see UIEventListener.uiEvent(UIEvent)
	public void uiEvent(UIEvent event) {
		//If sumbit...
		if (event.getGenerator() == submitButton) {
			//Build an array of strings
			String[] fieldData = new String[fields.length];
			for (int i = 0; i < fields.length; i++)
				fieldData[i] = fields[i].toString();
			//And send it off
			for (UIEventListener l : listeners)
				l.uiEvent(new UIEvent<String[]>(this, fieldData));
			//Kill this element
			dead = true;
		}
		//If cancel...
		else if (event.getGenerator() == cancelButton) {
			//Notify the listeners
			for (UIEventListener l : listeners)
				l.uiEvent(new UIEvent(this));
			//Kill this element
			dead = true;

		}
		//If an invalid generator...
		else {
			System.out.println("Invalid UIEvent given to MultifieldDialog.");
		}
	}

	//@see UIEventGenerator.addListener(UIEventListener)
	public void addListener(UIEventListener l) {
		listeners.add(l);
	}

	//@see UIEventGenerator.removeListener(UIEventListener)
	public void removeListener(UIEventListener l) {
		listeners.remove(l);
	}

	//@see UIElement.dead()
	public boolean dead() {
		return dead;
	}
}