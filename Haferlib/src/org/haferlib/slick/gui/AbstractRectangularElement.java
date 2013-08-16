package org.haferlib.slick.gui;

/**
 * An abstract implementation of a rectangular GUIElement to simplify everything else.
 * 
 * @author John Werner
 *
 */

public abstract class AbstractRectangularElement implements GUIElement {
	
	protected int x1, y1, x2, y2;			// Top left and bottom right coordinates.
	protected int width, height;			// Dimensions.
	protected int depth;					// The depth.
	protected boolean dead;					// Is this dead?
	
	/**
	 * Constructor.
	 * 
	 * @param x The initial x.
	 * @param y The initial y.
	 * @param width The initial width.
	 * @param height The initial height.
	 * @param depth The initial depth.
	 */
	public AbstractRectangularElement(int x, int y, int width, int height, int depth) {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setDepth(depth);
		dead = false;
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
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
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
	public boolean dead() {
		return dead;
	}

}
