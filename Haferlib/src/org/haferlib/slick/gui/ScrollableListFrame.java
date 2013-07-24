 package org.haferlib.slick.gui;

import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collections;

public class ScrollableListFrame extends ScrollableFrame implements GUIEventListener {
	
	public static final byte XALIGN_LEFT = 0;
	public static final byte XALIGN_CENTER = 1;
	public static final byte XALIGN_RIGHT = 2;
	
	private byte xAlign;
	private int xAlignOffset;
	private int ySpacing;
	private int lastSize; // Keeps track of the number of elements in this list so if any die the elements can be realigned.
	
	// Constructors.
	public ScrollableListFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, byte xAlign, int xAlignOffset, int ySpacing) {
		super(x, y, width, height, depth, scrollBarWidth, scrollBarColor);
		if (xAlign == XALIGN_LEFT || xAlign == XALIGN_CENTER || xAlign == XALIGN_RIGHT)
			this.xAlign = xAlign;
		else
			this.xAlign = XALIGN_LEFT;
		this.xAlignOffset = xAlignOffset;
		this.ySpacing = ySpacing;
		lastSize = 0;
	}
	
	public ScrollableListFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor) {
		this(x, y, width, height, depth, scrollBarWidth, scrollBarColor, XALIGN_LEFT, 0, 0);
	}

	public ScrollableListFrame(GUIElement[] elements, int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, byte xAlign, int xAlignOffset, int ySpacing) {
		this(x, y, width, height, depth, scrollBarWidth, scrollBarColor, xAlign, xAlignOffset, ySpacing);
		addElements(elements);
	}
	
	public ScrollableListFrame(GUIElement[] elements, int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor) {
		this(elements, x, y, width, height, depth, scrollBarWidth, scrollBarColor, XALIGN_LEFT, 0, 0);
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		if (lastSize != subcontext.getNumElements()) {
			realignAllElements();
			lastSize = subcontext.getNumElements();
		}
	}
	
	@Override
	public void addElement(GUIElement e) {
		// Find the bottom y to align to. 
		int yPos = getBottomY() + ySpacing;
		
		// Align the element.
		alignElementX(e, getAlignedXAnchor());
		e.setY(yPos);
		
		// Listen to it, if possible.
		if (e instanceof GUIEventGenerator)
			((GUIEventGenerator)e).addListener(this);
		
		// Add the element.
		super.addElement(e);
		lastSize++;
	}
	
	@Override
	public void addElements(GUIElement[] es) {
		// Find the bottom y to align to. 
		int yPos = getBottomY() + ySpacing;
		
		// Figure out the x position we're aligning to.
		int xAnchor = getAlignedXAnchor();
		
		// Align the elements and listen to them.
		for (int i = 0; i < es.length; i++) {
			alignElementX(es[i], xAnchor);
			es[i].setY(yPos);
			yPos += es[i].getHeight() + ySpacing;
			
			if (es[i] instanceof GUIEventGenerator)
				((GUIEventGenerator)es[i]).addListener(this);
		}
		
		// Add the elements.
		super.addElements(es);
		lastSize += es.length;
	}
	
	@Override
	public void removeElement(GUIElement e) {
		if (subcontext.contains(e)) {
			// Remove the element.
			subcontext.removeElement(e);
			subcontext.addAndRemoveElements();
			lastSize--;
			
			// Realign the others.
			GUIElement realignAnchor = getElementAbove(e);
			if (realignAnchor == null)
				realignAllElements();
			else
				realignFromElement(realignAnchor);
		}
	}
	
	// Add an element at a specific y, shifting elements down to make room.
	public void addElement(GUIElement e, int y) {
		alignElementX(e, getAlignedXAnchor());
		GUIElement above = getElementAbove(y);
		if (above == null) {
			e.setY(Integer.MIN_VALUE);
		}
		else if (above.getY() + above.getHeight() >= y) {
			e.setY(above.getY());
			above.setY(y);
		}
		else {
			e.setY(above.getY() + above.getHeight() + ySpacing);
		}
		subcontext.addElement(e);
		subcontext.addAndRemoveElements();
		lastSize++;
		realignFromElement(e);
	}
	
	// Get the x coordinate that we are aligning to.
	protected int getAlignedXAnchor() {
		switch (xAlign) {
			case XALIGN_LEFT: return x1 + xAlignOffset;
			case XALIGN_CENTER: return x1 + getWidth() / 2 + xAlignOffset;
			default: return x1 + getWidth() + xAlignOffset;
		}
	}
	
	// X align an element to an x anchor.
	protected void alignElementX(GUIElement e, int xAnchor) {
		switch (xAlign) {
			case XALIGN_LEFT: e.setX(xAnchor); break;
			case XALIGN_CENTER: e.setX(xAnchor - e.getWidth() / 2); break;
			default: e.setX(xAnchor - e.getWidth()); break;
		}
	}
	
	// Find the bottom y of the bottom element in this frame.
	public int getBottomY() {
		// Get the elements and loop through them to find which is on bottom.
		ArrayList<GUIElement> elements = subcontext.getElements();
		int bottomY = y1;
		for (GUIElement e : elements) {
			int elementY2 = e.getY() + e.getHeight();
			if (elementY2 > bottomY)
				bottomY = elementY2;
		}
		return bottomY;
	}
	
	// Realign all the elements.
	public void realignAllElements() {
		// Get a copy of the elements list sorted by Y.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementYComparator());
		
		// Reposition the elements below the index.
		int xAnchor = getAlignedXAnchor();
		int yPos = 0;
		for (int i = 0; i < elements.size(); i++) {
			GUIElement e = elements.get(i);
			alignElementX(e, xAnchor);
			e.setY(yPos);
			yPos += e.getHeight() + ySpacing;
		}

		// Recalculate the scrolling fields.
		recalculateScrollingFields();
	}
	
	// Realign the elements below the element given.
	public void realignFromElement(GUIElement e) {
		// Get a copy of the elements list sorted by Y.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementYComparator());
		
		// Get the index of the element, returning if we can't find it.
		int i = elements.indexOf(e);
		if (i == -1)
			return;
		
		// Reposition the elements below the index.
		int xAnchor = getAlignedXAnchor();
		int yPos = e.getY() + e.getHeight();
		for (i += 1; i < elements.size(); i++) {
			e = elements.get(i);
			alignElementX(e, xAnchor);
			e.setY(yPos);
			yPos += e.getHeight() + ySpacing;
		}
		
		// Recalculate the scrolling fields.
		recalculateScrollingFields();
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// Upon receiving a GUIEvent, see if its data indicates it resized.
		Object eventData = event.getData();
		if (eventData == null)
			return;
		if (eventData.equals(GUIEvent.RESIZE_EVENT)) {
			// If it was a resize, realign from the generator.
			GUIEventGenerator generator = event.getGenerator();
			if (generator instanceof GUIElement)
				realignFromElement((GUIElement)generator);
		}
	}
}
