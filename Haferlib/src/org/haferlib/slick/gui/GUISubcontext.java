//An interface for anything that can be drawn as a UI element.
//Coordinates for all parameters are always screen coordinates.

package org.haferlib.slick.gui;

import java.util.ArrayList;

import org.newdawn.slick.Input;

public abstract class GUISubcontext implements GUIElement {

	protected int x1, y1;
	protected GUIContext subcontext;
	protected boolean lmbPressed, mmbPressed, rmbPressed;
	protected boolean lmbDown, mmbDown, rmbDown;
	protected int mouseX, mouseY;

	protected GUISubcontext() {
		this(0, 0);
	}

	protected GUISubcontext(int x, int y) {
		subcontext = new GUIContext();
		x1 = x;
		y1 = y;
	}
	
	//Get the elements in the subcontext.
	public ArrayList<GUIElement> getElements() {
		return subcontext.getElements();
	}
	
	//See if the subcontext contains an element.
	public boolean contains(GUIElement e) {
		return subcontext.contains(e);
	}

	//Called every frame so that the element can think or update sprites
	public void update(int delta) {
		subcontext.update(mouseX, mouseY, lmbPressed, mmbPressed, rmbPressed, lmbDown, mmbDown, rmbDown, delta);
		lmbPressed = false;
		mmbPressed = false;
		rmbPressed = false;
		lmbDown = false;
		mmbDown = false;
		rmbDown = false;
	}
	
	//If setX(n) is called, getX() should return n. setX(n) is expected to move the element so that its
	//bounding box's left edge is on n.
	public void setX(int x) {
		int dX = x - x1;
		x1 = x;
		subcontext.translateX(dX);
	}

	public int getX() {
		return x1;
	}

	//If setY(n) is called, getY() should return n. setY(n) is expected to move the element so that its
	//bounding box's top edge is on n.
	public void setY(int y) {
		int dY = y - y1;
		y1 = y;
		subcontext.translateY(dY);
	}

	public int getY() {
		return y1;
	}

	//Called when this is clicked on.
	public void click(int x, int y, int button) {
		mouseX = x;
		mouseY = y;
		if (button == Input.MOUSE_LEFT_BUTTON)
			lmbPressed = true;
		else if (button == Input.MOUSE_MIDDLE_BUTTON)
			mmbPressed = true;
		else
			rmbPressed = true;
	}

	//Called when a mouse button is down on this.
	public void mouseDown(int x, int y, int button) {
		mouseX = x;
		mouseY = y;
		if (button == Input.MOUSE_LEFT_BUTTON)
			lmbDown = true;
		else if (button == Input.MOUSE_MIDDLE_BUTTON)
			mmbDown = true;
		else
			rmbDown = true;
	}

	//Called when the mouse is hovering over, but not clicking this element.
	public void hover(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	//Called when a click happens that doesn't click this.
	public void clickedElsewhere(int button) {
		mouseX = Integer.MIN_VALUE;
		mouseY = Integer.MIN_VALUE;
		if (button == Input.MOUSE_LEFT_BUTTON)
			lmbPressed = true;
		else if (button == Input.MOUSE_MIDDLE_BUTTON)
			mmbPressed = true;
		else
			rmbPressed = true;
	}

	//Called when a mouse down happens that isn't over this.
	public void mouseDownElsewhere(int button) {
		mouseX = Integer.MIN_VALUE;
		mouseY = Integer.MIN_VALUE;
		if (button == Input.MOUSE_LEFT_BUTTON)
			lmbDown = true;
		else if (button == Input.MOUSE_MIDDLE_BUTTON)
			mmbDown = true;
		else
			rmbDown = true;
	}

	//Called when the mouse is not hovering over this element and not clicking
	public void hoveredElsewhere() {
		mouseX = Integer.MIN_VALUE;
		mouseY = Integer.MIN_VALUE;
	}

	//Called when a key press happens.
	public void keyPressed(int key, char c) {
		subcontext.keyPressed(key, c);
	}

	//Called when all key inputs have been sent for this update, if there were any.
	public void keyInputDone() {
	}
	
	//Called when this is removed from being dead.
	public void destroy() {
		subcontext.destroy();
	}

}