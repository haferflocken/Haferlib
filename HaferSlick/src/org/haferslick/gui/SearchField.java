package org.haferslick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * A text field that can autocomplete to strings that start with the typed text.
 * 
 * @author John Werner
 *
 */

public class SearchField extends TextField {
	
	protected String[] searchStrings;
	protected Color searchColor;
	protected String foundString;
	protected int foundStringX, foundStringY;
	protected String[] foundStringDisplay;

	public SearchField(int x, int y, int width, int height, int depth,
			String startText, String backgroundMessage, String[] searchStrings, Font font,
			Color textColor, Color backgroundMessageColor, Color searchColor, Color backgroundColor) {
		super(x, y, width, height, depth, startText, backgroundMessage, font,
				textColor, backgroundMessageColor, backgroundColor);
		
		setSearchStrings(searchStrings);
		setSearchColor(searchColor);
		foundString = null;
	}
	
	/**
	 * Set the strings to search through.
	 * 
	 * @param strings The strings to search through.
	 * @throws IllegalArgumentException if strings is null.
	 */
	public void setSearchStrings(String[] strings) {
		if (strings == null)
			throw new IllegalArgumentException("Search strings cannot be null.");
		searchStrings = strings;
	}
	
	/**
	 * Set the serach color.
	 * 
	 * @param color The search color.
	 * @throws IllegalArgumentException if color is null.
	 */
	public void setSearchColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException("Search color cannot be null.");
		searchColor = color;
	}
	
	/**
	 * Override rewrapText() to also wrap the found string. This is done because wrapping the found
	 * string is inherently dependent on the display strings that are wrapped by super.rewrapText().
	 */
	@Override
	protected void rewrapText() {
		super.rewrapText();
		if (foundString == null)
			return;

		// Find the end dimensions of the text display.
		int tDLastWidth = font.getWidth(textDisplay[textDisplay.length - 1]);
		int tDLastYOffset = font.getLineHeight() * (textDisplay.length - 1);
		
		// Set foundStringX and Y appropriately.
		foundStringX = x1 + tDLastWidth;
		foundStringY = y1 + tDLastYOffset;
		
		// Wrap the first line first because it is shorter than the others.
		int remainingWidth = width - tDLastWidth;
		String firstLine = wordWrapper.getSubstringOfWidth(font, foundString, remainingWidth, 0);
		
		// Wrap the remaining lines if needed.
		if (firstLine.length() < foundString.length()) {
			String remainder = foundString.substring(firstLine.length());
			String[] wrappedRemainder = wordWrapper.wordWrap(font, remainder, width);
			foundStringDisplay = new String[wrappedRemainder.length + 1];
			foundStringDisplay[0] = remainder;
			for (int i = 1; i < foundStringDisplay.length; i++) {
				foundStringDisplay[i] = wrappedRemainder[i - 1];
			}
		}
		// Otherwise, just display the first line.
		else
			foundStringDisplay = new String[] { firstLine };
	}
	
	@Override
	protected void drawText(Graphics g) {
		super.drawText(g);
		
		// Display the found string display.
		if (foundString != null) {
			g.setColor(Color.red);
			g.drawString(foundStringDisplay[0], foundStringX, foundStringY);
			for (int i = 1; i < foundStringDisplay.length; i++) {
				g.drawString(foundStringDisplay[i], x1, foundStringY + font.getLineHeight() * i);
			}
		}
	}
	
	@Override
	public void keyPressed(int key, char c) {
		// Enter sets the text to the predicted text.
		if (key == Input.KEY_ENTER) {
			text.append(foundString);
			cursor = text.length();
			cursorFlash = true;
			cursorFlashCounter = 0;
		}
		// Handle other keys.
		else
			super.keyPressed(key, c);
	}
	
	@Override
	public void keyInputDone() {
		foundString = null;
		if (text.length() > 0) {
			String textString = text.toString();
			for (String s : searchStrings) {
				if (s.startsWith(textString)) {
					foundString = s.substring(text.length());
					break;
				}
			}
		}
		
		super.keyInputDone();
	}

}
