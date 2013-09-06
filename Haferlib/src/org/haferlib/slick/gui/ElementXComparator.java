package org.haferlib.slick.gui;

import java.util.Comparator;

public class ElementXComparator implements Comparator<GUIElement> {

	@Override
	public int compare(GUIElement a, GUIElement b) {
		if (a.getX() < b.getX())
			return -1;
		if (a.getX() > b.getX())
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ElementXComparator);
	}
}
