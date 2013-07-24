package org.haferlib.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

public class CollapsibleListFrame extends CollapsibleFrame implements GUIEventListener {
	
	private ListFrame listFrame; // The list frame that holds the elements.

	// Constructor.
	public CollapsibleListFrame(String title, Color textColor, Font font,
			int x, int y, int width, boolean expanded) {
		super(title, textColor, font, x, y, width, font.getLineHeight(), expanded);
		
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
		int oldHeight = expandedHeight;
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
		// If the list frame resized, resize this.
		Object eventData = event.getData();
		if (eventData == null)
			return;
		if (eventData.equals(GUIEvent.RESIZE_EVENT)) {
			// If it was a resize, realign from the generator.
			GUIEventGenerator generator = event.getGenerator();
			if (generator instanceof GUIElement)
				recalculateHeight();
		}
	}

}
