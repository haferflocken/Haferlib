package org.haferslick.gui;

import org.haferslick.gui.event.GUIEvent;
import org.haferslick.gui.event.GUIEventGenerator;
import org.haferslick.gui.event.GUIEventListener;
import org.haferslick.gui.event.ResizeEvent;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class CollapsibleListFrame extends CollapsibleFrame implements GUIEventListener {
	
	private ListFrame listFrame; // The list frame that holds the elements.

	// Constructor.
	public CollapsibleListFrame(String title, Color textColor, Font font,
			int x, int y, int width, int depth, boolean expanded) {
		super(title, textColor, font, x, y, width, font.getLineHeight(), depth, expanded);
		
		// Make the list frame.
		int listFrameX = x1 + font.getLineHeight();
		int listFrameY = subcontextY1;
		int listFrameWidth = getWidth() - font.getLineHeight();
		listFrame = new ListFrame(listFrameX, listFrameY, listFrameWidth, 0);
		listFrame.addListener(this);
		subcontext.addElement(listFrame);
		subcontext.addAndRemoveElements();
	}
	
	// EFFECTS: Recalculate the height.
	private void recalculateHeight() {
		int newHeight = collapsedHeight + listFrame.getHeight();
		int oldHeight = height;
		setHeight(newHeight);
		if (oldHeight != newHeight) {
			notifyListeners();
		}
	}
	
	// EFFECTS: Get the list width.
	public int getListWidth() {
		return listFrame.getWidth();
	}
	
	@Override
	protected void rethinkCollapsedHeight() {
		super.rethinkCollapsedHeight();
		if (listFrame != null)
			listFrame.setY(subcontextY1);
	}
	
	@Override
	public void addElement(GUIElement e) {
		listFrame.addElement(e);
	}
	
	@Override
	public void addElements(GUIElement[] es) {
		listFrame.addElements(es);
	}
	
	@Override
	public void removeElement(GUIElement e) {
		listFrame.removeElement(e);
	}
	
	@Override
	public void removeElements(GUIElement[] es) {
		listFrame.removeElements(es);
	}
	
	@Override
	public void clearElements() {
		listFrame.clearElements();
	}

	@Override
	public void guiEvent(GUIEvent<?> event) {
		// If it was a resize, realign from the generator.
		if (event instanceof ResizeEvent) {
			GUIEventGenerator generator = event.getGenerator();
			if (generator instanceof GUIElement)
				recalculateHeight();
		}
	}

}
