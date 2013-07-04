//An interface for anything that can be drawn as a UI element.
//Coordinates for all parameters are always screen coordinates.

package org.haferlib.slick.ui;

import org.newdawn.slick.Graphics;

public interface UIElement {

	//Called every frame so that the element can think or update sprites
	void update(int delta);

	//Draws this using the Graphics g.
	void render(Graphics g);

	//If setX(n) is called, getX() should return n. setX(n) is expected to move the element so that its
	//bounding box's left edge is on n.
	void setX(int x);

	int getX();

	//If setY(n) is called, getY() should return n. setY(n) is expected to move the element so that its
	//bounding box's top edge is on n.
	void setY(int y);

	int getY();

	//Return the width of the bounding box containing this element
	int getWidth();

	//Return the height of the bounding box containing this element
	int getHeight();

	//Checks if a point is contained within this UIElement.
	boolean pointIsWithin(int x, int y);

	//Called when this is clicked on.
	void click(int x, int y);

	//Called when the mouse is hovering over, but not clicking this element.
	void hover(int x, int y);

	//Called when a click happens that doesn't click this.
	void clickedElsewhere();

	//Called when the mouse is not hovering over this element and not clicking
	void hoveredElsewhere();

	//Get the depth of this element. Elements are drawn in an order from lowest depth to highest depth.
	int getDepth();

	//Called when a key press happens.
	void keyPressed(int key, char c);

	//Called when all key inputs have been sent for this update, if there were any.
	void keyInputDone();

	//A check to see if this element has decided to die
	boolean dead();

}