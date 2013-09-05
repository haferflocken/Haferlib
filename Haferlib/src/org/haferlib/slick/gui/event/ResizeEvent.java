package org.haferlib.slick.gui.event;

/**
 * An event indicating that an element has resized itself.
 * Does not hold data.
 * 
 * @author John Werner
 */

public class ResizeEvent extends GUIEvent<Object> {

	/**
	 * Constructor.
	 * 
	 * @param generator The generator of this event.
	 */
	public ResizeEvent(GUIEventGenerator generator) {
		super(generator, null);
	}

}
