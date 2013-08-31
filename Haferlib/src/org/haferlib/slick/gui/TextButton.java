package org.haferlib.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class TextButton<V> extends Button<V> {
	
	private String text;
	private Color textColor;
	private Font font;
	private byte textAlignment;
	private int textXOffset;

	public TextButton(V data, int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key,
			String text, Color textColor, Font font, byte textAlignment, int textXOffset) {
		super(data, x, y, width, height, depth, backgroundColor, highlightColor, key);
		
		this.text = text;
		this.textColor = textColor;
		this.font = font;
		this.textAlignment = textAlignment;
		this.textXOffset = textXOffset;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		
		// Draw the text.
		g.setColor(textColor);
		g.setFont(font);
		if (textAlignment == LEFT)
			g.drawString(text, x1 + textXOffset, centerY - font.getLineHeight()
					/ 2);
		else if (textAlignment == CENTER)
			g.drawString(text, centerX - g.getFont().getWidth(text) / 2
					+ textXOffset, centerY - font.getHeight(text) / 2);
		else
			g.drawString(text, x2 - g.getFont().getWidth(text) + textXOffset,
					centerY - font.getLineHeight() / 2);
	}
}
