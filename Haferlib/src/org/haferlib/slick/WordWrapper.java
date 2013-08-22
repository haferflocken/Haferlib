package org.haferlib.slick;

import org.newdawn.slick.Font;

import java.util.ArrayList;

/**
 * A class that exists to word wrap strings.
 * 
 * @author John Werner
 *
 */

public class WordWrapper {
	
	/**
	 * Word wrap a string.
	 * 
	 * @param font The font to wrap the string with.
	 * @param string The string to be wrapped.
	 * @param maxWidth The maximum width of each line.
	 * @return An array of strings where each element represents a line of the wrapped string.
	 */
	public String[] wordWrap(Font font, String string, int maxWidth) {
		// First, check if we need to wrap the string.
		// If we do, break it down into substrings where each is a line.
		if (font.getWidth(string) > maxWidth || string.indexOf('\n') != -1) {
			ArrayList<String> wrappedStrings = new ArrayList<>();
			String substring;
			int startIndex = 0;		// The start index of this line.
			int lastStartWord = 0;	// The index of the last word start
									// encountered.
			int currentWidth;
			char curChar = string.charAt(0);
			char prevChar;
			// Check the length of substrings until we reach the max width.
			for (int i = 1; i < string.length(); i++) {
				substring = string.substring(startIndex, i);
				currentWidth = font.getWidth(substring);
				prevChar = curChar;
				curChar = string.charAt(i);
				// Keep track of where words start.
				if (curChar == '\n' || !Character.isWhitespace(curChar) && Character.isWhitespace(prevChar))
					lastStartWord = i;
				// If we passed over the edge, cut the string off and move on to
				// the next line.
				if (currentWidth > maxWidth || curChar == '\n') {
					substring = string.substring(startIndex, lastStartWord);
					wrappedStrings.add(substring);
					startIndex = lastStartWord;
				}
			}
			// Get the last line if we need to.
			if (startIndex < string.length()) {
				substring = string.substring(startIndex);
				wrappedStrings.add(substring);
			}
			return wrappedStrings.toArray(new String[wrappedStrings.size()]);
		}
		// If we don't need to wrap the string, return it in an array.
		return new String[] { string };
	}

}
