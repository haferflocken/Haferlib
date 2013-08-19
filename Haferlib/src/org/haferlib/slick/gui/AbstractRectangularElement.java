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
	 * Constructor. This does not call the set methods so that way any initialization that
	 * subclasses need to do can be done and their setters don't need to have != null checks.
	 * 
	 * @param x The initial x.
	 * @param y The initial y.
	 * @param width The initial width.
	 * @param height The initial height.
	 * @param depth The initial depth.
	 */
	public AbstractRectangularElement(int x, int y, int width, int height, int depth) {
		x1 = x;
		y1 = y;
		x2 = x1 + width;
		y2 = y1 + height;
		this.width = width;
		this.height = height;
		this.depth = depth;
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
