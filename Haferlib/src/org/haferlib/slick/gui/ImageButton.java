package org.haferlib.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ImageButton<V> extends Button<V> {

	private Image image;
	private byte imageAlignment;
	private int imageXOffset;

	public ImageButton(V data, int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key,
			Image image, byte imageAlignment, int imageXOffset) {
		super(data, x, y, width, height, depth, backgroundColor, highlightColor, key);
		
		this.image = image;
		this.imageAlignment = imageAlignment;
		this.imageXOffset = imageXOffset;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		
		// Draw the image.
		if (imageAlignment == LEFT)
			g.drawImage(image, x1 + imageXOffset, centerY - image.getHeight() / 2);
		else if (imageAlignment == CENTER)
			g.drawImage(image, centerX - image.getWidth() / 2 + imageXOffset,
					centerY - image.getHeight() / 2);
		else
			g.drawImage(image, x2 - image.getWidth() + imageXOffset,
					centerY - image.getHeight() / 2);
	}
	
}
