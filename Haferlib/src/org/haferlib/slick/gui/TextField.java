//A rectangular field that you can type in.
//Can display a "background message" when the field is empty.

package org.haferlib.slick.gui;

import java.util.Arrays;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

public class TextField implements GUIElement {

	//An array of the characters that are allowed that aren't letters or digits. I took these from what would be considered valid in a URL.
	public static final char[] VALID_SYMBOLS = {' ', '!', '#', '$', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '=', '?', '@', '[', ']', '_', '~'};

	private StringBuilder text;
	private String textAsString;
	private String backgroundMessage;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int width;
	private int height;
	private int centerX;
	private int centerY;
	private Color textColor;
	private Color backgroundMessageColor;
	private Color borderColor;
	private Color backgroundColor;

	//Constructors
	public TextField(int x, int y, int w, int h) {
		this(x, y, w, h, Color.white, Color.white, Color.black);
	}

	public TextField(int x, int y, int w, int h, Color tColor, Color bColor, Color bgColor) {
		this(x, y, w, h, "", null, tColor, tColor.darker(), bColor, bgColor);
	}

	public TextField(int x, int y, int w, int h, String startText, String bgText, Color tColor, Color bgTextColor, Color bColor, Color bgColor) {
		setWidth(w);
		setHeight(h);
		setX(x);
		setY(y);
		text = new StringBuilder(startText);
		textAsString = startText;
		backgroundMessage = bgText;
		textColor = tColor;
		backgroundMessageColor = bgTextColor;
		borderColor = bColor;
		backgroundColor = bgColor;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics g)
	public void render(Graphics g) {
		//Draw the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		//The text's y loc
		int textY = centerY - g.getFont().getLineHeight()/2;

		//Draw the background message if there's no text
		if ((backgroundMessage != null) && (textAsString.length() == 0)) {
			g.setColor(backgroundMessageColor);
			g.drawString(backgroundMessage, centerX - g.getFont().getWidth(backgroundMessage)/2, textY);
		}
		//Or draw the text if there is text
		else {
			g.setColor(textColor);
			g.drawString(textAsString, centerX - g.getFont().getWidth(textAsString)/2, textY);
		}

		//Draw the outline
		g.setColor(borderColor);
		g.drawRect(x1, y1, width, height);
	}

	//@see UIElement.setX(int x)
	public void setX(int x) {
		x1 = x;
		x2 = x + width;
		centerX = x1 + width/2;
	}

	public int getX() {
		return x1;
	}

	//@see UIElement.setY(int y)
	public void setY(int y) {
		y1 = y;
		y2 = y + height;
		centerY = y1 + height/2;
	}

	public int getY() {
		return y1;
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

	//@see GUIElement.click(int, int, int)
	public void click(int x, int y, int button) {
	}

	//@see GUIElement.mouseDown(int, int, int)
	public void mouseDown(int x, int y, int button) {
	}

	//@see GUIElement.hover(int x, int y)
	public void hover(int x, int y) {
	}

	//@see GUIElement.clickedElsewhere(int)
	public void clickedElsewhere(int button) {
	}

	//@see GUIElement.mouseDownElsewhere(int)
	public void mouseDownElsewhere(int button) {
	}

	//@see GUIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
	}

	//@see GUIElement.getDepth()
	public int getDepth() {
		return 1;
	}

	//Determines if a key/char pair is a valid character to add to our string.
	public boolean isTextKey(int key, char c) {
		return (Character.isLetterOrDigit(c) || Arrays.binarySearch(VALID_SYMBOLS, c) >= 0);
	}

	//Called when a key press happens.
	public void keyPressed(int key, char c) {
		//If it's a text key
		if (isTextKey(key, c)) {
			text.append(c);
		}
		//If it's a backspace
		else if (key == Input.KEY_BACK) {
			if (text.length() > 0)
				text.deleteCharAt(text.length() - 1);
		}
	}

	//@see GUIElement.keyInputDone()
	public void keyInputDone() {
		textAsString = text.toString();
	}

	//Returns the string representation of this TextField.
	public String toString() {
		return textAsString;
	}

	//Clears this TextField's text.
	public void clear() {
		text.delete(0, text.length());
		textAsString = "";
	}

	//@see GUIElement.dead()
	public boolean dead() {
		return false;
	}
	
	//@see GUIElement.destroy()
	public void destroy() {
	}
}