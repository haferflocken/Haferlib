//An interface for anything that can be drawn as a UI element.
//Coordinates for all parameters are always screen coordinates.

package org.haferlib.slick.gui;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

public abstract class GUISubcontext implements GUIElement {

	protected int x1, y1;
	protected GUIContext subcontext;
	protected boolean lmbPressed, mmbPressed, rmbPressed;
	protected boolean lmbDown, mmbDown, rmbDown;
	protected int mouseX, mouseY;
	private Rectangle gClip;

	// Constructors.
	protected GUISubcontext() {
		this(0, 0);
	}

	protected GUISubcontext(int x, int y) {
		subcontext = new GUIContext();
		x1 = x;
		y1 = y;
		gClip = new Rectangle(0, 0, 0, 0);
	}
	
	// EFFECTS:  Get the elements in the subcontext.
	public ArrayList<GUIElement> getElements() {
		return subcontext.getElements();
	}
	
	// EFFECTS:  See if the subcontext contains an element.
	public boolean contains(GUIElement e) {
		return subcontext.contains(e);
	}
	
	// REQUIRES: g != null
	// EFFECTS:  Render the subcontext within the already defined clip area. Returns g.getClip()
	//			 to its state when this method was called by the end so as not to interfere with
	//			 other rendering.
	protected void renderSubcontext(Graphics g, int leftX, int topY, int rightX, int bottomY) {
		// Copy the old clipping.
		gClip.setBounds(g.getClip());
		
		// Only make the clip area smaller. Don't increase its size.
		if (leftX < gClip.getX())
			leftX = (int)gClip.getX();
		
		if (topY < gClip.getY())
			topY = (int)gClip.getY();
		
		if (rightX > gClip.getX() + gClip.getWidth())
			rightX = (int)(gClip.getX() + gClip.getWidth());
		
		if (bottomY > gClip.getY() + gClip.getHeight());
			bottomY = (int)(gClip.getY() + gClip.getHeight());
		
		// Render the subcontext.
		subcontext.render(g, leftX, topY, rightX, bottomY);
		
		// Set the clip to what it was.
		g.setClip(gClip);
	}

	// Update the subcontext.
	@Override
	public void update(int delta) {
		subcontext.update(mouseX, mouseY, lmbPressed, mmbPressed, rmbPressed, lmbDown, mmbDown, rmbDown, delta);
		lmbPressed = false;
		mmbPressed = false;
		rmbPressed = false;
		lmbDown = false;
		mmbDown = false;
		rmbDown = false;
	}
	
	@Override
	public void setX(int x) {
		int dX = x - x1;
		x1 = x;
		subcontext.translateX(dX);
	}

	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		int dY = y - y1;
		y1 = y;
		subcontext.translateY(dY);
	}

	@Override
	public int getY() {
		return y1;
	}

	// Track mouse clicks.
	@Override
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

	// Track mouseDowns.
	@Override
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

	// Track mouse position.
	@Override
	public void hover(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	// Track clicks and mouse position.
	@Override
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

	// Track mouseDowns and mouse position.
	@Override
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

	// Track mouse position.
	@Override
	public void hoveredElsewhere() {
		mouseX = Integer.MIN_VALUE;
		mouseY = Integer.MIN_VALUE;
	}

	// Tell the subcontext about key presses.
	@Override
	public void keyPressed(int key, char c) {
		subcontext.keyPressed(key, c);
	}

	@Override
	public void keyInputDone() {
	}
	
	// Destroy the subcontext.
	@Override
	public void destroy() {
		subcontext.destroy();
	}

}