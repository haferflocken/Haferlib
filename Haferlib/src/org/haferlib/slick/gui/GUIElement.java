//An interface for anything that can be drawn as a UI element.
//Coordinates for all parameters are always screen coordinates.

package org.haferlib.slick.gui;

import org.newdawn.slick.Graphics;

public interface GUIElement {

	// Called every frame so that the element can think or update sprites.
	void update(int delta);

	// Draws this using the Graphics g.
	void render(Graphics g);

	// If setX(n) is called, getX() should return n. setX(n) is expected to move
	// the element so that its bounding box's left edge is on n.
	void setX(int x);

	int getX();

	// If setY(n) is called, getY() should return n. setY(n) is expected to move
	// the element so that its bounding box's top edge is on n.
	void setY(int y);

	int getY();

	// Set and return the width of the bounding box containing this element.
	void setWidth(int w);

	int getWidth();

	// Set and return the height of the bounding box containing this element.
	void setHeight(int h);

	int getHeight();

	// Checks if a point is contained within this UIElement.
	boolean pointIsWithin(int x, int y);

	// Called when this is clicked on.
	void click(int x, int y, int button);

	// Called when the mouse button is down after a click over this.
	void mouseDown(int x, int y, int button);

	// Called when the mouse is hovering over, but not clicking this element.
	void hover(int x, int y);

	// Called when a click happens that doesn't click this.
	void clickedElsewhere(GUIElement target, int button);

	// Called when the mouse is down somewhere else.
	void mouseDownElsewhere(GUIElement target, int button);

	// Called when the mouse is not hovering over this element and not clicking.
	void hoveredElsewhere(GUIElement target);
	
	// Set the depth of this element. Elements are drawn in an order from lowest
	// depth to highest depth.
	void setDepth(int d);

	// Get the depth of this element.
	int getDepth();

	// Called when a key press happens.
	void keyPressed(int key, char c);

	// Called when all key inputs have been sent for this update,
	// if there were any.
	void keyInputDone();

	// A check to see if this element has decided to die.
	boolean dead();

	// Force the element to release any resources it may have accumulated.
	// Called by GUIContext when removing dead elements.
	void destroy();

}