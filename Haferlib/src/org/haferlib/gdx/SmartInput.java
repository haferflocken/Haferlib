package org.haferlib.gdx;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;

/**
 * A class that keeps track of input, allowing polling of the different
 * states of a press (just pressed, held down, just released, not down)
 * for the keyboard and every pointer.
 * 
 * @author John Werner
 */

public class SmartInput implements InputProcessor {
	
	// A class that holds pointer data.
	public static class Pointer {
		
		private Pointer() {
			buttonStates = new IntIntMap();
		}
		
		private int x;
		private int y;
		private IntIntMap buttonStates;
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public IntIntMap getButtonStates() {
			return buttonStates;
		}
		
		public boolean isAnyButtonInState(int state) {
			for (IntIntMap.Entry<?> entry : buttonStates.entries()) {
				if (entry.value == state)
					return true;
			}
			return false;
		}
		
		public boolean isButtonInState(int button, int state) {
			int buttonState = buttonStates.get(button, NOT_DOWN);
			return buttonState == state;
		}
		
		public int getButtonState(int button) {
			return buttonStates.get(button, NOT_DOWN);
		}
		
	}
	
	// Static final states of buttons or keys.
	public static final int JUST_RELEASED = -1;
	public static final int NOT_DOWN = 0;
	public static final int JUST_PRESSED = 1;
	public static final int HELD_DOWN = 2;
	
	// A map of pointers to buttons to states. 
	private IntMap<Pointer> pointerStates;
	
	// A map of keycodes to states.
	private IntIntMap keyStates;
	
	/**
	 * Make a SmartInput.
	 */
	public SmartInput() {
		pointerStates = new IntMap<Pointer>();
		keyStates = new IntIntMap();
	}
	
	/**
	 * Must be called at the end the render call as it progresses the states as needed.
	 */
	public void update() {
		// Progress button states.
		for (Pointer pointer : pointerStates.values()) {
			for (IntIntMap.Entry<?> entry : pointer.buttonStates.entries()) {
				if (entry.value == JUST_PRESSED)
					entry.value = HELD_DOWN;
				else if (entry.value == JUST_RELEASED)
					entry.value = NOT_DOWN;
			}
		}
		
		// Progress key states.
		for (IntIntMap.Entry<?> entry : keyStates.entries()) {
			if (entry.value == JUST_PRESSED)
				entry.value = HELD_DOWN;
			else if (entry.value == JUST_RELEASED)
				entry.value = NOT_DOWN;
		}
	}
	
	public IntMap<Pointer> getPointers() {
		return pointerStates;
	}
	
	public boolean doesAnyPointerHaveState(int state) {
		for (Pointer pointer : pointerStates.values()) {
			if (pointer.isAnyButtonInState(state))
				return true;
		}
		return false;
	}
	
	public boolean isPointerButtonInState(int pointer, int button, int state) {
		Pointer p = pointerStates.get(pointer);
		if (p != null) {
			return p.isButtonInState(button, state);
		}
		return false;
	}
	
	public int getPointerButtonState(int pointer, int button) {
		Pointer p = pointerStates.get(pointer);
		if (p != null) {
			return p.getButtonState(button);
		}
		return NOT_DOWN;
	}
	
	public boolean isKeyInState(int keycode, int state) {
		int keyState = keyStates.get(keycode, NOT_DOWN);
		return keyState == state;
	}
	
	public int getKeyState(int keycode) {
		return keyStates.get(keycode, NOT_DOWN);
	}

	@Override
	public boolean keyDown(int keycode) {
		keyStates.put(keycode, JUST_PRESSED);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		keyStates.put(keycode, JUST_RELEASED);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Pointer p = pointerStates.get(pointer);
		if (p == null) {
			p = new Pointer();
			pointerStates.put(pointer, p);
		}
		p.x = screenX;
		p.y = screenY;
		p.buttonStates.put(button, JUST_PRESSED);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Pointer p = pointerStates.get(pointer);
		if (p == null) {
			p = new Pointer();
			pointerStates.put(pointer, p);
		}
		p.x = screenX;
		p.y = screenY;
		p.buttonStates.put(button, JUST_RELEASED);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Pointer p = pointerStates.get(pointer);
		if (p == null) {
			p = new Pointer();
			pointerStates.put(pointer, p);
		}
		p.x = screenX;
		p.y = screenY;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		touchDragged(screenX, screenY, 0);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
