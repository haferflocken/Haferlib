//Makes a UI event

package org.haferlib.slick.gui;

public interface GUIEventGenerator {

	//Adds a listener for this generator's events
	void addListener(GUIEventListener l);

	//Removes a listener from this generator
	void removeListener(GUIEventListener l);

}