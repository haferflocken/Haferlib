// An event in the UI, such as a button press.
// Contains some predefined data for common events.

package org.haferlib.slick.gui;

public class GUIEvent<V> {
	
	// Some easy to use event data.
	public static final String RESIZE_EVENT = "#event: resize";

	// Instance fields.
	private GUIEventGenerator generator;
	private V data;

	// Constructors.
	public GUIEvent(GUIEventGenerator generator) {
		this(generator, null);
	}

	public GUIEvent(GUIEventGenerator generator, V data) {
		this.generator = generator;
		this.data = data;
	}

	// Return what created this UIEvent.
	public GUIEventGenerator getGenerator() {
		return generator;
	}

	// Return this UIEvent's data.
	public V getData() {
		return data;
	}

}