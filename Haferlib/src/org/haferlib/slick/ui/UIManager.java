//Manages UI elements, calling their methods when appropriate.
//Keeps a buffer of key pressess and notifies UIElements of them if it is added to an input's keylisteners.
//Keeps track of which element has the input focus. Only the element with input focus receives key events.
//This shouldn't be used for static, unchanging things. It's for the UI that can be interacted with.

package org.haferlib.slick.ui;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

public class UIManager implements KeyListener {

	//Holds a pair of a key code and a char. Used by the key buffer.
	private class KeyCharPair {

		private int key;
		private char c;

		private KeyCharPair(int key, char c) {
			this.key = key;
			this.c = c;
		}
	}

	//The buffer of key presses that have occured since the last update.
	private Vector<KeyCharPair> keyBuffer;

	//The UIElements in this UIManager, sorted by depth
	private ArrayList<UIElement> elements;

	//The element in focus
	private UIElement focus;
	private int focusBoxX, focusBoxY, focusBoxWidth, focusBoxHeight;

	//The elements to be added and removed on the next update
	private ArrayList<UIElement> addThese;
	private ArrayList<UIElement> removeThese;

	//Is this UIManager listening to key input?
	private boolean enabled;

	//Constructors
    public UIManager() {
    	elements = new ArrayList<UIElement>();
    	enabled = true;
    	keyBuffer = new Vector<KeyCharPair>();
    	addThese = new ArrayList<UIElement>();
    	removeThese = new ArrayList<UIElement>();
    	focus = null;
    }

	//Run this UIManager.
	public void update(Input input, int delta) {
		//If we're just not running this thing
		if (!enabled)
			return;

		//And and remove stuff as it has asked
		addAndRemoveElements();

		//Get the mouse coordinates into an easier to read form.
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		//Get the mouse pressed state
		boolean mousePressed = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		//If the mouse was clicked, clear the focus. This allows for unfocusing by clicking nothing.
		if (mousePressed)
			clearFocus();
		//Loop through the elements and call their methods as they need to be called.
		//They are looped through backwards to ensure that for overlapping elements, the one clicked is
		//the one the player saw on top.
		UIElement e;
		for (int i = elements.size() - 1; i > -1; i--) {
			e = elements.get(i);
			//If we're within the element's area...
			if (e.pointIsWithin(mouseX, mouseY)) {
				//If we are clicking...
				if (mousePressed) {
					//Click the element
					e.click(mouseX, mouseY);
					//Make it the focus
					setFocus(e);
					//Only the first to be clicked on gets clicked. No overlapping click bullshit here.
					mousePressed = false;
				}
				//If we are hovering...
				else {
					e.hover(mouseX, mouseY);
				}
			}
			//If we're not within the area...
			else {
				//If we're clicking outside of this element...
				if (mousePressed) {
					e.clickedElsewhere();
				}
				//If we are hovering outside of this element...
				else {
					e.hoveredElsewhere();
				}
			}
			//Update the element after all the other stuff happens
			e.update(delta);
			//Check to see if the element is dead now, and if so, remove it
			if (e.dead())
				removeElement(e);
		}
		//Only the element with input focus gets key input.
		if (focus != null) {
			//If any keys have been pressed...
			if (keyBuffer.size() > 0) {
				//Tell the focus of those key presses
				for (KeyCharPair k : keyBuffer) {
					focus.keyPressed(k.key, k.c);
				}
				focus.keyInputDone();
				//Then check again if the focus is dead because new stuff happened to it.
				if (focus.dead())
					removeElement(focus);
			}
		}
		//Clear the key buffer now that the focus knows about it.
		keyBuffer.clear();
	}

	private void setFocus(UIElement e) {
		focus = e;
		focusBoxX = focus.getX() - 5;
		focusBoxY = focus.getY() - 5;
		focusBoxWidth = focus.getWidth() + 10;
		focusBoxHeight = focus.getHeight() + 10;
	}

	private void clearFocus() {
		focus = null;
	}

	private void addAtSortedLoc(UIElement e) {
		for (int i = 0; i < elements.size(); i++) {
			//If the element being added is of a lower depth than the one we are looking at, add it.
			if (e.getDepth() < elements.get(i).getDepth()) {
				elements.add(i, e);
				return;
			}
		}
		//If it is greater than everything in the list, add it to the end
		elements.add(e);
	}

	//Adds an element to this manager in the right spot
	public void addElement(UIElement e) {
		addThese.add(e);
	}

	//Removes an element from this manager if it contains it.
	public void removeElement(UIElement e) {
		removeThese.add(e);
	}

	//Add and remove everything that has asked to be added or removed.
	public void addAndRemoveElements() {
		//Remove anything that wants to be removed. Removal is done first to make adding faster.
		if (removeThese.size() > 0) {
			elements.removeAll(removeThese);
			if (removeThese.contains(focus))
				focus = null;
			System.out.println("Removed " + removeThese);
			removeThese.clear();
		}

		//Add anything that wants to be added
		if (addThese.size() > 0) {
			for (UIElement e : addThese)
				addAtSortedLoc(e);
			System.out.println("Added " + addThese);
			addThese.clear();
		}
	}

	//Clear this UIManager of elements.
	public void clear() {
		clearFocus();
		elements.clear();
		addThese.clear();
		removeThese.clear();
	}

	//Render this UIManager.
	public void render(Graphics g) {
		//Render the elements
		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).render(g);
		}
		//Draw the focus box
		if (focus != null) {
			g.setColor(Color.red);
			g.drawRect(focusBoxX, focusBoxY, focusBoxWidth, focusBoxHeight);
		}
	}

	//Turn this manager's key listening on and off.
	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	//Does this contain a given UIElement?
	public boolean contains(UIElement e) {
		return elements.contains(e);
	}

	//Is a point within any of the elements?
	public boolean pointIsWithin(int x, int y) {
		for (UIElement e : elements) {
			if (e.pointIsWithin(x, y))
				return true;
		}
		return false;
	}

	//KeyListener methods
	public void keyPressed(int key, char c) {
		keyBuffer.add(new KeyCharPair(key, c));
	}

	public void keyReleased(int key, char c) {
	}

	public void inputStarted() {
	}

	public void inputEnded() {
	}

	public boolean isAcceptingInput() {
		return enabled;
	}

	public void setInput(Input input) {
	}
}