// A collapsible frame. Has a title and a subcontext.
// The subcontext is only updated and rendered when this is expanded.

package org.haferlib.slick.gui;

import java.util.HashSet;

import org.haferlib.slick.GraphicsUtils;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class CollapsibleFrame extends GUISubcontext implements GUIEventGenerator {
	
	protected int x2, y2;									// The location of the bottom right corner.
	protected int width, collapsedHeight, expandedHeight;	// The dimensions.
	protected int subcontextY1;								// The top of the subcontext render area.
	protected int depth;
	protected boolean dead;
	protected HashSet<GUIEventListener> listeners;			// The listeners listening to this. They are notified of collapsing/expanding.
	protected String title;									// The title of this element.
	protected Color textColor;								// The color of the title.
	protected Font font;									// The font of the title.
	protected int toggleButtonX1, toggleButtonY1,			// The toggle button position.
		toggleButtonX2, toggleButtonY2, toggleButtonCX;
	protected int toggleButtonPos, toggleButtonSize;		// The toggle button offset and side length.
	protected boolean expanded;								// Is this expanded?
	protected Image titleImage;								// The predrawn title.
	
	// Constructor.
	public CollapsibleFrame(String title, Color textColor, Font font, int x, int y, int width, int height, boolean expanded) {
		super();
		listeners = new HashSet<>();
		dead = false;
		this.title = title;
		this.textColor = textColor;
		this.font = font;
		setWidth(width);
		setHeight(height);
		redrawTitle();
		setX(x);
		setY(y);
		setExpanded(expanded);
	}
	
	// EFFECTS:  Add an element to this.
	public void addElement(GUIElement e) {
		subcontext.addElement(e);
	}
	
	// EFFECTS:  Add several elements to this.
	public void addElements(GUIElement[] es) {
		for (GUIElement e : es) {
			subcontext.addElement(e);
		}
	}
	
	// Remove an element from this.
	public void removeElement(GUIElement e) {
		subcontext.removeElement(e);
	}
	
	// EFFECTS:  Remove several elements from this.
	public void removeElements(GUIElement[] es) {
		for (GUIElement e : es) {
			subcontext.removeElement(e);
		}
	}
	
	// EFFECTS:  Remove all elements from this.
	public void clearElements() {
		subcontext.clear();
	}
	
	// Set if this is expanded or not.
	protected void setExpanded(boolean e) {
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
	protected void notifyListeners() {
		for (GUIEventListener l : listeners)
			l.guiEvent(new GUIEvent<Object>(this, GUIEvent.RESIZE_EVENT));
	}
	
	// Predraw the title.
	public void redrawTitle() {
		// Make a GraphicsUtil to help out.
		GraphicsUtils gUtil = new GraphicsUtils();

		// Figure out the height.
		int titleX = font.getLineHeight();
		int titleWidth = width - titleX;
		int titleHeight = gUtil.getWrappedStringHeight(font, title, titleWidth);
		collapsedHeight = titleHeight;

		try {
			// Create a new image to draw to.
			titleImage = Image.createOffscreenImage(width, collapsedHeight);
	
			// Get the graphics to draw with.
			Graphics g = titleImage.getGraphics();
			g.clear();
	
			// Give the image a transparent background.
			g.clearAlphaMap();
	
			// Figure out the size of the toggle button and draw it.
			toggleButtonSize = font.getLineHeight() * 2 / 3;
			int toggleButtonCenter = font.getLineHeight() / 2;
			toggleButtonPos = font.getLineHeight() / 6;
			g.setColor(textColor);
			g.setFont(font);
			g.drawRect(toggleButtonPos, toggleButtonPos, toggleButtonSize, toggleButtonSize); // Button outline.
			g.fillRect(toggleButtonPos, toggleButtonCenter - 1, toggleButtonSize, 2); // Horizontal bar.
	
			// Draw the title.
			gUtil.drawStringWrapped(g, title, titleX, 0, titleWidth);
	
			// Flush the graphics to the image.
			g.flush();
			g.destroy();
		}
		catch (SlickException e) {
			// Failing to create the title image is pretty awful, so mark this for death.
			dead = true;
			System.out.println("CollapsibleFrame failed to draw title image; marking for death.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(int delta) {
		// Only update the subcontext when expanded.
		if (expanded)
			super.update(delta);
	}

	@Override
	public void render(Graphics g) {
		// Draw the title.
		g.drawImage(titleImage, x1, y1);
		
		// Draw the subcontext if this is expanded.
		if (expanded) {
			renderSubcontext(g, x1, subcontextY1, x2, y2);
		}
		// If collapsed, draw the vertical bar on top of the title image to make the minus sign into a plus sign.
		else {
			g.setColor(textColor);
			g.fillRect(toggleButtonCX, toggleButtonY1, 2, toggleButtonSize);
		}
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		x2 = x1 + width;
		toggleButtonX1 = x1 + toggleButtonPos;
		toggleButtonX2 = toggleButtonX1 + toggleButtonSize;
		toggleButtonCX = toggleButtonX1 + toggleButtonSize / 2;
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		if (expanded)
			y2 = y1 + expandedHeight;
		else
			y2 = y1 + collapsedHeight;
		toggleButtonY1 = y1 + toggleButtonPos;
		toggleButtonY2 = toggleButtonY1 + toggleButtonSize;
		subcontextY1 = y1 + collapsedHeight;
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
	// EFFECTS:  setHeight sets the expanded height.
	public void setHeight(int h) {
		expandedHeight = h;
		if (expanded)
			y2 = y1 + expandedHeight;
	}

	@Override
	public int getHeight() {
		if (expanded)
			return expandedHeight;
		return collapsedHeight;
	}
	
	@Override
	public void click(int x, int y, int button) {
		super.click(x, y, button);
		
		// Collapse or expand if we click the button.
		if (button == Input.MOUSE_LEFT_BUTTON && x >= toggleButtonX1 && y >= toggleButtonY1 && x <= toggleButtonX2 && y <= toggleButtonY2)
			setExpanded(!expanded);
	}


	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean dead() {
		return dead;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (titleImage != null) {
			try {
				titleImage.getGraphics().destroy();
				titleImage.destroy();
			}
			catch (SlickException e) {
				e.printStackTrace();
			}
			titleImage = null;
		}
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
