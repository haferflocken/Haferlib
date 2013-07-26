//A rectangular field that you can type in.
//Can display a "background message" when the field is empty.

package org.haferlib.slick.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

public class TextField implements GUIElement {

	// An array of the characters that are allowed that aren't letters or digits. I took these from what would be considered valid in a URL.
	public static final char[] VALID_SYMBOLS = {' ', '!', '#', '$', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '=', '?', '@', '[', ']', '_', '~'};

	private StringBuilder text;
	private String textAsString;
	private String backgroundMessage;
	private int x1, y1, x2, y2;
	private int width, height;
	private int centerX, centerY;
	private int depth;
	private Color textColor;
	private Color backgroundMessageColor;
	private Color borderColor;
	private Color backgroundColor;

	// Constructors.
	public TextField(int x, int y, int w, int h, int d) {
		this(x, y, w, h, d, Color.white, Color.white, Color.black);
	}

	public TextField(int x, int y, int w, int h, int d, Color tColor, Color bColor, Color bgColor) {
		this(x, y, w, h, d, "", null, tColor, tColor.darker(), bColor, bgColor);
	}

	public TextField(int x, int y, int w, int h, int d, String startText, String bgText, Color tColor, Color bgTextColor, Color bColor, Color bgColor) {
		setWidth(w);
		setHeight(h);
		setX(x);
		setY(y);
		setDepth(d);
		text = new StringBuilder(startText);
		textAsString = startText;
		backgroundMessage = bgText;
		textColor = tColor;
		backgroundMessageColor = bgTextColor;
		borderColor = bColor;
		backgroundColor = bgColor;
	}
	
	// EFFECTS:  Determines if a key/char pair is a valid character to add to our string.
	private boolean isTextKey(int key, char c) {
		if (Character.isLetterOrDigit(c))
			return true;
		for (char symbol : VALID_SYMBOLS)  {
			if (symbol == c)
				return true;
		}
		return false;
	}

	// EFFECTS: Clears this TextField's text.
	public void clear() {
		text.delete(0, text.length());
		textAsString = "";
	}

	@Override
	public void update(int delta) {
	}

	@Override
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

	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x + width;
		centerX = x1 + width/2;
	}

	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		y2 = y + height;
		centerY = y1 + height/2;
	}

	@Override
	public int getY() {
		return y1;
	}

	@Override
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setHeight(int h) {
		height = h;
		y2 = y1 + height;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	@Override
	public void click(int x, int y, int button) {
	}

	@Override
	public void mouseDown(int x, int y, int button) {
	}

	@Override
	public void hover(int x, int y) {
	}

	@Override
	public void clickedElsewhere(GUIElement target, int button) {
	}

	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
	}

	@Override
	public void hoveredElsewhere(GUIElement target) {
	}
	
	@Override
	public void setDepth(int d) {
		depth = d;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void keyPressed(int key, char c) {
		// If it's a text key, remember it.
		if (isTextKey(key, c)) {
			text.append(c);
		}
		// If it's a backspace, delete the last character in text.
		else if (key == Input.KEY_BACK) {
			if (text.length() > 0)
				text.deleteCharAt(text.length() - 1);
		}
	}

	@Override
	public void keyInputDone() {
		textAsString = text.toString();
	}

	@Override
	public boolean dead() {
		return false;
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public String toString() {
		return textAsString;
	}
}