package org.haferlib.slick.gui;

import org.newdawn.slick.Graphics;

/**
 * An interface for an element of a user interface.
 * All coordinate parameters are always screen coordinates.
 * 
 * @author John Werner
 * 
 */

public interface GUIElement {

	/**
	 * Run any per-frame logic that the element needs to run.
	 * 
	 * @param delta The time, in miliseconds, since the last call to update.
	 */
	void update(int delta);

	/**
	 * Draw this element.
	 * 
	 * @param g The graphics to draw with.
	 */
	void render(Graphics g);

	/**
	 * Set the left x coordinate of the element's bounding box.
	 * 
	 * @param x The coordinate to set the bounding box's x to.
	 */
	void setX(int x);

	/**
	 * Get the left x coordinate of the element's bounding box.
	 * 
	 * @return The left x coordinate of the element's bounding box.
	 */
	int getX();

	/**
	 * Sets the top y coordinate of the element's bounding box.
	 * 
	 * @param y The coordinate to set the bounding box's y to.
	 */
	void setY(int y);

	/**
	 * Get the top y coordinate of the element's bounding box.
	 * 
	 * @return The top y coordinate of the element's bounding box.
	 */
	int getY();

	/**
	 * Set the width of the bounding box containing this element.
	 * In the case of elements that can resize themselves, this should set the maximum
	 * width of the element.
	 *  
	 * @param w The width to set.
	 */
	void setWidth(int w);

	/**
	 * Get the current width of the element's bounding box.
	 * 
	 * @return The current width of the element's bounding box.
	 */
	int getWidth();

	/**
	 * Set the height of the bounding box containing this element.
	 * In the case of elements that can resize themselves, this should set the maximum
	 * height of the element.
	 * 
	 * @param h The height to set.
	 */
	void setHeight(int h);

	/**
	 * Get the current height of the element's bounding box.
	 * 
	 * @return The current height of the element's bounding box.
	 */
	int getHeight();

	/**
	 * Checks if a point is within this element.
	 * 
	 * @param x The x coord of the point to check.
	 * @param y The y coord of the point to check.
	 * @return true if the given (x, y) is within this element, false otherwise.
	 */
	boolean pointIsWithin(int x, int y);

	/**
	 * Called by the GUIContext when this element is clicked on.
	 * 
	 * @param x The mouse x.
	 * @param y The mouse y.
	 * @param button The button that was clicked.
	 */
	void click(int x, int y, int button);

	/**
	 * Called by the GUIContext when the mouse is down on this element.
	 * 
	 * @param x The mouse x.
	 * @param y The mouse y.
	 * @param button The button that is down.
	 */
	void mouseDown(int x, int y, int button);

	/**
	 * Called by the GUIContext when the mouse is hovering over this element.
	 * 
	 * @param x The mouse x.
	 * @param y The mouse y.
	 */
	void hover(int x, int y);

	/**
	 * Called by the GUIContext when another element is clicked.
	 * 
	 * @param target Which element was clicked.
	 * @param button The button that was clicked.
	 */
	void clickedElsewhere(GUIElement target, int button);

	/**
	 * Called by the GUIContext when the mouse is down on another element.
	 * 
	 * @param target The element that the mouse is down on.
	 * @param button The button that is down.
	 */
	void mouseDownElsewhere(GUIElement target, int button);

	/**
	 * Called by the GUIContext when the mouse hovers over another element.
	 * 
	 * @param target The element that is being hovered over.
	 */
	void hoveredElsewhere(GUIElement target);
	
	/**
	 * Set the depth of the element that it appears in the GUIContext.
	 * 
	 * @param d The depth to set.
	 */
	void setDepth(int d);

	/**
	 * Get the current depth of the element.
	 * 
	 * @return The depth of the element.
	 */
	int getDepth();

	/**
	 * Called by the GUIContext when a key is pressed and this element is "in focus."
	 * 
	 * @param key The key that was pressed.
	 * @param c The character that the key represents.
	 */
	void keyPressed(int key, char c);

	/**
	 * Called by the GUIContext when all keyPressed() calls have been made to this element for this frame.
	 */
	void keyInputDone();

	/**
	 * A getter to see if the element is dead or not.
	 * Dead elements are removed from their GUIContext and destroy is called on them.
	 * 
	 * @return Is the element dead?
	 */
	boolean dead();

	/**
	 * Release all resources. Called by the GUIContext if dead() is true.
	 */
	void destroy();

}