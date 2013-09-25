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
	private int textX, textY;

	// Constructor.
	public TextButton(V data, int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key,
			String text, Color textColor, Font font, byte textAlignment, int textXOffset) {
		super(data, x, y, width, height, depth, backgroundColor, highlightColor, key);
		
		this.text = text;
		this.textColor = textColor;
		this.font = font;
		this.textAlignment = textAlignment;
		this.textXOffset = textXOffset;
		
		rethinkTextPos();
	}
	
	public TextButton(V data, int x, int y, int width, int height, int depth, Color backgroundColor, Color highlightColor, int key,
			String text, Color textColor, Font font) {
		this(data, x, y, width, height, depth, backgroundColor, highlightColor, key, text, textColor, font, CENTER, 0);
	}
	
	// Recalculate the position of the text.
	private void rethinkTextPos() {
		textY = centerY - font.getHeight(text) / 2;
		if (textAlignment == LEFT) 
			textX = x1;
		else if (textAlignment == CENTER) 
			textX = centerX - font.getWidth(text) / 2;
		else
			textX = x2 - font.getWidth(text);
		textX += textXOffset;
	}
	
	// Set the text.
	public void setText(String t) {
		text = t;
		rethinkTextPos();
	}
	
	// Set the font.
	public void setFont(Font f) {
		font = f;
		rethinkTextPos();
	}
	
	// Set the text alignment.
	public void setTextAlignment(byte tA) {
		textAlignment = tA;
		rethinkTextPos();
	}
	
	// Set the text x offset.
	public void setTextXOffset(int tXO) {
		textXOffset = tXO;
		rethinkTextPos();
	}
	
	@Override
	public void setX(int x) {
		int dX = x - x1;
		super.setX(x);
		textX += dX;
	}
	
	@Override
	public void setY(int y) {
		int dY = y - y1;
		super.setY(y);
		textY += dY;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		
		// Draw the text.
		g.setColor(textColor);
		g.setFont(font);
		g.drawString(text, textX, textY);
	}
}
