//Listens for a UI event, whatever that may be.

package org.haferlib.slick.gui.event;

public interface GUIEventListener {

	//Is called by the element that this is listening to.
	void guiEvent(GUIEvent<?> event);

}