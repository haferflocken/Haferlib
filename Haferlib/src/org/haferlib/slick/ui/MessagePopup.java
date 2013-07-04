//A message that goes away when clicked

package org.haferlib.slick.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

public class MessagePopup implements UIElement {

	private String text;
	private Color textColor;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int centerX;
	private int centerY;
	private int width;
	private int height;
	private Color backgroundColor;
	private boolean dead;

	//Constructors
	private MessagePopup() {}

	public MessagePopup(String t, Color tColor, int x, int y, int w, int h, Color bgColor) {
		text = t;
		textColor = tColor;
		width = w;
		height = h;
		setX(x);
		setY(y);
		backgroundColor = bgColor;
		dead = false;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics)
	public void render(Graphics g) {
		//Draw the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		//Draw the text
		g.setColor(textColor);
		g.drawString(text, centerX - g.getFont().getWidth(text)/2, centerY - g.getFont().getLineHeight()/2);

		//Border
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
		dead = true;
	}

	//@see UIElement.hover(int x, int y)
	public void hover(int x, int y) {
	}

	//@see UIElement.clickedElsewhere()
	public void clickedElsewhere() {
	}

	//@see UIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
	}

	//@see UIElement.getDepth()
	public int getDepth() {
		return Integer.MAX_VALUE;
	}

	//@see UIElement.keyInputDone()
	public void keyPressed(int key, char c) {
		dead = true;
	}

	//@see UIElement.keyInputDone()
	public void keyInputDone() {
	}

	//@see UIElement.dead()
	public boolean dead() {
		return dead;
	}

}