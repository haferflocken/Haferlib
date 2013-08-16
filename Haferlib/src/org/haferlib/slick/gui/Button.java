//A button, that when pushed, tells its listeners and sends then its data.

package org.haferlib.slick.gui;

import java.util.HashSet;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;

public class Button<V> extends AbstractRectangularElement implements GUIEventGenerator {

	public static final byte LEFT = 0;
	public static final byte CENTER = 1;
	public static final byte RIGHT = 2;

	protected V data;
	protected int centerX, centerY;
	protected Color backgroundColor;
	protected boolean highlight;
	protected Color highlightColor;
	protected int buttonKey;
	protected HashSet<GUIEventListener> listeners;

	// Constructors.
	public Button(int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key) {
		this(null, x, y, width, height, depth, backgroundColor, highlightColor, key);
	}

	public Button(V data, int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key) {
		super(x, y, width, height, depth);
		this.data = data;
		this.backgroundColor = backgroundColor;
		this.highlightColor = highlightColor;
		highlight = false;
		listeners = new HashSet<>();
		buttonKey = key;
	}
	
	// Get the data.
	public V getData() {
		return data;
	}
	
	// Press this button.
	protected void press() {
		for (GUIEventListener l : listeners)
			l.guiEvent(new GUIEvent<V>(this, data));
	}

	@Override
	public void update(int delta) {
	}

	@Override
	public void render(Graphics g) {
		// If this is being moused over, highlight the background.
		if (highlight) {
			if (highlightColor != null) {
				g.setColor(highlightColor);
				g.fillRect(x1, y1, width, height);
			}
		}
		// Otherwise, draw the non-highlighted background.
		else {
			if (backgroundColor != null) {
				g.setColor(backgroundColor);
				g.fillRect(x1, y1, width, height);
			}
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		centerX = x1 + width / 2;
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		centerY = y1 + height / 2;
	}

	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		centerX = x1 + width / 2;
	}

	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		centerY = y1 + height / 2;
	}

	@Override
	public void click(int x, int y, int button) {
		if (button == Input.MOUSE_LEFT_BUTTON)
			press();
	}

	@Override
	public void mouseDown(int x, int y, int button) {
	}

	@Override
	public void hover(int x, int y) {
		highlight = true;
	}

	@Override
	public void clickedElsewhere(GUIElement target, int button) {
	}

	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
	}

	@Override
	public void hoveredElsewhere(GUIElement target) {
		highlight = false;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if (key == buttonKey)
			press();
	}

	@Override
	public void keyInputDone() {
	}

	@Override
	public void destroy() {
		listeners.clear();
		listeners = null;
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

}