//A button, that when pushed, does something.

package org.haferlib.slick.gui;

import java.util.HashSet;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;

public class Button<V> implements GUIElement, GUIEventGenerator {

	public static final byte LEFT = 0;
	public static final byte CENTER = 1;
	public static final byte RIGHT = 2;

	private String text;
	private V data;
	private Color textColor;
	private Font font;
	private byte textAlignment;
	private int textXOffset;
	private int x1, y1, x2, y2;
	private int centerX, centerY;
	private int width, height;
	private int depth;
	private Color backgroundColor;
	private boolean highlight;
	private Color highlightColor;
	private int buttonKey;
	private HashSet<GUIEventListener> listeners;

	// Constructors.
	public Button(String text, Color textColor, Font font, int x, int y, int width, int height,
			int depth, Color backgroundColor, Color highlightColor, int key) {
		this(text, null, textColor, font, CENTER, 0, x, y, width, height, depth, backgroundColor, highlightColor, key);
	}

	public Button(String text, Color textColor, Font font, byte tAlign, int tXOffset, int x, int y, int width, int height,
			int depth, Color backgroundColor, Color highlightColor, int key) {
		this(text, null, textColor, font, tAlign, tXOffset, x, y, width, height, depth, backgroundColor, highlightColor, key);
	}

	public Button(String text, V data, Color textColor, Font font, int x, int y, int width, int height,
			int depth, Color backgroundColor, Color highlightColor, int key) {
		this(text, data, textColor, font, CENTER, 0, x, y, width, height, depth, backgroundColor, highlightColor, key);
	}

	public Button(String text, V data, Color textColor, Font font, byte tAlign, int tXOffset, int x, int y, int width, int height,
			int depth, Color backgroundColor, Color highlightColor, int key) {
		this.text = text;
		this.data = data;
		this.textColor = textColor;
		this.font = font;
		textAlignment = tAlign;
		textXOffset = tXOffset;
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		this.depth = depth;
		this.backgroundColor = backgroundColor;
		this.highlightColor = highlightColor;
		highlight = false;
		listeners = new HashSet<>();
		buttonKey = key;
	}
	
	// Press this button.
	public void press() {
		for (GUIEventListener l : listeners)
			l.guiEvent(new GUIEvent<V>(this, data));
	}

	// Return the text.
	public String toString() {
		return text;
	}

	// Get the data.
	public V getData() {
		return data;
	}

	@Override
	public void update(int delta) {
	}

	@Override
	public void render(Graphics g) {
		// Draw the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		// Draw the text
		g.setColor(textColor);
		g.setFont(font);
		if (textAlignment == LEFT)
			g.drawString(text, x1 + textXOffset,
					centerY - font.getLineHeight() / 2);
		else if (textAlignment == CENTER)
			g.drawString(text, centerX - g.getFont().getWidth(text) / 2 + textXOffset,
					centerY - font.getLineHeight() / 2);
		else
			g.drawString(text, x2 - g.getFont().getWidth(text) + textXOffset,
					centerY - font.getLineHeight() / 2);

		// Highlight.
		if (highlight) {
			g.setColor(highlightColor);
			g.drawRect(x1, y1, width, height);
		}
		// Border.
		else {
			g.drawRect(x1, y1, width, height);
		}
	}

	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x + width;
		centerX = x1 + width / 2;
	}

	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		y2 = y + height;
		centerY = y1 + height / 2;
	}

	@Override
	public int getY() {
		return y1;
	}

	@Override
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
		centerX = x1 + width / 2;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setHeight(int h) {
		height = h;
		y2 = y1 + height;
		centerY = y1 + height / 2;
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
		if (button == Input.MOUSE_LEFT_BUTTON)
			press();
	}

	@Override
	public void mouseDown(int x, int y, int button) {
	}

	@Override
	public void hover(int x, int y) {
		highlight = true;
	}

	@Override
	public void clickedElsewhere(int button) {
	}

	@Override
	public void mouseDownElsewhere(int button) {
	}

	@Override
	public void hoveredElsewhere() {
		highlight = false;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void keyPressed(int key, char c) {
		if (key == buttonKey)
			press();
	}

	@Override
	public void keyInputDone() {
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

	@Override
	public boolean dead() {
		return false;
	}

	@Override
	public void destroy() {
		listeners.clear();
		listeners = null;
	}

	

}