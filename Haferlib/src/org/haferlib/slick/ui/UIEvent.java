//An event in the UI, such as a button press.

package org.haferlib.slick.ui;

public class UIEvent<V> {

	private UIEventGenerator generator;
	private V data;

	//Constructors
	private UIEvent() {}

	public UIEvent(UIEventGenerator generator) {
		this(generator, null);
	}

	public UIEvent(UIEventGenerator generator, V data) {
		this.generator = generator;
		this.data = data;
	}

	//Return what created this UIEvent
	public UIEventGenerator getGenerator() {
		return generator;
	}

	//Return this UIEvent's data
	public V getData() {
		return data;
	}

}