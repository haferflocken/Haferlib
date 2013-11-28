//Makes a UI event

package org.haferslick.gui.event;

public interface GUIEventGenerator {

	//Adds a listener for this generator's events
	void addListener(GUIEventListener l);

	//Removes a listener from this generator
	void removeListener(GUIEventListener l);

}