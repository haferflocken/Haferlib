package org.haferlib.gdx.gui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

import org.haferlib.gdx.SmartInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;

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

public class GUIContext  {
	
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

	/////////////////////////
	//// INSTANCE FIELDS ////
	/////////////////////////
	private ArrayList<GUIElement> elements;					// The GUIElements in this GUIContext, sorted by depth.
	private ArrayDeque<GUIElement> loopStack;				// The stack used for looping to get into subelements.
	private ElementDepthComparator depthComparator;			// Compares elements by depth.
	private IntMap<GUIElement> pointerDownFocus;			// The elements that have been touched.
	private IntMap<GUIElement> pointerHoverFocus;			// The elements in hover focus.
	private int clickFocusBoxX, clickFocusBoxY;				// The box drawn around the click focus in debug mode.
	private int clickFocusBoxWidth, clickFocusBoxHeight;
	private int hoverFocusBoxX, hoverFocusBoxY;				// The box drawn around the hover focus in debug mode.
	private int hoverFocusBoxWidth, hoverFocusBoxHeight;
	private ArrayList<GUIElement> addThese;					// The elements to be added on the next update.
	private ArrayList<GUIElement> removeThese;				// The elements to be removed on the next update.
	private boolean enabled;								// If false, update does nothing and ignores key input.
	private Rectangle scissors;								// A rectangle for clipping.

	/**
	 * Create an empty GUIContext.
	 */
    public GUIContext() {
    	elements = new ArrayList<GUIElement>();
    	loopStack = new ArrayDeque<GUIElement>();
    	depthComparator = new ElementDepthComparator();
    	enabled = true;
    	addThese = new ArrayList<GUIElement>();
    	removeThese = new ArrayList<GUIElement>();
    	pointerDownFocus = new IntMap<GUIElement>();
    	pointerHoverFocus = new IntMap<GUIElement>();
    	scissors = new Rectangle();
    }

