package org.haferlib.slick.gui;

import java.util.HashSet;

import org.haferlib.slick.WordWrapper;
import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventGenerator;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.haferlib.slick.gui.event.ResizeEvent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class TextDisplay extends AbstractRectangularElement implements GUIEventGenerator {
	
	private HashSet<GUIEventListener> listeners;	// The listeners.
	private int maxWidth, maxHeight;				// Max dimensions.
	private Font font;								// The font to display the text with.
	private Color color;							// The color to draw the text in.
	private String[] wrappedText;					// The text to display. Each string is a line.
	
	public TextDisplay(int x, int y, int width, int height, int depth, String text, Font font, Color color) {
		super(x, y, width, height, depth);
		listeners = new HashSet<>();
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setText(text, font);
		setColor(color);
	}
	
	public void setText(String text, Font f) {
		// Set the font.
		font = f;
		
		// Word wrap the text.
		wrappedText = new WordWrapper().wordWrap(font, text, maxWidth);
		
		// Recalc the width.
		int oldWidth = width;
		width = 0;
		for (String s : wrappedText) {
			int sWidth = font.getWidth(s);
			if (sWidth > width)
				width = sWidth;
		}
		if (width > maxWidth)
			width = maxWidth;
		
		// Recalc the height.
		int oldHeight = height;
		height = wrappedText.length * font.getLineHeight();
		if (height > maxHeight)
			height = maxHeight;
		
		// Notify listeners if needed.
		if (oldWidth != width || oldHeight != height)
			notifyListeners();
	}
	
	public void setText(String text) {
		setText(text, font);
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	private void notifyListeners() {
		GUIEvent<?> event = new ResizeEvent(this);
		for (GUIEventListener l : listeners) 
			l.guiEvent(event);
	}

	@Override
	public void update(int delta) {
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		for (int i = 0; i < wrappedText.length; i++) {
			g.drawString(wrappedText[i], x1, y1 + i * font.getLineHeight());
		}
	}

	@Override
	public void setWidth(int w) {
		maxWidth = w;
		if (maxWidth < width) {
			width = maxWidth;
			x2 = x1 + width;
		}
	}

	@Override
	public void setHeight(int h) {
		maxHeight = h;
		if (maxHeight < height) {
			height = maxHeight;
			y2 = y1 + height;
		}
	}

	@Override
	public void click(int x, int y, int button) {
	}

	@Override
	public void mouseDown(int x, int y, int button) {
	}

	@Override
	public void hover(int x, int y) {
	}

	@Override
	public void clickedElsewhere(GUIElement target, int button) {
	}

	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
	}

	@Override
	public void hoveredElsewhere(GUIElement target) {
	}

	@Override
	public void keyPressed(int key, char c) {
	}

	@Override
	public void keyInputDone() {
	}

	@Override
	public void destroy() {
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
