package org.haferlib.slick.gui.event;

/**
 * An event in the UI, such as a button press. Can contain data.
 * 
 * @author John Werner
 *
 * @param <V> The type of the data.
 */

public class GUIEvent<V> {

	private final GUIEventGenerator generator;
	private final V data;

	/**
	 * Construct a GUIEvent.
	 * 
	 * @param generator What generated this event.
	 * @param data The data in this event.
	 */
	public GUIEvent(GUIEventGenerator generator, V data) {
		this.generator = generator;
		this.data = data;
	}

	/**
	 * Get the generator of this event.
	 * 
	 * @return The GUIEventGenerator that created this event.
	 */
	public GUIEventGenerator getGenerator() {
		return generator;
	}

	/**
	 * Get the data of this event.
	 * 
	 * @return The data this event holds.
	 */
	public V getData() {
		return data;
	}

}