	/**
	 * Update this context using user input and the time since the last frame.
	 * 
	 * @param input The input to use when smartInput is insufficient.
	 * @param smartInput A smartInput that relies on input.
	 * @param delta The time since the last frame.
	 */
	public void update(Input input, SmartInput smartInput, int delta) {
		// If we're just not running this thing, return.
		if (!enabled)
			return;

		// And and remove stuff as it has asked.
		addAndRemoveElements();

		// If the mouse was clicked or is down, clear the click focus. This allows for unfocusing by clicking nothing.
		boolean pointerJustPressed = smartInput.isAnyPointerJustPressed();
		boolean pointerDown = smartInput.isAnyPointerDown();
		if (pointerJustPressed || pointerDown)
			pointerDownFocus.clear();
		
		// Clear the hover focus because it needs to be rethought every frame.
		pointerHoverFocus.clear();
		
		// Loop through the elements to do click, mouseDown, and hover.
		resetLoopStack(); // Get the loop stack ready.
		GUIElement e;
		while(!loopStack.isEmpty()) {
			e = loopStack.peek();
			// If the element has subelements, push them onto the stack on top of this element
			// and then just go to them.
			GUIElement[] subElements = e.getSubElements();
			if (subElements != null) {
				for (GUIElement s : subElements)
					loopStack.push(s);
				continue;
			}
			
			// Otherwise, pop this element off the stack and continue.
			loopStack.pop();
			
			// For each pointer...
			for (IntMap.Entry<SmartInput.Pointer> entry : smartInput.getPointers().entries()) {
				int pointerId = entry.key;
				SmartInput.Pointer pointer = entry.value;
				int pointerX = pointer.getX();
				int pointerY = pointer.getY();
				
				boolean justPressed = pointer.isAnyButtonJustPressed();
				boolean down = pointer.isAnyButtonDown();
				
				boolean justPressedConsumed = !justPressed;
				boolean downConsumed = !down;
				boolean hoverConsumed = false;
				
				// If we are within the element's area...
				if (e.pointIsWithin(pointerX, pointerY)) {
					// If we are justPressing...
					if (justPressed) {
						// If the event has not been consumed, justPressed the element and make it the click focus.
						if (!justPressedConsumed) {
							IntIntMap.Keys buttons = pointer.getButtons();
							while (buttons.hasNext()) {
								int button = buttons.next();
								if (pointer.isButtonJustPressed(button))
									e.pointerJustPressed(pointerX, pointerY, pointerId, button);
							}
							setClickFocus(e);
							justPressedConsumed = true;
						}
					}
					// If we have the mouse down...
					else if (pointerDown) {
						// MouseDown the element and make it the click focus.
						if (!downConsumed) {
							IntIntMap.Keys buttons = pointer.getButtons();
							while (buttons.hasNext()) {
								int button = buttons.next();
								if (pointer.isButtonDown(button))
									e.pointerDown(pointerX, pointerY, pointerId, button);
							}
							setClickFocus(e);
							downConsumed = true;
						}
					}
					// If we are hovering...
					else {
						// Hover the element and make it the hover focus.
						if (!hoverConsumed) {
							e.pointerHover(pointerX, pointerY, pointerId);
							setHoverFocus(e);
							hoverConsumed = true;
						}
					}
				}
			}
		}
		
		// Loop through the elements to do clickedElsewhere, mouseDownElsewhere, and hoveredElsewhere.
		resetLoopStack();
		while(!loopStack.isEmpty()) {
			e = loopStack.peek();
			// If the element has subelements, push them onto the stack on top of this element
			// and then just go to them.
			GUIElement[] subElements = e.getSubElements();
			if (subElements != null) {
				for (GUIElement s : subElements)
					loopStack.push(s);
				continue;
			}
			
			// Otherwise, pop this element off the stack and continue.
			loopStack.pop();
			
			// For each pointer...
			for (IntMap.Entry<SmartInput.Pointer> entry : smartInput.getPointers().entries()) {
				int pointerId = entry.key;
				SmartInput.Pointer pointer = entry.value;
							
				boolean justPressed = pointer.isAnyButtonJustPressed();
				boolean down = pointer.isAnyButtonDown();
				
				// If there was a justPressed, clickedElsehwere this element if it is not the click focus.
				if (justPressed) {
					if (e != clickFocus) {
						
						e.pointerJustPressedElsewhere();
					}
				}
				// If the mouse is down, mouseDown this element if it is not the click focus.
				else if (down) {
					if (e != clickFocus)
						pointerDownElementElsewhere(e, leftMouseDown, middleMouseDown, rightMouseDown);
				}
				// If the mouse is hovering, hoveredElsewhere this element if it is not the hover focus.
				else {
					if (e != hoverFocus)
						e.pointerHoveredElsewhere(hoverFocus);
				}
			}
		}
		
		// Loop through the elements and update them.
		resetLoopStack();
		while(!loopStack.isEmpty()) {
			e = loopStack.peek();
			// If the element has subelements, push them onto the stack on top of this element
			// and then just go to them.
			GUIElement[] subElements = e.getSubElements();
			if (subElements != null) {
				for (GUIElement s : subElements)
					loopStack.push(s);
				continue;
			}
			
			// Otherwise, pop this element off the stack and continue.
			loopStack.pop();
			
			e.update(delta);
		}
		
		// Pass key input to the element in click focus.
		if (clickFocus != null) {
			// If any keys have been pressed...
			if (keyBuffer.size > 0) {
				// Tell the focus of those key presses.
				while (keyBuffer.size > 0) {
					clickFocus.keyPressed(keyBuffer.pop());
				}
				clickFocus.keyInputDone();
			}
		}
		// If there is no element in click focus, clear the key buffer.
		else {
			keyBuffer.clear();
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
	 * Reset the loop stack so it is ready to be looped through.
	 */
	private void resetLoopStack() {
		loopStack.clear();
		for (int i = elements.size() - 1; i > -1; i--) {
			loopStack.add(elements.get(i));
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
			e.click(mouseX, mouseY, Input.Buttons.LEFT);
		if (mmbPressed)
			e.click(mouseX, mouseY, Input.Buttons.MIDDLE);
		if (rmbPressed)
			e.click(mouseX, mouseY, Input.Buttons.RIGHT);
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
			e.clickedElsewhere(clickFocus, Input.Buttons.LEFT);
		if (mmbPressed)
			e.clickedElsewhere(clickFocus, Input.Buttons.MIDDLE);
		if (rmbPressed)
			e.clickedElsewhere(clickFocus, Input.Buttons.RIGHT);
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
			e.mouseDown(mouseX, mouseY, Input.Buttons.LEFT);
		if (mmbDown)
			e.mouseDown(mouseX, mouseY, Input.Buttons.MIDDLE);
		if (rmbDown)
			e.mouseDown(mouseX, mouseY, Input.Buttons.RIGHT);
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
			e.mouseDownElsewhere(clickFocus, Input.Buttons.LEFT);
		if (mmbDown)
			e.mouseDownElsewhere(clickFocus, Input.Buttons.MIDDLE);
		if (rmbDown)
			e.mouseDownElsewhere(clickFocus, Input.Buttons.RIGHT);
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
	public void render(Camera camera, ShapeRenderer sR, float viewportX, float viewportY, float viewportWidth, float viewportHeight, Rectangle clipBounds) {
		// Render the elements that are in the render area.
		float leftX = clipBounds.getX();
		float topY = clipBounds.getY();
		float rightX = leftX + clipBounds.getWidth();
		float bottomY = topY + clipBounds.getHeight();
		
		ScissorStack.calculateScissors(camera, viewportX, viewportY, viewportWidth, viewportHeight, sR.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);

		for (GUIElement e : elements) {
			if (e.dead())
				continue;
			if (e.getX() + e.getWidth() >= leftX && e.getY() + e.getHeight() >= topY && e.getX() <= rightX && e.getY() <= bottomY)
				e.render(camera, sR, viewportX, viewportY, viewportWidth, viewportHeight);
		}
		
		ScissorStack.popScissors();

		// Debug rendering.
		if (debugMode == DEBUG_FOCUS || debugMode == DEBUG_BOTH) {
			sR.begin(ShapeType.Line);
			// Draw the click focus box.
			if (clickFocus != null) {
				sR.setColor(Color.RED);
				sR.rect(clickFocusBoxX, clickFocusBoxY, clickFocusBoxWidth, clickFocusBoxHeight);
			}
			// Draw the hover focus box.
			if (hoverFocus != null && hoverFocus != clickFocus) {
				sR.setColor(Color.GREEN);
				sR.rect(hoverFocusBoxX, hoverFocusBoxY, hoverFocusBoxWidth, hoverFocusBoxHeight);
			}
			sR.end();
		}
		if (debugMode == DEBUG_CLIP || debugMode == DEBUG_BOTH) {
			// Draw the clip area.
			sR.begin(ShapeType.Filled);
			sR.setColor(CLIP_FILL_COLOR);
			sR.rect(leftX, topY, clipBounds.getWidth(), clipBounds.getHeight());
			sR.end();
			sR.begin(ShapeType.Line);
			sR.setColor(CLIP_BORDER_COLOR);
			sR.rect(leftX, topY, clipBounds.getWidth(), clipBounds.getHeight());
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
		
		// Clear other references.
		keyBuffer = null;
		depthComparator = null;
		clickFocus = null;
		hoverFocus = null;
		enabled = false;
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}