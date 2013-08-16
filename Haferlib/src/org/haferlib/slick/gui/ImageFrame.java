// A frame that displays an image, stretched to fit the frame.

package org.haferlib.slick.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ImageFrame extends AbstractRectangularElement {
	
	private Image image;		// The image in the frame.
	
	// Constructors.
	public ImageFrame(Image image, int x, int y, int depth) {
		this(image, x, y, image.getWidth(), image.getHeight(), depth);
	}
	
	public ImageFrame(Image image, int x, int y, int width, int height, int depth) {
		super(x, y, width, height, depth);
		this.image = image;
	}
	
	// Get the image.
	public Image getImage() {
		return image;
	}

	@Override
	public void update(int delta) { }

	@Override
	public void render(Graphics g) {
		g.drawImage(image, x1, y1, x2, y2, 0, 0, width, height);
	}

	@Override
	public void click(int x, int y, int button) { }

	@Override
	public void mouseDown(int x, int y, int button) { }

	@Override
	public void hover(int x, int y) { }

	@Override
	public void clickedElsewhere(GUIElement target, int button) { }

	@Override
	public void mouseDownElsewhere(GUIElement target, int button) { }

	@Override
	public void hoveredElsewhere(GUIElement target) { }

	@Override
	public void keyPressed(int key, char c) { }

	@Override
	public void keyInputDone() { }

	@Override
	public void destroy() { }

}
