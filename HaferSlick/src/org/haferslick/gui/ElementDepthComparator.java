package org.haferslick.gui;

import java.util.Comparator;

public class ElementDepthComparator implements Comparator<GUIElement> {

	@Override
	public int compare(GUIElement a, GUIElement b) {
		if (a.getDepth() < b.getDepth())
			return -1;
		if (a.getDepth() > b.getDepth())
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ElementDepthComparator);
	}
}
