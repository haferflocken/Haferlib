//Compares elements by depth.

package org.haferlib.slick.gui;

import java.util.Comparator;

public class ElementYComparator implements Comparator<GUIElement> {

	@Override
	public int compare(GUIElement a, GUIElement b) {
		if (a.getY() < b.getY())
			return -1;
		if (a.getY() > b.getY())
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ElementYComparator);
	}
}
