//An event in the UI, such as a button press.

package org.haferlib.slick.gui;

public class GUIEvent<V> {

	private GUIEventGenerator generator;
	private V data;

	//Constructors
	public GUIEvent(GUIEventGenerator generator) {
		this(generator, null);
	}

	public GUIEvent(GUIEventGenerator generator, V data) {
		this.generator = generator;
		this.data = data;
	}

	//Return what created this UIEvent
	public GUIEventGenerator getGenerator() {
		return generator;
	}

	//Return this UIEvent's data
	public V getData() {
		return data;
	}

}