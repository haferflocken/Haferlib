//A button, that when pushed, does something.

package org.haferlib.slick.ui;

import java.util.HashSet;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

public class Button<V> implements UIElement, UIEventGenerator {

	public static final byte LEFT = 0;
	public static final byte CENTER = 1;
	public static final byte RIGHT = 2;

	private String text;
	private V data;
	private Color textColor;
	private byte textAlignment;
	private int textXOffset;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int centerX;
	private int centerY;
	private int width;
	private int height;
	private Color backgroundColor;
	private boolean highlight;
	private Color highlightColor;
	private int buttonKey;
	private HashSet<UIEventListener> listeners;

	//Constructors
	private Button() {}

	public Button(String t, Color tColor, int x, int y, int w, int h, Color bgColor, Color hColor, int key) {
		this(t, null, tColor, CENTER, 0, x, y, w, h, bgColor, hColor, key);
	}

	public Button(String t, Color tColor, byte tAlign, int tXOffset, int x, int y, int w, int h, Color bgColor, Color hColor, int key) {
		this(t, null, tColor, tAlign, tXOffset, x, y, w, h, bgColor, hColor, key);
	}

	public Button(String t, V d, Color tColor, int x, int y, int w, int h, Color bgColor, Color hColor, int key) {
		this(t, d, tColor, CENTER, 0, x, y, w, h, bgColor, hColor, key);
	}

	public Button(String t, V d, Color tColor, byte tAlign, int tXOffset, int x, int y, int w, int h, Color bgColor, Color hColor, int key) {
		text = t;
		data = d;
		textColor = tColor;
		textAlignment = tAlign;
		textXOffset = tXOffset;
		width = w;
		height = h;
		setX(x);
		setY(y);
		backgroundColor = bgColor;
		highlight = false;
		highlightColor = hColor;
		listeners = new HashSet<UIEventListener>();
		buttonKey = key;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics g)
	public void render(Graphics g) {
		//Draw the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		//Draw the text
		g.setColor(textColor);
		if (textAlignment == LEFT)
			g.drawString(text, x1 + textXOffset, centerY - g.getFont().getLineHeight()/2);
		else if (textAlignment == CENTER)
			g.drawString(text, centerX - g.getFont().getWidth(text)/2 + textXOffset, centerY - g.getFont().getLineHeight()/2);
		else
			g.drawString(text, x2 - g.getFont().getWidth(text) + textXOffset, centerY - g.getFont().getLineHeight()/2);

		//Highlight
		if (highlight) {
			g.setColor(highlightColor);
			g.drawRect(x1, y1, width, height);
		}
		//Border
		else {
			g.drawRect(x1, y1, width, height);
		}
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

	//@see UIElement.getWidth()
	public int getWidth() {
		return width;
	}

	//@see UIElement.getHeight()
	public int getHeight() {
		return height;
	}

	//@see UIElement.pointIsWithin(int x, int y)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	//@see UIElement.click(int x, int y)
	public void click(int x, int y) {
		press();
	}

	//@see UIElement.hover(int x, int y)
	public void hover(int x, int y) {
		highlight = true;
	}

	//@see UIElement.clickedElsewhere()
	public void clickedElsewhere() {
	}

	//@see UIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
		highlight = false;
	}

	//@see UIElement.getDepth()
	public int getDepth() {
		return 2;
	}

	//@see UIElement.keyInputDone()
	public void keyPressed(int key, char c) {
		if (key == buttonKey)
			press();
	}

	//@see UIElement.keyInputDone()
	public void keyInputDone() {
	}

	//Press this button
	public void press() {
		for (UIEventListener l : listeners)
			l.uiEvent(new UIEvent(this));
	}

	//Adds a button listener to be notified when this button is clicked
	public void addListener(UIEventListener l) {
		listeners.add(l);
	}

	//Remove a button listener from this button
	public void removeListener(UIEventListener l) {
		listeners.remove(l);
	}

	//@see UIElement.dead()
	public boolean dead() {
		return false;
	}

	public String toString() {
		return text;
	}

	public V getData() {
		return data;
	}

}