package org.hafergdx.gui;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.hafergdx.SmartInput;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;

public class NewContext {
	
	// An interface that emulates a higher order function.
	// Setup is called, then modElement called 0 to n times, then finish is called.
	private static interface ElementLoopMethod {
		void setup();
		void modElement(GUIElement e);
		void finish();
	}
	
	// A method that handles the pointer input for a GUIElement.
	private final ElementLoopMethod POINTER_INPUT_METHOD = new ElementLoopMethod() {
		
		private Input input;
		private SmartInput smartInput;
		private IntIntMap consumedEvents = new IntIntMap();
		
		@Override
		public void setup() {
			input = (Input)elmParameters.get(0);
			smartInput = (SmartInput)elmParameters.get(1);
			consumedEvents.clear();
		}
		
		@Override
		public void modElement(GUIElement e) {
			// For each pointer...
			for (IntMap.Entry<SmartInput.Pointer> entry : smartInput.getPointers().entries()) {
				int pointerId = entry.key;
				SmartInput.Pointer pointer = entry.value;
				int pointerX = pointer.getX();
				int pointerY = pointer.getY();
				boolean pointerWithin = e.pointIsWithin(pointerX, pointerY);
				
				// For each button in the pointer, touch the element appropriately.
				IntIntMap buttonStates = pointer.getButtonStates();
				for (IntIntMap.Entry<?> button : buttonStates.entries()) {
					// button.key is the id, button.value is the state
					// Get if the button has been consumed.
					boolean buttonConsumed = consumedEvents.get(pointerId, Integer.MAX_VALUE) == Integer.MAX_VALUE;
					if (button.value == SmartInput.HELD_DOWN) {
						if (pointerWithin) {
							if (buttonConsumed)
								e.pointerDownElsewhere(pointerId, button.key);
							else
								e.pointerDown(pointerX, pointerY, pointerId, button.key);
						}
						else {
							e.pointerDownElsewhere(pointerId, button.key);
						}
					}
					else if (button.value == SmartInput.JUST_PRESSED) {
						if (pointerWithin) {
							if (buttonConsumed)
								e.pointerJustPressedElsewhere(pointerId, button.key);
							else
								e.pointerJustPressed(pointerX, pointerY, pointerId, button.key);
						}
						else {
							e.pointerJustPressedElsewhere(pointerId, button.key);
						}
					}
					else if (button.value == SmartInput.JUST_RELEASED) {
						if (pointerWithin) {
							if (buttonConsumed)
								e.pointerJustReleasedElsewhere(pointerId, button.key);
							else
								e.pointerJustReleased(pointerX, pointerY, pointerId, button.key);
						}
						else {
							e.pointerJustReleasedElsewhere(pointerId, button.key);
						}
					}
					else
						continue;
					consumedEvents.put(pointerId, button.key);
				}
				
				// If no buttons were pressed, hover.
				// TODO
				if (!consumedEvents.containsKey(pointerId)) {
					e.pointerHovering(pointerX, pointerY, pointerId);
					consumedEvents.put(pointerId, Integer.MIN_VALUE);
				}
			}
		}
		
		@Override
		public void finish() {}
	};
	
	// A method that calls the update method of a GUIElement.
	private final ElementLoopMethod UPDATE_METHOD = new ElementLoopMethod() {
		@Override
		public void setup() {}
		
		@Override
		public void modElement(GUIElement e) {
			e.update(lastDelta);
		}
		
		@Override
		public void finish() {}
	};
	
	private ArrayList<Object> elmParameters;	// A field to pass parameters to the loop methods.
	private int lastDelta;						// The last delta. Set at the start of a call to update().
	private ArrayList<GUIElement> elements;
	private ArrayDeque<GUIElement> loopStack;
	private GUIElement touchFocus;

	/**
	 * Make a context.
	 */
	public NewContext() {
		elmParameters = new ArrayList<Object>();
		elements = new ArrayList<GUIElement>();
		loopStack = new ArrayDeque<GUIElement>();
		touchFocus = null;
	}
	
	public void update(Input input, SmartInput smartInput, int delta) {
		lastDelta = delta;
		
		// Handle pointer input.
		elmParameters.set(0, input);
		elmParameters.set(1, smartInput);
		loop(POINTER_INPUT_METHOD);

		// Call the update() of all elements.
		loop(UPDATE_METHOD);
	}
	
	/**
	 * Do something to the elements.
	 * 
	 * @param method What to do.
	 */
	private void loop(ElementLoopMethod method) {
		// Clear the loop stack and fill it with the elements in reverse order.
		loopStack.clear();
		for (int i = elements.size() - 1; i > -1; i--) {
			loopStack.add(elements.get(i));
		}
		
		// Setup the method.
		method.setup();
		
		// Loop over the elements and their subelements.
		GUIElement e;
		while (!loopStack.isEmpty()) {
			// If there are subelements, push them onto the stack and handle them first.
			e = loopStack.peek();
			GUIElement[] subElements = e.getSubElements();
			if (subElements != null) {
				for (GUIElement s : subElements) {
					loopStack.push(s);
				}
			}
			// If there aren't subelements, pop the stack and use method on the element.
			else {
				loopStack.pop();
				method.modElement(e);
			}
		}
		
		// Finish the method.
		method.finish();
	}
	
}
