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
	
	// The modes of resizing.
	public static final byte WIDTH_DYNAMIC_HEIGHT_DYNAMIC = 0;
	public static final byte WIDTH_STATIC_HEIGHT_DYNAMIC = 1;
	public static final byte WIDTH_DYNAMIC_HEIGHT_STATIC = 2;
	public static final byte WIDTH_STATIC_HEIGHT_STATIC = 3;
	
	// The ways to align text.
	public static final byte TEXT_ALIGN_LEFT = 0;
	public static final byte TEXT_ALIGN_CENTER = 1;
	public static final byte TEXT_ALIGN_RIGHT = 2;
	
	private HashSet<GUIEventListener> listeners;	// The listeners.
	private byte dimensionMode;						// The mode for setting dimensions.
	private int maxWidth, maxHeight;				// Max dimensions.
	private Font font;								// The font to display the text with.
	private Color color;							// The color to draw the text in.
	private String unwrappedText;					// The unwrapped version of the text to display.
	private String[] wrappedText;					// The text to display. Each string is a line.
	private byte textAlign;							// The way to align the text.
	private int[] wrappedTextX;						// The x coordinates of the wrapped text.
	
	public TextDisplay(int x, int y, int maxWidth, int maxHeight, int depth, String text, Font font, Color color, byte dimensionMode, byte textAlign) {
		super(x, y, maxWidth, maxHeight, depth);
		listeners = new HashSet<>();
		setDimensionMode(dimensionMode);
		this.unwrappedText = text;
		this.font = font;
		this.textAlign = textAlign;
		setHeight(maxHeight);
		setWidth(maxWidth);
		setColor(color);
	}
	
	public TextDisplay(int x, int y, int maxWidth, int maxHeight, int depth, String text, Font font, Color color) {
		this(x, y, maxWidth, maxHeight, depth, text, font, color, WIDTH_DYNAMIC_HEIGHT_DYNAMIC, TEXT_ALIGN_LEFT);
	}
	
	private boolean hasDynamicWidth() {
		return dimensionMode == WIDTH_DYNAMIC_HEIGHT_DYNAMIC || dimensionMode == WIDTH_DYNAMIC_HEIGHT_STATIC;
	}
	
	private boolean hasDynamicHeight() {
		return dimensionMode == WIDTH_DYNAMIC_HEIGHT_DYNAMIC || dimensionMode == WIDTH_STATIC_HEIGHT_DYNAMIC;
	}
	
	private void rewrapText() {
		// Word wrap the text.
		wrappedText = new WordWrapper().wordWrap(font, unwrappedText, maxWidth);
				
		// Recalc the width if the width is dynamic.
		int oldWidth = width;
		if (hasDynamicWidth()) {
			width = 0;
			for (String s : wrappedText) {
				int sWidth = font.getWidth(s);
				if (sWidth > width)
					width = sWidth;
			}
			if (width > maxWidth)
				width = maxWidth;
			x2 = x1 + width;
		}
				
		// Recalc the height if the height is dynamic.
		int oldHeight = height;
		if (hasDynamicHeight()) {
			height = wrappedText.length * font.getLineHeight();
			if (height > maxHeight)
				height = maxHeight;
			y2 = y1 + height;
		}
				
		// Notify listeners if needed.
		if (oldWidth != width || oldHeight != height)
			notifyListeners();
				
		// Align the text.
		wrappedTextX = new int[wrappedText.length];
		if (textAlign == TEXT_ALIGN_LEFT) {
			for (int i = 0; i < wrappedText.length; i++) {
				wrappedTextX[i] = x1;
			}
		}
		else if (textAlign == TEXT_ALIGN_CENTER) {
			int centerX = (x1 + x2) / 2;
			for (int i = 0; i < wrappedText.length; i++) {
				wrappedTextX[i] = centerX - font.getWidth(wrappedText[i]) / 2;
			}
		}
		else {
			for (int i = 0; i < wrappedText.length; i++) {
				wrappedTextX[i] = x2 - font.getWidth(wrappedText[i]);
			}
		}
	}
	
	public void setDimensionMode(byte dM) {
		dimensionMode = dM;
	}
	
	public void setText(String text, Font f, byte tA) {
		unwrappedText = text;
		font = f;
		textAlign = tA;
		rewrapText();
	}
	
	public void setText(String text, Font f) {
		setText(text, f, textAlign);
	}
	
	public void setText(String text) {
		setText(text, font, textAlign);
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
			g.drawString(wrappedText[i], wrappedTextX[i], y1 + i * font.getLineHeight());
		}
	}
	
	@Override
	public void setX(int x) {
		int dX = x - x1;
		super.setX(x);
		for (int i = 0; i < wrappedTextX.length; i++)
			wrappedTextX[i] += dX;
	}

	@Override
	public void setWidth(int w) {
		maxWidth = w;
		if (hasDynamicWidth()) {
			if (maxWidth < width) {
				width = maxWidth;
				x2 = x1 + width;
			}
		}
		else {
			super.setWidth(w);
		}
		rewrapText();
	}

	@Override
	public void setHeight(int h) {
		maxHeight = h;
		if (hasDynamicHeight()) {
			if (maxHeight < height) {
				height = maxHeight;
				y2 = y1 + height;
			}
		}
		else {
			super.setHeight(h);
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
