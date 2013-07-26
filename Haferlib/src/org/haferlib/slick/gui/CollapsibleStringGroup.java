// A collapsible group of strings that can update its position in a ScrollableListFrame when it changes size.

package org.haferlib.slick.gui;

import java.util.HashSet;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.haferlib.slick.GraphicsUtils;

public class CollapsibleStringGroup implements GUIElement, GUIEventGenerator {
	
	private HashSet<GUIEventListener> listeners;					// The listeners to be notified if this changes size.
	protected String title;											// The title of the group.
	private String[] strings;										// The strings within the group.
	private Color textColor;										// The color of the text.
	private Font font;												// The font of the text.
	private int x1, y1, x2, y2;										// The position.
	private int width, expandedHeight, collapsedHeight;				// The dimensions.
	private int toggleButtonX1, toggleButtonY1,						// The toggle button position.
					toggleButtonX2, toggleButtonY2, toggleButtonCX;
	private int toggleButtonPos, toggleButtonSize;					// The toggle button offset and side length.
	private int depth;
	private boolean expanded;										// Is this expanded?
	private Image image;											// The image that this is drawn to offscreen.
	
	// Constructor.
	public CollapsibleStringGroup(String title, String[] strings, Color textColor,
			int x, int y, int width, int depth, Font font, boolean expanded) {
		listeners = new HashSet<>();
		this.title = title;
		this.strings = strings;
		this.textColor = textColor;
		this.font = font;
		setWidth(width);
		setDepth(depth);
		try {
			redraw();
		}
		catch (SlickException e) {
		}
		setExpanded(expanded);
		setX(x);
		setY(y);
	}
	
	// Accessors.
	public String getTitle() {
		return title;
	}
	
	public String[] getStrings() {
		return strings;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	// Draw the predrawn image.
	public void redraw() throws SlickException {
		//Make a GraphicsUtil to help out.
		GraphicsUtils gUtil = new GraphicsUtils();
		
		//Figure out the height.
		int titleX = font.getLineHeight();
		int titleWidth = width - titleX;
		int titleHeight = gUtil.getWrappedStringHeight(font, title, titleWidth);
		int bulletSize = font.getLineHeight() / 3;
		int bulletX = titleX;
		int bulletYOffset = bulletSize;
		int stringsX = bulletX + bulletSize + bulletSize;
		int stringsWidth = width - stringsX;
		int stringsHeight = 0;
		for (String s : strings) {
			stringsHeight += gUtil.getWrappedStringHeight(font, s, stringsWidth);
		}
		expandedHeight = titleHeight + stringsHeight;
		collapsedHeight = titleHeight;
		
		// Only create a new image if the new height is larger or the current image is null.
		//if (image == null || image.isDestroyed() || image.getHeight() < expandedHeight) {
			// Release old resources.
			if (image != null) {
				//image.getGraphics().destroy();
				image.destroy();
			}
			image = Image.createOffscreenImage(width, expandedHeight);
		//}
		
		// Get the graphics to draw with.
		Graphics g = image.getGraphics();
		g.clear();
		
		// Give the image a transparent background.
		g.clearAlphaMap();
		
		// Figure out the size of the toggle button and draw it.
		toggleButtonSize = font.getLineHeight() * 2 / 3;
		int toggleButtonCenter = font.getLineHeight() / 2;
		toggleButtonPos = font.getLineHeight() / 6;
		g.setColor(textColor);
		g.setFont(font);
		g.drawRect(toggleButtonPos, toggleButtonPos, toggleButtonSize, toggleButtonSize); //Button outline.
		g.fillRect(toggleButtonPos, toggleButtonCenter - 1, toggleButtonSize, 2); //Horizontal bar.
		
		// Draw the title.
		int stringsY = gUtil.drawStringWrapped(g, title, titleX, 0, titleWidth).y + font.getLineHeight();

		// Draw the strings.
		for (String s : strings) {
			g.fillRect(bulletX, stringsY + bulletYOffset, bulletSize, bulletSize); //The bullet.
			stringsY = gUtil.drawStringWrapped(g, s, stringsX, stringsY, stringsWidth).y + font.getLineHeight(); //The string.
		}
		
		// Flush the graphics to the image.
		g.flush();
		g.destroy();
	}
	
	// Set if this is expanded or now.
	private void setExpanded(boolean e) {
		expanded = e;
		if (expanded) {
			y2 = y1 + expandedHeight;
		}
		else {
			y2 = y1 + collapsedHeight;
		}
		notifyListeners();
	}
	
	// Notify the listeners of expanding/collapsing.
	private void notifyListeners() {
		for (GUIEventListener l : listeners)
			l.guiEvent(new GUIEvent<Object>(this, GUIEvent.RESIZE_EVENT));
	}
	
	@Override
	public void update(int delta) {
	}
	
	@Override
	public void render(Graphics g) {
		// If expanded, just draw the whole image.
		if (expanded)
			g.drawImage(image, x1, y1);
		// If collapsed, draw the title of the image as well as a vertical bar to make the minus sign into a plus sign.
		else {
			g.drawImage(image, x1, y1, x2, y2, 0, 0, width, collapsedHeight);
			g.setColor(textColor);
			g.fillRect(toggleButtonCX, toggleButtonY1, 2, toggleButtonSize);
		}
	}
	
	@Override
	public void setX(int x) {
		x1 = x;
		x2 = x1 + width;
		toggleButtonX1 = x1 + toggleButtonPos;
		toggleButtonX2 = toggleButtonX1 + toggleButtonSize;
		toggleButtonCX = toggleButtonX1 + toggleButtonSize / 2;
	}
	
	@Override
	public int getX() {
		return x1;
	}

	@Override
	public void setY(int y) {
		y1 = y;
		if (expanded)
			y2 = y1 + expandedHeight;
		else
			y2 = y1 + collapsedHeight;
		toggleButtonY1 = y1 + toggleButtonPos;
		toggleButtonY2 = toggleButtonY1 + toggleButtonSize;
	}

	@Override
	public int getY() {
		return y1;
	}
	
	@Override
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public void setHeight(int h) {
	}

	@Override
	public int getHeight() {
		if (expanded)
			return expandedHeight;
		return collapsedHeight;
	}

	@Override
	public void click(int x, int y, int button) {
		// Collapse or expand if we click the button.
		if (button == Input.MOUSE_LEFT_BUTTON && x >= toggleButtonX1 && y >= toggleButtonY1 && x <= toggleButtonX2 && y <= toggleButtonY2)
			setExpanded(!expanded);
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
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}
	
	@Override
	public void setDepth(int d) {
		depth = d;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void keyPressed(int key, char c) {
	}
	
	@Override
	public void keyInputDone() {
	}
	
	@Override
	public boolean dead() {
		return false;
	}
	
	@Override
	public void destroy() {
		if (image != null) {
			try {
				image.getGraphics().destroy();
				image.destroy();
			}
			catch (SlickException e) {
				e.printStackTrace();
			}
			image = null;
		}
	}

	@Override
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.add(l);
	}
}
