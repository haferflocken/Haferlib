package org.haferslick.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

/**
 * A GUISubcontext is a rectangular GUIElement that can contain other GUIElements in an internal GUIContext.
 * 
 * @author John Werner
 *
 */

public abstract class GUISubcontext extends AbstractRectangularElement {

	protected GUIContext subcontext;
	protected boolean lmbPressed, mmbPressed, rmbPressed;
	protected boolean lmbDown, mmbDown, rmbDown;
	protected int mouseX, mouseY;
	private Rectangle gClip;

	/**
	 * Make a GUISubcontext. A note: if you add anything to the subcontext, be sure to call its
	 * addAndRemoveElements() method at the end of the constructor so that anything done to this
	 * before a call to update() affects the elements in the subcontext as well.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param depth
	 */
	protected GUISubcontext(int x, int y, int width, int height, int depth) {
		super(x, y, width, height, depth);
		subcontext = new GUIContext();
		gClip = new Rectangle(0, 0, 0, 0);
	}
	
	// EFFECTS:  Get the elements in the subcontext.
	public List<GUIElement> getElements() {
		return subcontext.getElements();
	}
	
	// EFFECTS:  See if the subcontext contains an element.
	public boolean contains(GUIElement e) {
		return subcontext.contains(e);
	}
	
	// EFFECTS:  Get the element immediately above the element given,
	//			 or null if one could not be found.
	public GUIElement getElementAbove(GUIElement e) {
		return getElementAbove(e.getY());
	}

	// EFFECTS:  Get the element immediately above the given y coordinate,
	//			 or null if one could not be found.
	public GUIElement getElementAbove(int y) {
		GUIElement out = null;
		int dY = Integer.MAX_VALUE;

		// Loop through the elements to find the one closest to the y.
		for (GUIElement e : subcontext.getElements()) {
			int newDY = y - e.getY();
			if (newDY < dY && newDY > 0) {
				dY = newDY;
				out = e;
			}
		}

		return out;
	}

	// EFFECTS:  Get the element immediately below the element given,
	//			 or null if one could not be found.
	public GUIElement getElementBelow(GUIElement e) {
		return getElementBelow(e.getY());
	}

	// EFFECTS:  Get the element immediately below the given y coordinate,
	//			 or null if one could not be found.
	public GUIElement getElementBelow(int y) {
		// Get a copy of the elements list sorted by Y.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementYComparator());

		// Loop through the elements backwards until we find an element that is above the y.
		for (int i = elements.size() - 2; i > -1; i--) {
			if (elements.get(i).getY() < y)
				return elements.get(i + 1);
		}

		// Return null if we can't find anything below the y.
		return null;
	}
	
	// EFFECTS:  Get the element immediately to the left of the element
	//			 given, or null if one could not be found.
	public GUIElement getElementToLeft(GUIElement e) {
		return getElementToLeft(e.getX());
	}
	
	// EFFECTS:  Get the element immediately to the left of the given
	//			 x coordinate, or null if one could not be found.
	public GUIElement getElementToLeft(int x) {
		GUIElement out = null;
		int dX = Integer.MAX_VALUE;

		// Loop through the elements to find the one closest to the x.
		for (GUIElement e : subcontext.getElements()) {
			int newDX = x - e.getX();
			if (newDX < dX && newDX > 0) {
				dX = newDX;
				out = e;
			}
		}

		return out;
	}

	// EFFECTS:  Get the element that contains a point,
	//			 or null if one could not be found.
	public GUIElement getElementAtPoint(int x, int y) {
		return subcontext.getElementAtPoint(x, y);
	}
	
	// REQUIRES: g != null
	// EFFECTS:  Render the subcontext within the already defined clip area. Returns g.getClip()
	//			 to its state when this method was called by the end so as not to interfere with
	//			 other rendering.
	protected void renderSubcontext(Graphics g, int leftX, int topY, int rightX, int bottomY) {
		// Copy the old clipping.
		gClip.setBounds(g.getClip());
		
		// Only make the clip area smaller. Don't increase its size.
		int gClipLeftX = (int)gClip.getX();
		if (leftX < gClipLeftX)
			leftX = gClipLeftX;
		
		int gClipTopY = (int)gClip.getY();
		if (topY < gClipTopY)
			topY = gClipTopY;
		
		int gClipRightX = (int)(gClip.getX() + gClip.getWidth());
		if (rightX > gClipRightX)
			rightX = gClipRightX;
		
		int gClipBottomY = (int)(gClip.getY() + gClip.getHeight());
		if (bottomY > gClipBottomY)
			bottomY = gClipBottomY;
			
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
		subcontext.translateX(dX);
		super.setX(x);
	}

	@Override
	public void setY(int y) {
		int dY = y - y1;
		subcontext.translateY(dY);
		super.setY(y);
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
	public void clickedElsewhere(GUIElement target, int button) {
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
	public void mouseDownElsewhere(GUIElement target, int button) {
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
	public void hoveredElsewhere(GUIElement target) {
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
		subcontext = null;
	}

}