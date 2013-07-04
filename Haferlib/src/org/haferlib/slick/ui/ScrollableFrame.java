
//A scrollable frame containing UIElements, arranged in a list.
//If there is enough height to display all, no scroll bar is dislayed.

package org.haferlib.slick.ui;

import org.newdawn.slick.Input;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;

public class ScrollableFrame implements UIElement, UIEventListener {

	private UIElement[] elements;
	private UIElement focus;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private int width;
	private int height;
	private int depth;
	private int spacing;
	private int leftMargin;
	private int topMargin;
	private int bottomMargin;
	private int scrolledAmount;
	private int maxScrolledAmount;
	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarWidth;
	private int scrollBarHeight;
	private int topIndex;
	private int bottomIndex;
	private Color frameColor;
	private Color backgroundColor;

	//Constructors
	private ScrollableFrame() {}

	public ScrollableFrame(UIElement[] elements, int x, int y, int w, int h, int d, int sBW, int spcng, int lMargin, int tMargin, int bMargin, Color fColor, Color bgColor) {
		focus = null;
		width = w;
		height = h;
		depth = d;
		scrollBarWidth = sBW;
		spacing = spcng;
		leftMargin = lMargin;
		topMargin = tMargin;
		bottomMargin = bMargin;
		setElements(elements);
		frameColor = fColor;
		backgroundColor = bgColor;
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics)
	public void render(Graphics g) {
		//Draw the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		//Draw the elements in the visible range, culled to be drawn only in this frame
		if (elements.length > 0) {
			g.setClip(x1, y1, width, height);
			for (int i = topIndex; i < bottomIndex; i++) {
				elements[i].render(g);
			}
			g.clearClip();
		}

		//Draw the scroll bar if there is one
		g.setColor(frameColor);
		if (maxScrolledAmount > 0)
			g.fillRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight);

