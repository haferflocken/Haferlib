//Listens for a UI event, whatever that may be.

package org.haferlib.slick.gui;

public interface GUIEventListener {

	//Is called by the element that this is listening to.
	@SuppressWarnings("rawtypes")
	void guiEvent(GUIEvent event);

}