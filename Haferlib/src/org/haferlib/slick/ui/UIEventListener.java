//Listens for a UI event, whatever that may be.

package org.haferlib.slick.ui;

public interface UIEventListener {

	//Is called by the element that this is listening to.
	void uiEvent(UIEvent event);

}