		//Draw the frame
		g.drawRect(x1, y1, width, height);
	}

	//Calculate the scrolling fields
	private void calculateScrollingFields() {
		maxScrolledAmount = 0;
		maxScrolledAmount += topMargin;
		maxScrolledAmount += bottomMargin;
		maxScrolledAmount -= height;

		if (elements.length > 0) {
			for (int i = 0; i < elements.length; i++) {
				maxScrolledAmount += elements[i].getHeight();
			}
			maxScrolledAmount += (elements.length - 1) * spacing;
			scrollBarHeight = maxScrolledAmount / elements.length;
		}
	}
	
	//Add this as a listener to all elements that can change size.
	private void listenToAllDynamicDimensions() {
		for (int i = 0; i < elements.length; i++) {
			listenToDynamicDimensions(elements[i]);
		}
	}
	
	//Check if an element can change its size and if so listen to it.
	private void listenToDynamicDimensions(UIElement e) {
		if (e instanceof DynamicDimensions)
			((DynamicDimensions)e).addListener(this);
	}

	//Add an element to this
	public void addElement(UIElement e) {
		UIElement[] newElements = new UIElement[elements.length + 1];
		for (int i = 0; i < elements.length; i++) {
			newElements[i] = elements[i];
		}
		newElements[newElements.length - 1] = e;
		elements = newElements;
		listenToDynamicDimensions(e);
		calculateScrollingFields();
		setX(x1);
		setY(y1);
		updateVisibleRange();
	}

	//Set the elements to something
	public void setElements(UIElement[] newElements) {
		elements = newElements;
		scrolledAmount = 0;
		listenToAllDynamicDimensions();
		calculateScrollingFields();
		setX(x1);
		setY(y1);
		updateVisibleRange();
	}

	//Get the elements in this frame
	public UIElement[] getElements() {
		return elements;
	}

	//@see UIElement.setX(int)
	public void setX(int x) {
		x1 = x;
		x2 = x1 + width;
		scrollBarX = x2 - scrollBarWidth;
		//set the list stuff...
		x += leftMargin;
		for (UIElement e : elements) {
			e.setX(x);
		}
	}

	public int getX() {
		return x1;
	}

	//@see UIElement.setY(int)
	public void setY(int y) {
		scrollBarY = (scrollBarY - y1) + y;
		y1 = y;
		y2 = y1 + height;
		//set the list stuff...
		y += topMargin;
		for (int i = 0; i < elements.length; i++) {
			elements[i].setY(y);
			y += elements[i].getHeight();
			y += spacing;
		}
	}

	public int getY() {
		return y1;
	}

	//@see UIElement.getWidth()
	public int getWidth() {
		return width;
	}

	//@see UIElement.getHeight()
	public int getHeight() {
		return height;
	}

	//@see UIElement.pointIsWithin(int, int)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	//@see UIElement.click(int, int)
	public void click(int x, int y) {
		//Reset the focus
		focus = null;
		//If there is a scroll bar and the click was on it, scroll and clickedElsewhere the elements.
		//Basically we set the center of the scroll bar to where the bar was clicked.
		if (maxScrolledAmount > 0 && x >= scrollBarX) {
			scroll((scrollBarY - y + scrollBarHeight / 2) * height / scrollBarHeight);
			for (UIElement e : elements) {
				e.clickedElsewhere();
			}
		}
		//Otherwise, click an element and clickedElsewhere the other elements.
		else if (elements.length > 0) {
			int i;
			for (i = 0; i < topIndex; i++) {
				elements[i].clickedElsewhere();
			}
			for (i = topIndex; i < bottomIndex; i++) {
				if (elements[i].pointIsWithin(x, y)) {
					elements[i].click(x, y);
					focus = elements[i];
					break;
				}
				else {
					elements[i].clickedElsewhere();
				}
			}
			for (i++; i < elements.length; i++) {
				elements[i].clickedElsewhere();
			}
		}
	}

	//@see UIElement.hover(int, int)
	public void hover(int x, int y) {
		//if we're not on the scroll bar, hover on an element
		if ((maxScrolledAmount <= 0 || x < scrollBarX) && (elements.length > 0)) {
			int i;
			for (i = 0; i < topIndex; i++) {
				elements[i].hoveredElsewhere();
			}
			for (i = topIndex; i < bottomIndex; i++) {
				if (elements[i].pointIsWithin(x, y)) {
					elements[i].hover(x, y);
					break;
				}
				else {
					elements[i].hoveredElsewhere();
				}
			}
			for (i++; i < elements.length; i++) {
				elements[i].hoveredElsewhere();
			}
		}
	}

	//@see UIElement.clickedElsewhere()
	public void clickedElsewhere() {
		//call the clickedElsewhere of the elements.
		for (UIElement e : elements) {
			e.clickedElsewhere();
		}
	}

	//@see UIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
		//call the hoveredElsewhere of the elements.
		for (UIElement e : elements) {
			e.hoveredElsewhere();
		}
	}

	//@see UIElement.getDepth()
	public int getDepth() {
		return depth;
	}

	//@see UIElement.keyPressed(int, char)
	public void keyPressed(int key, char c) {
		//Arrows scroll
		if (maxScrolledAmount > 0) {
			if (key == Input.KEY_DOWN) {
				//scroll down
				scroll(-10);
			}
			else if (key == Input.KEY_UP) {
				//scroll up
				scroll(10);
			}
		}
		//Notify the focus
		if (focus != null) {
			focus.keyPressed(key, c);
		}
	}

	//@see UIElement.keyInputDone()
	public void keyInputDone() {
		if (focus != null)
			focus.keyInputDone();
	}

	//@see UIElement.dead()
	public boolean dead() {
		return false;
	}

	//Scroll some amount of pixels. Negative amounts are down, positive amounts are up.
	private void scroll(int amount) {
		//No sense scrolling if the frame is tall enough to display all the elements.
		//if (maxScrolledAmount > 0) {
			//Adjust the amount so we can't scroll out of bounds
			if (scrolledAmount - amount < 0)
				amount = scrolledAmount;
			else if (scrolledAmount - amount > maxScrolledAmount)
				amount = scrolledAmount - maxScrolledAmount;
			//Shift the elements and find the new top and bottom indexes
			for (UIElement e : elements) {
				e.setY(e.getY() + amount);
			}
			updateVisibleRange();
			//Move the scroll bar
			scrolledAmount -= amount;
			scrollBarY = y1 + (scrolledAmount * (height - scrollBarHeight)) / maxScrolledAmount;
		//}
	}

	//Update topIndex and bottomIndex.
	private void updateVisibleRange() {
		topIndex = 0;
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getY() < y1)
				topIndex = i;
			else
				break;
		}
		bottomIndex = 0;
		for (int i = topIndex; i < elements.length; i++) {
			if (elements[i].getY() <= y2)
				bottomIndex = i;
			else {
				break;
			}
		}
		bottomIndex++;
		System.out.println(topIndex + " -> " + bottomIndex);
	}
	
	//@see UIEventListener.uiEvent(UIEvent)
	public void uiEvent(UIEvent event) {
		//If an element has changed size, update the scroll stuff.
		if (event.getData() == event.getGenerator() && event.getGenerator() instanceof DynamicDimensions) {
			calculateScrollingFields();
			setY(y1);
			updateVisibleRange();
		}
	}

}