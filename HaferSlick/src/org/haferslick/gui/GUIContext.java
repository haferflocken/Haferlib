package org.haferslick.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

import org.newdawn.slick.KeyListener;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

/**
 * A collection of GUIElements that can be updated and rendered as a whole.
 * 
 * A note on the implementation: The elements are stored in an ArrayList. This is
 * because the list is iterated over both forward and backward. Forward for rendering,
 * backward for updating. This ensures that elements are properly given user input 
 * (the element you visually click is the one that gets clicked) and rendered by depth
 * (elements of lower depth are rendered first).
 * 
 * @author John Werner
 *
 */

public class GUIContext implements KeyListener {
	
	private static final byte DEBUG_NONE = 0;
	private static final byte DEBUG_FOCUS = 1;
	private static final byte DEBUG_CLIP = 2;
	private static final byte DEBUG_BOTH = 3;
	public static final byte NUM_DEBUG_MODES = 4;
	
	// The colors to draw the debug clips with.
	private static final Color CLIP_BORDER_COLOR = new Color(255, 255, 122, 100);
	private static final Color CLIP_FILL_COLOR = new Color(255, 255, 122, 10);

	// The debug drawing mode. This is static as that makes sure all contexts
	// are in the same debug mode (viewing just one context in debug mode is next to useless).
	public static byte debugMode = DEBUG_NONE;
	
	// A basic queue backed by a linked list that holds keys and chars in the links
	// rather than encapsulating them with them (like java.util.LinkedList).
	private static class KeyCharQueue {
		
		// A link in the list.
		private static class Link {
			private final int key;
			private final char c;
			private Link next;
			
			private Link(int key, char c, Link next) {
				this.key = key;
				this.c = c;
				setNext(next);
			}
			
			private void setNext(Link n) {
				next = n;
			}
		}
		
		// Instance fields.
		private Link head; // The head link of the list.
		private Link tail; // The tail link of the list.
		private int size; // The number of links in the list.
		
		// Constructor.
		private KeyCharQueue() {
			head = null;
			tail = null;
			size = 0;
		}
		
		// Add a pair.
		private void enqueue(int key, char c) {
			Link newLink = new Link(key, c, null);
			// If the queue is empty, this is now the head and the tail.
			if (size == 0) {
				head = newLink;
				tail = newLink;
			}
			// Otherwise, the new link is the new tail.
			else {
				tail.setNext(newLink);
				tail = newLink;
			}
			size++;
		}
		
		// Peek at the head key.
		private int peekKey() {
			return head.key;
		}
		
		// Peek at the head char.
		private char peekChar() {
			return head.c;
		}
		
		// Dequeue the front pair.
		private void dequeue() {
			head = head.next;
			if (head == null)
				tail = null;
			size--;
		}
		
		// Get the size of the queue.
		private int size() {
			return size;
		}
	}

	/////////////////////////
	//// INSTANCE FIELDS ////
	/////////////////////////
	private KeyCharQueue keyBuffer;							// The buffer of key presses that have occurred since the last update.
	private ArrayList<GUIElement> elements;					// The GUIElements in this GUIContext, sorted by depth.
	private ElementDepthComparator depthComparator;			// Compares elements by depth.
	private GUIElement clickFocus, hoverFocus;				// The element in focus for different actions.
	private int clickFocusBoxX, clickFocusBoxY;				// The box drawn around the click focus in debug mode.
	private int clickFocusBoxWidth, clickFocusBoxHeight;
	private int hoverFocusBoxX, hoverFocusBoxY;				// The box drawn around the hover focus in debug mode.
	private int hoverFocusBoxWidth, hoverFocusBoxHeight;
	private ArrayList<GUIElement> addThese;					// The elements to be added on the next update.
	private ArrayList<GUIElement> removeThese;				// The elements to be removed on the next update.
	private boolean enabled;								// If false, update does nothing and ignores key input.

	/**
	 * Create an empty GUIContext.
	 */
    public GUIContext() {
    	elements = new ArrayList<GUIElement>();
    	depthComparator = new ElementDepthComparator();
    	enabled = true;
    	keyBuffer = new KeyCharQueue();
    	addThese = new ArrayList<GUIElement>();
    	removeThese = new ArrayList<GUIElement>();
    	clickFocus = null;
    	hoverFocus = null;
    }

