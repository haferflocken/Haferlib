 package org.haferlib.slick.gui;

import java.util.List;

import org.haferlib.slick.gui.event.GUIEvent;
import org.haferlib.slick.gui.event.GUIEventGenerator;
import org.haferlib.slick.gui.event.GUIEventListener;
import org.haferlib.slick.gui.event.ResizeEvent;
import org.newdawn.slick.Color;

public class ScrollableListFrame extends ScrollableFrame implements GUIEventListener {
	
	private ListFrame listFrame; // The list frame that handles all the listing work, because why recode it?
	
	// Constructors.
	public ScrollableListFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, byte xAlign, int xAlignOffset, int ySpacing) {
		super(x, y, width, height, depth, scrollBarWidth, scrollBarColor);
		
		// Make the list frame.
		makeListFrame(xAlign, xAlignOffset, ySpacing);
	}
	
	public ScrollableListFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor) {
		this(x, y, width, height, depth, scrollBarWidth, scrollBarColor, ListFrame.XALIGN_LEFT, 0, 0);
	}

	public ScrollableListFrame(GUIElement[] elements, int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, byte xAlign, int xAlignOffset, int ySpacing) {
		this(x, y, width, height, depth, scrollBarWidth, scrollBarColor, xAlign, xAlignOffset, ySpacing);
		addElements(elements);
	}
	
	public ScrollableListFrame(GUIElement[] elements, int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor) {
		this(elements, x, y, width, height, depth, scrollBarWidth, scrollBarColor, ListFrame.XALIGN_LEFT, 0, 0);
	}
	
	// Get listFrame's x align.
	public byte getXAlign() {
		return listFrame.getXAlign();
	}
	
	// Return listFrame's xAlignOffset.
	public int getXAlignOffset() {
		return listFrame.getXAlignOffset();
	}
		
	// Return listFrame's ySpacing.
	public int getYSpacing() {
		return listFrame.getYSpacing();
	}
	
	// Make the list frame.
	private void makeListFrame(byte xAlign, int xAlignOffset, int ySpacing) {
		listFrame = new ListFrame(x1, y1, width - scrollBarWidth, 0, xAlign, xAlignOffset, ySpacing);
		listFrame.addListener(this);
		subcontext.addElement(listFrame);
		subcontext.addAndRemoveElements();
		recalculateScrollingFields();
	}
	
	// Recalculate the list height.
	public void recalculateListHeight() {
		listFrame.recalculateHeight();
		recalculateScrollingFields();
	}
	
	// Add an element at a specific y, shifting elements down to make room.
	public void addElement(GUIElement e, int y) {
		listFrame.addElement(e, y);
		recalculateScrollingFields();
	}
	
	// Move an element to a different spot in the list.
	public void moveElement(GUIElement e, int y) {
		listFrame.moveElement(e, y);
		recalculateScrollingFields();
	}
	
	@Override
	public void addElement(GUIElement e) {
		listFrame.addElement(e);
		recalculateScrollingFields();
	}
	
	@Override
	public void addElements(GUIElement[] es) {
		listFrame.addElements(es);
		recalculateScrollingFields();
	}
	
	@Override
	public void removeElement(GUIElement e) {
		listFrame.removeElement(e);
		recalculateScrollingFields();
	}
	
	@Override
	public void clearElements() {
		listFrame.clearElements();
		recalculateScrollingFields();
	}
	
	@Override
	public boolean contains(GUIElement e) {
		return listFrame.contains(e);
	}
	
	@Override
	public void reinitSubcontext() {
		byte xAlign = listFrame.getXAlign();
		int xAlignOffset = listFrame.getXAlignOffset();
		int ySpacing = listFrame.getYSpacing();
		super.reinitSubcontext();
		makeListFrame(xAlign, xAlignOffset, ySpacing);
	}
	
	/**
	 * GUISubcontext has some accessors that have to be overridden to look
	 * at listFrame rather than subcontext.
	 * These methods are between here...
	 */
	
	@Override 
	public GUIElement getElementAbove(int y) {
		return listFrame.getElementAbove(y);
	}
	
	@Override
	public GUIElement getElementBelow(int y) {
		return listFrame.getElementBelow(y);
	}
	
	@Override
	public GUIElement getElementAtPoint(int x, int y) {
		return listFrame.getElementAtPoint(x, y);
	}
	
	@Override
	public List<GUIElement> getElements() {
		if (listFrame != null)
			return listFrame.getElements();
		return null;
	}
	
	/**
	 * .. and here.
	 */
	
	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If it was a resize, realign from the generator.
		if (event instanceof ResizeEvent) {
			GUIEventGenerator generator = event.getGenerator();
			if (generator instanceof GUIElement)
				recalculateScrollingFields();
		}
	}
}
