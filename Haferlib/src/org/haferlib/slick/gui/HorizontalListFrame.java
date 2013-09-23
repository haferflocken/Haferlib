 package org.haferlib.slick.gui;

import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventGenerator;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.haferlib.slick.gui.event.ResizeEvent;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HorizontalListFrame extends GUISubcontext implements GUIEventGenerator, GUIEventListener {
	
	public static final byte YALIGN_TOP = 0;
	public static final byte YALIGN_CENTER = 1;
	public static final byte YALIGN_BOTTOM = 2;
	
	private byte yAlign;
	private int yAlignOffset;
	private int xSpacing;
	private int lastSize; // Keeps track of the number of elements in this list so if any die the elements can be realigned.
	private HashSet<GUIEventListener> listeners;
	
	// Constructors.
	public HorizontalListFrame(int x, int y, int height, int depth, byte yAlign, int yAlignOffset, int xSpacing) {
		super(x, y, 0, height, depth);
		listeners = new HashSet<>();
		recalculateWidth();
		if (yAlign == YALIGN_TOP || yAlign == YALIGN_CENTER || yAlign == YALIGN_BOTTOM)
			this.yAlign = yAlign;
		else
			this.yAlign = YALIGN_TOP;
		this.yAlignOffset = yAlignOffset;
		this.xSpacing = xSpacing;
		lastSize = 0;
	}
	
	public HorizontalListFrame(int x, int y, int height, int depth) {
		this(x, y, height, depth, YALIGN_TOP, 0, 0);
	}

	public HorizontalListFrame(GUIElement[] elements, int x, int y, int height, int depth, byte yAlign, int yAlignOffset, int xSpacing) {
		this(x, y, height, depth, yAlign, yAlignOffset, xSpacing);
		addElements(elements);
	}
	
	public HorizontalListFrame(GUIElement[] elements, int x, int y, int height, int depth) {
		this(elements, x, y, height, depth, YALIGN_TOP, 0, 0);
	}
	
	// EFFECTS:  Return yAlign.
	public byte getYAlign() {
		return yAlign;
	}
	
	// EFFECTS:  Return yAlignOffset.
	public int getYAlignOffset() {
		return yAlignOffset;
	}
	
	// EFFECTS:  Return xSpacing.
	public int getXSpacing() {
		return xSpacing;
	}
	
	// EFFECTS:  Get the y coordinate that we are aligning to.
	private int getAlignedYAnchor() {
		switch (yAlign) {
			case YALIGN_TOP: return y1 + yAlignOffset;
			case YALIGN_CENTER: return y1 + getHeight() / 2 + yAlignOffset;
			default: return x1 + getWidth() + yAlignOffset;
		}
	}

	// EFFECTS:  Y align an element to an y anchor.
	private void alignElementY(GUIElement e, int yAnchor) {
		switch (yAlign) {
			case YALIGN_TOP: e.setY(yAnchor); break;
			case YALIGN_CENTER: e.setY(yAnchor - e.getHeight() / 2); break;
			default: e.setY(yAnchor - e.getHeight()); break;
		}
	}

	// EFFECTS:  Find the x2 of the rightmost element in this frame.
	private int getRightmostX() {
		// Get the elements and loop through them to find which is on bottom.
		List<GUIElement> elements = subcontext.getElements();
		int rightX = x1;
		for (GUIElement e : elements) {
			int elementX2 = e.getX() + e.getWidth();
			if (elementX2 > rightX)
				rightX = elementX2;
		}
		return rightX;
	}
	
	// EFFECTS:  Recalculate the width.
	public void recalculateWidth() {
		int oldWidth = width;
		x2 = getRightmostX();
		width = x2 - x1;
		if (width != oldWidth) {
			ResizeEvent event = new ResizeEvent(this);
			for (GUIEventListener l : listeners) {
				l.guiEvent(event);
			}
		}
	}
	
	// EFFECTS:  Realign all the elements.
	private void realignAllElements() {
		// Get a copy of the elements list sorted by X.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementXComparator());

		// Reposition the elements below the index.
		int yAnchor = getAlignedYAnchor();
		int xPos = x1;
		for (int i = 0; i < elements.size(); i++) {
			GUIElement e = elements.get(i);
			alignElementY(e, yAnchor);
			e.setX(xPos);
			xPos += e.getWidth() + xSpacing;
		}

		// Recalculate the width.
		recalculateWidth();
	}

	// EFFECTS:  Realign the elements to the right of the element given.
	//			 If the element given is not in this, realign all elements.
	private void realignFromElement(GUIElement e) {
		// Get a copy of the elements list sorted by X.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementXComparator());

		// Get the index of the element, returning if we can't find it.
		int i = elements.indexOf(e);
		if (i == -1) {
			realignAllElements();
			return;
		}

		// Reposition the elements to the right of the index.
		int yAnchor = getAlignedYAnchor();
		int xPos = e.getX() + e.getHeight() + xSpacing;
		for (i += 1; i < elements.size(); i++) {
			e = elements.get(i);
			alignElementY(e, yAnchor);
			e.setX(xPos);
			xPos += e.getWidth() + xSpacing;
		}

		// Recalculate the width.
		recalculateWidth();
	}
	
	// EFFECTS:  Add an element to the subcontext, setting its position appropriately.
	public void addElement(GUIElement e) {
		// Find the rightmost x to align to. 
		int xPos = getRightmostX() + xSpacing;
		
		// Align the element.
		alignElementY(e, getAlignedYAnchor());
		e.setX(xPos);
		
		// Listen to it, if possible.
		if (e instanceof GUIEventGenerator)
			((GUIEventGenerator)e).addListener(this);
		
		// Add the element.
		subcontext.addElement(e);
		subcontext.addAndRemoveElements();
		lastSize++;
		recalculateWidth();
	}
	
	// EFFECTS:  Add many elements to the subcontext, setting their positions appropriately.
	public void addElements(GUIElement[] es) {
		// Find the rightmost x to align to. 
		int xPos = getRightmostX() + xSpacing;
		
		// Figure out the x position we're aligning to.
		int yAnchor = getAlignedYAnchor();
		
		// Align the elements, listen to them, and add them.
		for (int i = 0; i < es.length; i++) {
			alignElementY(es[i], yAnchor);
			es[i].setX(xPos);
			xPos += es[i].getWidth() + xSpacing;
			
			if (es[i] instanceof GUIEventGenerator)
				((GUIEventGenerator)es[i]).addListener(this);
			
			subcontext.addElement(es[i]);
		}
		
		subcontext.addAndRemoveElements();
		lastSize += es.length;
		recalculateWidth();
	}
	
	// EFFECTS:  Add an element at a specific x, shifting elements right to make room.
	public void addElement(GUIElement e, int x) {
		// Align e.getY().
		alignElementY(e, getAlignedYAnchor());
		
		// Listen to e.
		if (e instanceof GUIEventGenerator)
			((GUIEventGenerator)e).addListener(this);
		
		// Add e to the subcontext.
		subcontext.addElement(e);
		subcontext.addAndRemoveElements();
		lastSize++;
		
		// Align e.getX().
		GUIElement above = getElementToLeft(x);
		if (above == null) {
			e.setX(Integer.MIN_VALUE);
			realignAllElements();
		}
		else {
			if (above.getX() + above.getWidth() >= x) {
				e.setX(above.getX());
				above.setX(x);
			}
			else 
				e.setX(above.getX() + above.getWidth() + xSpacing);
			
			realignFromElement(e);
		}
	}
	
	// EFFECTS:  Remove an element from the subcontext,
	//			 realigning the other elements appropriately.
	public void removeElement(GUIElement e) {
		if (subcontext.contains(e)) {
			// Stop listening to e.
			if (e instanceof GUIEventGenerator)
				((GUIEventGenerator)e).removeListener(this);
			
			// Remove the element.
			subcontext.removeElement(e);
			subcontext.addAndRemoveElements();
			lastSize--;
			
			// Realign the others.
			GUIElement realignAnchor = getElementToLeft(e);
			if (realignAnchor == null)
				realignAllElements();
			else
				realignFromElement(realignAnchor);
		}
	}
	
	// EFFECTS:  Remove several elements from the subcontext,
	//			 realigning other elements appropriately.
	public void removeElements(GUIElement[] es) {
		// Remove the elements.
		for (GUIElement e : es) {
			// Stop listening to e.
			if (e instanceof GUIEventGenerator)
				((GUIEventGenerator)e).removeListener(this);
			
			// Remove the element.
			subcontext.removeElement(e);
			subcontext.addAndRemoveElements();
			lastSize--;
		}
		
		// Realign all the elements.
		realignAllElements();
	}
	
	// EFFECTS:  Clear all elements from this.
	public void clearElements() {
		// Stop listening to all the elements.
		for (GUIElement e : subcontext.getElements()) {
			if (e instanceof GUIEventGenerator)
				((GUIEventGenerator)e).removeListener(this);
		}
		
		// Clear the subcontext and recalc the height.
		subcontext.clear();
		recalculateWidth();
	}
	
	// EFFECTS:  Change an element's position in the list to the one at the
	//			 given x coordinate.
	public void moveElement(GUIElement e, int x) {
		// If we don't contain e, return.
		if (!contains(e))
			return;
		
		// Loop through the elements to find one at the given x
		// and whether or not x is in the left half of the found element.
		GUIElement other = null;
		boolean leftHalf = false;
		for (GUIElement o : subcontext.getElements()) {
			int oX1 = o.getX();
			int oX2 = oX1 + o.getWidth();
			if (x >= oX1 && x <= oX2) {
				other = o;
				int oCX = (oX1 + oX2) / 2;
				if (x < oCX)
					leftHalf = true;
				break;
			}
		}
		
		// If an element was found, shift other and all elements to the right of it
		// right by e.getWidth() and then set e's x to other's old x.
		if (other != null) {
			int newX = (leftHalf ? other.getX() : other.getX() + other.getWidth());
			for (GUIElement o : subcontext.getElements()) {
				if (o.getX() >= newX)
					o.setX(o.getX() + e.getWidth());
			}
			e.setX(newX);
		}
		// Otherwise, place the element to the right of all other elements.
		else {
			int rightmostX = Integer.MIN_VALUE;
			for (GUIElement o : subcontext.getElements()) {
				int oX2 = o.getX() + o.getWidth();
				if (oX2 > rightmostX)
					rightmostX = oX2;
			}
			e.setX(rightmostX);
		}
		
		// Realign everything.
		realignAllElements();
	}
	
	@Override
	public void update(int delta) {
		super.update(delta);
		
		// Check if any elements died.
		if (lastSize != subcontext.getNumElements()) {
			realignAllElements();
			lastSize = subcontext.getNumElements();
		}
	}

	@Override
	public void render(Graphics g) {
		renderSubcontext(g, x1, y1, x2, y2);
	}
	
	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If it was a resize, realign from the generator.
		if (event instanceof ResizeEvent) {
			GUIEventGenerator generator = event.getGenerator();
			if (generator instanceof GUIElement)
				realignFromElement((GUIElement)generator);
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