    /**
     * Update this context using user input and the time since the last frame.
     * 
     * @param input The input data.
     * @param delta The time since the last frame.
     */
	public void update(Input input, int delta) {
		update(input.getMouseX(), input.getMouseY(),
			input.isMousePressed(Input.MOUSE_LEFT_BUTTON), input.isMousePressed(Input.MOUSE_MIDDLE_BUTTON), input.isMousePressed(Input.MOUSE_RIGHT_BUTTON),
			input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON), input.isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON), input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON),
			delta);
	}

	/**
	 * Update this context using user input and the time since the last frame.
	 * 
	 * @param mouseX The x coordinate of the mouse.
	 * @param mouseY The y coordinate of the mouse.
	 * @param leftMousePressed Whether or not the left mouse is pressed.
	 * @param middleMousePressed Whether or not the middle mouse is pressed.
	 * @param rightMousePressed Whether or not the right mouse is pressed.
	 * @param leftMouseDown Whether or not the left mouse is down.
	 * @param middleMouseDown Whether or not the middle mouse is down.
	 * @param rightMouseDown Whether or not the right mouse is down.
	 * @param delta The time since the last frame.
	 */
	public void update(int mouseX, int mouseY, boolean leftMousePressed, boolean middleMousePressed, boolean rightMousePressed, boolean leftMouseDown, boolean middleMouseDown, boolean rightMouseDown, int delta) {
		// If we're just not running this thing, return.
		if (!enabled)
			return;

		// And and remove stuff as it has asked.
		addAndRemoveElements();

		// If the mouse was clicked or is down, clear the click focus. This allows for unfocusing by clicking nothing.
		if (leftMousePressed | middleMousePressed | rightMousePressed | leftMouseDown | middleMouseDown | rightMouseDown)
			clickFocus = null;
		
		// Clear the hover focus because it needs to be rethought every frame.
		hoverFocus = null;
		
		// Did certain events happen?
		boolean mousePressed = leftMousePressed | middleMousePressed | rightMousePressed;
		boolean mouseDown = leftMouseDown | middleMouseDown | rightMouseDown;
		
		// We only want to call events on the top element of a bunch of elements if they overlap,
		// so we keep track of if the event has been consumed yet. If not clicking/mouseDown,
		// the event is already consumed (this is so the loop can break early if it is done).
		boolean clickConsumed = !mousePressed;
		boolean mouseDownConsumed = !mouseDown;
		boolean hoverConsumed = false;
		
		// Loop through the elements to do click, mouseDown, and hover.
		GUIElement e;
		for (int i = elements.size() - 1; i > -1; i--) {
			e = elements.get(i);
			// If we are within the element's area...
			if (e.pointIsWithin(mouseX, mouseY)) {
				// If we are clicking...
				if (mousePressed) {
					// Click the element and make it the click focus.
					if (!clickConsumed) {
						clickElement(e, mouseX, mouseY, leftMousePressed, middleMousePressed, rightMousePressed);
						setClickFocus(e);
						clickConsumed = true;
					}
				}
				// If we have the mouse down...
				else if (mouseDown) {
					// MouseDown the element and make it the click focus.
					if (!mouseDownConsumed) {
						mouseDownElement(e, mouseX, mouseY, leftMouseDown, middleMouseDown, rightMouseDown);
						setClickFocus(e);
						mouseDownConsumed = true;
					}
				}
				// If we are hovering...
				else {
					// Hover the element and make it the hover focus.
					if (!hoverConsumed) {
						e.hover(mouseX, mouseY);
						setHoverFocus(e);
						hoverConsumed = true;
					}
				}
			}
			// Break if all events are consumed.
			if (clickConsumed && mouseDownConsumed && hoverConsumed)
				break;
		}
		
		// Loop through the elements to do clickedElsewhere, mouseDownElsewhere, and hoveredElsewhere.
		for (int i = elements.size() - 1; i > -1; i--) {
			e = elements.get(i);
			// If a click happened, clickedElsehwere this element if it is not the click focus.
			if (mousePressed) {
				if (e != clickFocus)
					clickedElementElsewhere(e, leftMousePressed, middleMousePressed, rightMousePressed);
			}
			// If the mouse is down, mouseDown this element if it is not the click focus.
			else if (mouseDown) {
				if (e != clickFocus)
					mouseDownElementElsewhere(e, leftMouseDown, middleMouseDown, rightMouseDown);
			}
			// If the mouse is hovering, hoveredElsewhere this element if it is not the hover focus.
			else {
				if (e != hoverFocus)
					e.hoveredElsewhere(hoverFocus);
			}
		}
		
		// Loop through the elements and update them.
		for (int i = elements.size() - 1; i > -1; i--) {
			e = elements.get(i);
			e.update(delta);
		}
		
		// Pass key input to the element in click focus.
		if (clickFocus != null) {
			// If any keys have been pressed...
			if (keyBuffer.size() > 0) {
				// Tell the focus of those key presses.
				while (keyBuffer.size() > 0) {
					clickFocus.keyPressed(keyBuffer.peekKey(), keyBuffer.peekChar());
					keyBuffer.dequeue();
				}
				clickFocus.keyInputDone();
			}
		}
		// If there is no element in click focus, clear the key buffer.
		else {
			while (keyBuffer.size() > 0)
				keyBuffer.dequeue();
		}
		
		// Remove and destroy dead elements.
		removeAndDestroyDeadElements();
		
		// See if we need to resort the elements and if we do, do it.
		for (int i = 1; i < elements.size(); i++) {
			// If the two elements are out of order, sort that shit.
			if (elements.get(i - 1).getDepth() > elements.get(i).getDepth()) {
				Collections.sort(elements, depthComparator);
				break;
			}
		}
	}

	/**
	 * Set the click focus to an element.
	 * 
	 * @param e The element to make the click focus.
	 */
	private void setClickFocus(GUIElement e) {
		clickFocus = e;
		clickFocusBoxX = clickFocus.getX();
		clickFocusBoxY = clickFocus.getY();
		clickFocusBoxWidth = clickFocus.getWidth();
		clickFocusBoxHeight = clickFocus.getHeight();
	}
	
	/**
	 * Set the hover focus to an element.
	 * 
	 * @param e The element to make the hover focus.
	 */
	private void setHoverFocus(GUIElement e) {
		hoverFocus = e;
		hoverFocusBoxX = hoverFocus.getX();
		hoverFocusBoxY = hoverFocus.getY();
		hoverFocusBoxWidth = hoverFocus.getWidth();
		hoverFocusBoxHeight = hoverFocus.getHeight();
	}

	/**
	 * Click an element.
	 * 
	 * @param e The element to click.
	 * @param mouseX The x position of the mouse.
	 * @param mouseY The y position of the mouse.
	 * @param lmbPressed Whether or not the left button is pressed.
	 * @param mmbPressed Whether or not the middle button is pressed.
	 * @param rmbPressed Whether or not the right button is pressed.
	 */
	private void clickElement(GUIElement e, int mouseX, int mouseY, boolean lmbPressed, boolean mmbPressed, boolean rmbPressed) {
		if (lmbPressed)
			e.click(mouseX, mouseY, Input.MOUSE_LEFT_BUTTON);
		if (mmbPressed)
			e.click(mouseX, mouseY, Input.MOUSE_MIDDLE_BUTTON);
		if (rmbPressed)
			e.click(mouseX, mouseY, Input.MOUSE_RIGHT_BUTTON);
	}
	
	/**
	 * ClickedElsewhere an element.
	 * 
	 * @param e The element to clickedElsewhere.
	 * @param lmbPressed Whether or not the left button is pressed.
	 * @param mmbPressed Whether or not the middle button is pressed.
	 * @param rmbPressed Whether or not the right button is pressed.
	 */
	private void clickedElementElsewhere(GUIElement e, boolean lmbPressed, boolean mmbPressed, boolean rmbPressed) {
		if (lmbPressed)
			e.clickedElsewhere(clickFocus, Input.MOUSE_LEFT_BUTTON);
		if (mmbPressed)
			e.clickedElsewhere(clickFocus, Input.MOUSE_MIDDLE_BUTTON);
		if (rmbPressed)
			e.clickedElsewhere(clickFocus, Input.MOUSE_RIGHT_BUTTON);
	}
	
	/**
	 * MouseDown an element.
	 * 
	 * @param e The element to mouseDown.
	 * @param mouseX The x position of the mouse.
	 * @param mouseY The y position of the mouse.
	 * @param lmbDown Whether or not the left button is down.
	 * @param mmbDown Whether or not the middle button is down.
	 * @param rmbDown Whether or not the right button is down.
	 */
	private void mouseDownElement(GUIElement e, int mouseX, int mouseY, boolean lmbDown, boolean mmbDown, boolean rmbDown) {
		if (lmbDown)
			e.mouseDown(mouseX, mouseY, Input.MOUSE_LEFT_BUTTON);
		if (mmbDown)
			e.mouseDown(mouseX, mouseY, Input.MOUSE_MIDDLE_BUTTON);
		if (rmbDown)
			e.mouseDown(mouseX, mouseY, Input.MOUSE_RIGHT_BUTTON);
	}
	
	/**
	 * MouseDownElsewhere an element.
	 * 
	 * @param e The element to mouseDownElsewhere.
	 * @param lmbDown Whether or not the left button is down.
	 * @param mmbDown Whether or not the middle button is down.
	 * @param rmbDown Whether or not the right button is down.
	 */
	private void mouseDownElementElsewhere(GUIElement e, boolean lmbDown, boolean mmbDown, boolean rmbDown) {
		if (lmbDown)
			e.mouseDownElsewhere(clickFocus, Input.MOUSE_LEFT_BUTTON);
		if (mmbDown)
			e.mouseDownElsewhere(clickFocus, Input.MOUSE_MIDDLE_BUTTON);
		if (rmbDown)
			e.mouseDownElsewhere(clickFocus, Input.MOUSE_RIGHT_BUTTON);
	}

	/**
	 * Add e to elements at the correct position.
	 * 
	 * @param e The element to add.
	 */
	private void addAtSortedLoc(GUIElement e) {
		// TODO Binary search and then insert, rather than linear insert.
		for (int i = 0; i < elements.size(); i++) {
			// If the element being added is of a lower depth than the one we are looking at, add it.
			if (e.getDepth() < elements.get(i).getDepth()) {
				elements.add(i, e);
				return;
			}
		}
		// If it is greater than everything in the list, add it to the end
		elements.add(e);
	}

	/**
	 * Add an element to this context.
	 * These will not be affected by any operations to this context
	 * until addAndRemoveElements() is called (and it is called automatically
	 * in update()).
	 * 
	 * @param e The element to add.
	 */
	public void addElement(GUIElement e) {
		addThese.add(e);
	}

	/**
	 * Remove an element from this context.
	 * These will not be affected by any operations to this context
	 * until addAndRemoveElements() is called (and it is called automatically
	 * in update()).
	 * 
	 * @param e The element to remove.
	 */
	public void removeElement(GUIElement e) {
		removeThese.add(e);
	}

	/**
	 * Take elements waiting to be added and removed and put them into the context.
	 * This is called automatically during update, but is useful for clients that 
	 * wish to ensure elements will be affected by translate calls before the call
	 * to update.
	 */
	public void addAndRemoveElements() {
		// Remove anything that wants to be removed. Removal is done first to make adding faster.
		if (removeThese.size() > 0) {
			elements.removeAll(removeThese);
			if (removeThese.contains(clickFocus))
				clickFocus = null;
			if (removeThese.contains(hoverFocus))
				hoverFocus = null;
			removeThese.clear();
		}

		// Add anything that wants to be added
		if (addThese.size() > 0) {
			for (GUIElement e : addThese)
				addAtSortedLoc(e);
			addThese.clear();
		}
	}

	/**
	 * Looks through the the context for dead elements, calling their
	 * destroy() method and removing them immediately from the context.
	 */
	public void removeAndDestroyDeadElements() {
		Iterator<GUIElement> iterator = elements.iterator();
		GUIElement e;
		while (iterator.hasNext()) {
			e = iterator.next();
			if (e.dead()) {
				if (e == clickFocus)
					clickFocus = null;
				if (e == hoverFocus)
					hoverFocus = null;
				e.destroy();
				iterator.remove();
			}
		}
	}
	
	/**
	 * Get the elements in this context.
	 * This directly returns the List backing this context! Be careful.
	 * 
	 * @return The elements in this context.
	 */
	public List<GUIElement> getElements() {
		return elements;
	}
	
	/**
	 * Get the number of elements in this context.
	 * 
	 * @return The number of elements in this context.
	 */
	public int getNumElements() {
		return elements.size();
	}

	/**
	 * Clear the elements from this context.
	 */
	public void clear() {
		clickFocus = null;
		hoverFocus = null;
		elements.clear();
		addThese.clear();
		removeThese.clear();
	}

	/**
	 * Draw this context with the given clipping area.
	 * 
	 * @param g The graphics to draw with.
	 * @param leftX The left x of the clip area.
	 * @param topY The top y of the clip area.
	 * @param rightX The right x of the clip area.
	 * @param bottomY The bottom y of the clip area.
	 */
	public void render(Graphics g, int leftX, int topY, int rightX, int bottomY) {
		// Render the elements that are in the render area.
		int clipWidth = rightX - leftX;
		int clipHeight = bottomY - topY;
		g.setClip(leftX, topY, clipWidth, clipHeight);
		for (GUIElement e : elements) {
			if (e.dead())
				continue;
			if (e.getX() + e.getWidth() >= leftX && e.getY() + e.getHeight() >= topY && e.getX() <= rightX && e.getY() <= bottomY)
				e.render(g);
		}
		g.clearClip();

		// Debug rendering.
		if (debugMode == DEBUG_FOCUS || debugMode == DEBUG_BOTH) {
			// Draw the click focus box.
			g.setLineWidth(2);
			if (clickFocus != null) {
				g.setColor(Color.red);
				g.drawRect(clickFocusBoxX, clickFocusBoxY, clickFocusBoxWidth, clickFocusBoxHeight);
			}
			// Draw the hover focus box.
			if (hoverFocus != null && hoverFocus != clickFocus) {
				g.setColor(Color.green);
				g.drawRect(hoverFocusBoxX, hoverFocusBoxY, hoverFocusBoxWidth, hoverFocusBoxHeight);
			}
			g.setLineWidth(1);
		}
		if (debugMode == DEBUG_CLIP || debugMode == DEBUG_BOTH) {
			// Draw the clip area.
			g.setColor(CLIP_FILL_COLOR);
			g.fillRect(leftX, topY, clipWidth, clipHeight);
			g.setColor(CLIP_BORDER_COLOR);
			g.drawRect(leftX, topY, clipWidth, clipHeight);
		}
	}

	/**
	 * Enable the context, allowing it to update and take key input.
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Disable the context, preventing it from updating or taking key input.
	 */
	public void disable() {
		enabled = false;
	}
	
	/**
	 * Get the topmost element that contains a point.
	 * 
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @return The topmost elemnet that contains the point (x, y). Return null if no element contains (x, y).
	 */
	public GUIElement getElementAtPoint(int x, int y) {
		GUIElement e;
		for (int i = elements.size() - 1; i > -1; i--) {
			e = elements.get(i);
			if (e.pointIsWithin(x, y))
				return e;
		}
		return null;
	}

	/**
	 * See if this context contains the given GUIElement.
	 * 
	 * @param e The element to look for.
	 * @return true if e is in the context, false otherwise.
	 */
	public boolean contains(GUIElement e) {
		return elements.contains(e);
	}

	/**
	 * See if any elements contain the given point.
	 * 
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @return true if an element contains the point, false otherwise.
	 */
	public boolean pointIsWithin(int x, int y) {
		for (GUIElement e : elements) {
			if (e.pointIsWithin(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Translate all the elements in this context along the x axis.
	 * 
	 * @param dX The amount to translate by.
	 */
	public void translateX(int dX) {
		for (GUIElement e : elements) {
			e.setX(e.getX() + dX);
		}
	}

	/**
	 * Translate all the elements in this context along the y axis.
	 * 
	 * @param dY The amount to translate by.
	 */
	public void translateY(int dY) {
		for (GUIElement e : elements) {
			e.setY(e.getY() + dY);
		}
	}

	/**
	 * Destroy all elements in the context and set the fields of this element to null
	 * so that they cannot be used. Attempting to call any methods of a GUIContext after
	 * calling this will most likely throw a NullPointerException. Behavior of all
	 * methods after calling this is undefined.
	 */
	public void destroy() {
		// Destroy the elements.
		addAndRemoveElements();
		for (GUIElement e : elements) {
			e.destroy();
		}
		elements.clear();
		elements = null;
		
		// Destroy the key buffer.
		while (keyBuffer.size() > 0)
			keyBuffer.dequeue();
		keyBuffer = null;
		
		// Clear other references.
		depthComparator = null;
		clickFocus = null;
		hoverFocus = null;
		enabled = false;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		keyBuffer.enqueue(key, c);
	}

	@Override
	public void keyReleased(int key, char c) {
	}

	@Override
	public void inputStarted() {
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public boolean isAcceptingInput() {
		return enabled;
	}

	@Override
	public void setInput(Input input) {
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}