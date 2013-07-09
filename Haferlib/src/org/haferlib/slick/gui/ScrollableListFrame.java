package org.haferlib.slick.gui;

import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collections;

public class ScrollableListFrame extends ScrollableFrame {
	
	public static final byte XALIGN_LEFT = 0;
	public static final byte XALIGN_CENTER = 1;
	public static final byte XALIGN_RIGHT = 2;
	
	private byte xAlign;
	private int xAlignOffset;
	private int ySpacing;
	
	public ScrollableListFrame(int x, int y, int width, int height, int depth, int scrollBarWidth, Color scrollBarColor, byte xAlign, int xAlignOffset, int ySpacing) {
		super(x, y, width, height, depth, scrollBarWidth, scrollBarColor);
		if (xAlign == XALIGN_LEFT || xAlign == XALIGN_CENTER || xAlign == XALIGN_RIGHT)
			this.xAlign = xAlign;
		else
			this.xAlign = XALIGN_LEFT;
		this.xAlignOffset = xAlignOffset;
		this.ySpacing = ySpacing;
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
	
	private int getBottomY() {
		//Get the elements and loop through them to find which is on bottom.
		ArrayList<GUIElement> elements = subcontext.getElements();
		int bottomY = y1;
		for (GUIElement e : elements) {
			int elementY2 = e.getY() + e.getHeight();
			if (elementY2 > bottomY)
				bottomY = elementY2;
		}
		return bottomY;
	}
	
	public void addElement(GUIElement e) {
		//Find the bottom y to align to. 
		int yPos = getBottomY() + ySpacing;
		
		//Align the element.
		e.setX(x1);
		e.setY(yPos);
		
		//Add the element.
		super.addElement(e);
	}
	
	public void addElements(GUIElement[] es) {
		//Find the bottom y to align to. 
		int yPos = getBottomY() + ySpacing;
		
		//Figure out the x position we're aligning to.
		int xPos;
		switch (xAlign) {
			case XALIGN_LEFT: xPos = x1 + xAlignOffset; break;
			case XALIGN_CENTER: xPos = x1 + getWidth() / 2 + xAlignOffset; break;
			default: xPos = x1 + getWidth() + xAlignOffset; break;
		}
		
		//Align the elements.
		for (int i = 0; i < es.length; i++) {
			switch (xAlign) {
				case XALIGN_LEFT: es[i].setX(xPos); break;
				case XALIGN_CENTER: es[i].setX(xPos - es[i].getWidth() / 2); break;
				default: es[i].setX(xPos - es[i].getWidth()); break;
			}
			es[i].setY(yPos);
			yPos += es[i].getHeight() + ySpacing;
		}
		
		//Add the elements.
		super.addElements(es);
	}
	
	public void realignFromElement(GUIElement e) {
		//Get a copy of the elements list sorted by Y.
		ArrayList<GUIElement> elements = new ArrayList<GUIElement>(subcontext.getElements());
		Collections.sort(elements, new ElementYComparator());
		
		//Get the index of the element.
		int i;
		for (i = 0; i < elements.size(); i++) {
			if (elements.get(i).equals(e))
				break;
		}
		//If we don't find the element, return.
		if (i == elements.size())
			return;
		
		//Reposition the elements below the index.
		int yPos = e.getY() + e.getHeight();
		for (i += 1; i < elements.size(); i++) {
			e = elements.get(i);
			e.setY(yPos);
			yPos += e.getHeight() + ySpacing;
		}
		
		//Recalculate the scrolling fields.
		recalculateScrollingFields();
	}
}
