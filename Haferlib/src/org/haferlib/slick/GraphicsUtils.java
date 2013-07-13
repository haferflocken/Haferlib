package org.haferlib.slick;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Font;

public class GraphicsUtils {
	
	//Constructor.
	public GraphicsUtils() {
	}
	
	//Draw a string with word wrap then return the bottom y coordinate of it.
	public int drawStringWrapped(Graphics g, String string, int x, int y, int maxWidth) {
		Font font = g.getFont(); //Get the font. We need that shit.

		//First, check if we need to wrap the string.
		//If we do...
		if (font.getWidth(string) > maxWidth) {
			//Break it down by substrings.
			String substring;
			int startIndex = 0; //The start index of this line.
			int lastStartWord = 0; //The index of the last word start encountered.
			int currentWidth;
			//Check the length of substrings until we reach the max width.
			for (int i = 1; i < string.length(); i++) {
				substring = string.substring(startIndex, i);
				currentWidth = font.getWidth(substring);
				//Keep track of where words start.
				if (!Character.isWhitespace(string.charAt(i)) && Character.isWhitespace(string.charAt(i - 1)))
					lastStartWord = i;
				//If we passed over the edge, cut the string off and move on to the next line.
				if (currentWidth > maxWidth) {
					substring = string.substring(startIndex, lastStartWord);
					g.drawString(substring, x, y);
					y += font.getLineHeight();
					startIndex = lastStartWord;
				}
			}
			//Draw the last line if we need to.
			if (startIndex < string.length()) {
				g.drawString(string.substring(startIndex), x, y);
				y += font.getLineHeight();
			}
			return y;
		}
		//If we don't need to wrap the string, just draw it.
		g.drawString(string, x, y);
		return y + font.getLineHeight();
	}
	
	//For those times when you need to know how tall a word wrapped string will be without drawing it,
	//here is a method for exactly that.
	public int getWrappedStringHeight(Font font, String string, int maxWidth) {
		int y = 0; //Make a variable to keep track of the height.
		
		// First, check if we need to wrap the string.
		// If we do...
		if (font.getWidth(string) > maxWidth) {
			// Break it down by substrings.
			String substring;
			int startIndex = 0; // The start index of this line.
			int lastStartWord = 0; // The index of the last word start encountered.
			int currentWidth;
			// Check the length of substrings until we reach the max width.
			for (int i = 1; i < string.length(); i++) {
				substring = string.substring(startIndex, i);
				currentWidth = font.getWidth(substring);
				// Keep track of where words start.
				if (!Character.isWhitespace(string.charAt(i))
						&& Character.isWhitespace(string.charAt(i - 1)))
					lastStartWord = i;
				// If we passed over the edge, cut the string off and move on to
				// the next line.
				if (currentWidth > maxWidth) {
					substring = string.substring(startIndex, lastStartWord);
					y += font.getLineHeight();
					startIndex = lastStartWord;
				}
			}
			// Draw the last line if we need to.
			if (startIndex < string.length()) {
				y += font.getLineHeight();
			}
			return y;
		}
		// Return the height.
		return y + font.getLineHeight();
	}

}
