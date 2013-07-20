//A frame that displays an image, stretched to fit the frame.

package org.haferlib.slick.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ImageFrame implements GUIElement {
	
	private int x1, y1, x2, y2;	//The position of the frame.
	private int width, height;	//The dimensions of the frame.
	private int depth;			//The depth.
	private Image image;		//The image in the frame.
	
	//Constructor.
	public ImageFrame(Image image, int x, int y, int width, int height, int depth) {
		this.image = image;
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		this.depth = depth;
	}
	
	//Get the image.
	public Image getImage() {
		return image;
	}

	@Override
	public void update(int delta) { }

	@Override
	public void render(Graphics g) {
		g.drawImage(image, x1, y1, x2, y2, 0, 0, width, height);
		//g.setColor(org.newdawn.slick.Color.red);
		//g.drawRect(x1, y1, width, height);
	}

	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x1 + width;
	}

	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		y2 = y1 + height;
	}

	@Override
	public int getY() {
		return y1;
	}

	@Override
	public void setWidth(int w) {
		if (w > image.getWidth())
			w = image.getWidth();
		width = w;
		x2 = x1 + width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setHeight(int h) {
		if (h > image.getHeight())
			h = image.getHeight();
		height = h;
		y2 = y1 + height;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}

	@Override
	public void click(int x, int y, int button) { }

	@Override
	public void mouseDown(int x, int y, int button) { }

	@Override
	public void hover(int x, int y) { }

	@Override
	public void clickedElsewhere(int button) { }

	@Override
	public void mouseDownElsewhere(int button) { }

	@Override
	public void hoveredElsewhere() { }

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void keyPressed(int key, char c) { }

	@Override
	public void keyInputDone() { }

	@Override
	public boolean dead() {
		return false;
	}

	@Override
	public void destroy() { }

}
