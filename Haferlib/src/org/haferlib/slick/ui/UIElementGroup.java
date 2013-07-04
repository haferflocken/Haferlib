//An instance of this contains pointers to some number of UIElements and calls their methods when this instance's methods are called.
//This doesn't really have a shape, so setX, setY, getX, and getY are meaningless.
//The elements in this are not necessarily ordered by their depth and might not be rendered at their depth if render is called.

package org.haferlib.slick.ui;

import org.newdawn.slick.Graphics;

public class UIElementGroup implements UIElement {

	private UIElement[] elements;
	//Reference coordiate of the group. Typically the top left corner of the bouning box.
	private int x;
	private int y;

	//Constructors
	private UIElementGroup() {}

	public UIElementGroup(UIElement[] es, int x, int y) {
		elements = es;
		this.x = x;
		this.y = y;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//Draws the entire group.
	public void render(Graphics g) {
		for (UIElement e : elements) {
			e.render(g);
		}
	}

	//setX and setY should move all the elements in the group. getX and getY return the point all the elements in the group are moved relative to.
	public void setX(int x) {
		int dX;
		for (UIElement e : elements) {
			dX = e.getX() - this.x;
			e.setX(x + dX);
		}
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		int dY;
		for (UIElement e : elements) {
			dY = e.getY() - this.y;
			e.setY(y + dY);
		}
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	//Checks if a point is contained within this UIElement.
	public boolean pointIsWithin(int x, int y) {
		for (UIElement e : elements) {
			if (e.pointIsWithin(x, y))
				return true;
		}
		return false;
	}

	//Called when this is clicked on.
	public void click(int x, int y) {
		for (UIElement e : elements) {
			if (e.pointIsWithin(x, y))
				e.click(x, y);
			else
				e.clickedElsewhere();
		}
	}

	//Called when the mouse is hovering over, but not clicking this element.
	public void hover(int x, int y) {
		for (UIElement e : elements) {
			if (e.pointIsWithin(x, y))
				e.hover(x, y);
			else
				e.hoveredElsewhere();
		}
	}

	//Called when a click happens that doesn't click this.
	public void clickedElsewhere() {
		for (UIElement e : elements) {
			e.clickedElsewhere();
		}
	}

	//Called when the mouse is not hovering over this element and not clicking
	public void hoveredElsewhere() {
		for (UIElement e : elements) {
			e.hoveredElsewhere();
		}
	}

	//Get the depth of this element. Elements are drawn in an order from lowest depth to highest depth.
	public int getDepth() {
		return Integer.MAX_VALUE;
	}

	//Called when a key press happens.
	public void keyPressed(int key, char c) {
		for (UIElement e : elements) {
			e.keyPressed(key, c);
		}
	}

	//Called when all key inputs have been sent for this update, if there were any.
	public void keyInputDone() {
		for (UIElement e : elements) {
			e.keyInputDone();
		}
	}

	//@see UIElement.dead()
	public boolean dead() {
		//Groups just don't die. They have to be manually cleaned up.
		return false;
	}

}