//Makes a UI event

package org.haferlib.slick.ui;

public interface UIEventGenerator {

	//Adds a listener for this generator's events
	void addListener(UIEventListener l);

	//Removes a listener from this generator
	void removeListener(UIEventListener l);

}