// A collapsible frame. Has a title and a subcontext.
// The subcontext is only updated and rendered when this is expanded.

package org.haferslick.gui;

import java.util.HashSet;

import org.haferslick.WordWrapper;
import org.haferslick.gui.event.GUIEventGenerator;
import org.haferslick.gui.event.GUIEventListener;
import org.haferslick.gui.event.ResizeEvent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class CollapsibleFrame extends GUISubcontext implements GUIEventGenerator {
	
	protected int collapsedHeight;							// The height when collapsed.
	protected int subcontextY1;								// The top of the subcontext render area.
	protected HashSet<GUIEventListener> listeners;			// The listeners listening to this. They are notified of collapsing/expanding.
	protected String[] title;									// The title of this element.
	protected Color textColor;								// The color of the title.
	protected Font font;									// The font of the title.
	protected int titleX, titleWidth;						// The title position.
	protected int toggleButtonX1, toggleButtonY1,			// The toggle button position.
		toggleButtonX2, toggleButtonY2,
		toggleButtonCX, toggleButtonCY;
	protected int toggleButtonPos, toggleButtonSize;		// The toggle button offset and side length.
	protected boolean expanded;								// Is this expanded?
	
	// Constructor.
	public CollapsibleFrame(String title, Color textColor, Font font, int x, int y, int width, int height, int depth, boolean expanded) {
		super(x, y, width, height, depth);
		listeners = new HashSet<GUIEventListener>();
		dead = false;
		this.textColor = textColor;
		this.width = width;
		setTitle(title, font);
		setWidth(width);
		setHeight(height);
		setX(x);
		setY(y);
		setExpanded(expanded);
	}
	
	// Get the text color.
	public Color getTextColor() {
		return textColor;
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
	
	// EFFECTS:  Remove an element from this.
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
	
	// EFFECTS:  Set if this is expanded or not.
	protected void setExpanded(boolean e) {
		expanded = e;
		if (expanded) {
			y2 = y1 + height;
		}
		else {
			y2 = y1 + collapsedHeight;
		}
		notifyListeners();
	}
	
	// Notify the listeners of expanding/collapsing.
	protected void notifyListeners() {
		ResizeEvent event = new ResizeEvent(this);
		for (GUIEventListener l : listeners)
			l.guiEvent(event);
	}
	
	// Set the title and word wrap it.
	public void setTitle(String t, Font f) {
		// Set the font.
		font = f;
		
		rethinkTitleWidth();
		
		// Word wrap the title.
		title = new WordWrapper().wordWrap(font, t, titleWidth);
		
		rethinkCollapsedHeight();
		rethinkTitleX();
		
		rethinkToggleButtonDimensions();
		rethinkToggleButtonX();
		rethinkToggleButtonY();
	}
	
	// Rethink the title dimensions.
	protected void rethinkTitleWidth() {
		titleWidth = width - font.getLineHeight();
	}
	
	// Rethink the collapsed height.
	protected void rethinkCollapsedHeight() {
		collapsedHeight = font.getLineHeight() * title.length;
		subcontextY1 = y1 + collapsedHeight;
	}
	
	// Rethink the title position.
	protected void rethinkTitleX() {
		titleX = x1 + font.getLineHeight();
	}
	
	// Rethink the size and offsets of the toggle button.
	protected void rethinkToggleButtonDimensions() {
		toggleButtonSize = font.getLineHeight() * 2 / 3;
		toggleButtonPos = font.getLineHeight() / 6;
	}
	
	// Sets the toggle button's position appropriately.
	protected void rethinkToggleButtonX() {
		toggleButtonX1 = x1 + toggleButtonPos;
		toggleButtonX2 = toggleButtonX1 + toggleButtonSize;
		toggleButtonCX = toggleButtonX1 + toggleButtonSize / 2;
	}
	
	protected void rethinkToggleButtonY() {
		toggleButtonY1 = y1 + toggleButtonPos;
		toggleButtonY2 = toggleButtonY1 + toggleButtonSize;
		toggleButtonCY = toggleButtonY1 + toggleButtonSize / 2;
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
		g.setColor(textColor);
		g.setFont(font);
		for (int i = 0; i < title.length; i++) {
			g.drawString(title[i], titleX, y1 + i * font.getLineHeight());
		}
		
		// Draw the toggle button.
		g.drawRect(toggleButtonX1, toggleButtonY1, toggleButtonSize, toggleButtonSize); // Button outline.
		g.fillRect(toggleButtonX1, toggleButtonCY, toggleButtonSize, 2); // Horizontal bar.
		
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
		
		// Set the title position.
		rethinkTitleX();
		
		// Set the toggle button position.
		rethinkToggleButtonX();
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		if (!expanded)
			y2 = y1 + collapsedHeight;
		
		// Set the toggle button position.
		rethinkToggleButtonY();
		
		// Adjust the subcontext clip.
		subcontextY1 = y1 + collapsedHeight;
	}

	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		rethinkTitleWidth();
	}

	@Override
	// EFFECTS:  setHeight sets the expanded height.
	public void setHeight(int h) {
		super.setHeight(h);
		if (!expanded)
			y2 = y1 + collapsedHeight;
	}

	@Override
	public int getHeight() {
		if (expanded)
			return super.getHeight();
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
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

}
