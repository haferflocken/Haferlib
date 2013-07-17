package org.haferlib.slick;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Font;

import java.awt.Point;

public class GraphicsUtils {
	
	//Constructor.
	public GraphicsUtils() {
	}
	
	//REQUIRES:	font != null, string != null
	//EFFECTS:	If g != null, draws the string with word wrapping from the point (x, y) without crossing maxWidth.
	//			Returns the point at which it stops drawing.
	private Point stringWrap(Graphics g, Font font, String string, int x, int y, int maxWidth) {
		Point endPoint = new Point(x, y); //The end drawing point.

		//First, check if we need to wrap the string.
		//If we do...
		if (font.getWidth(string) > maxWidth || string.indexOf('\n') != -1) {
			//Break it down by substrings.
			String substring;
			int startIndex = 0; //The start index of this line.
			int lastStartWord = 0; //The index of the last word start encountered.
			int currentWidth;
			char curChar = string.charAt(0);
			char prevChar;
			//Check the length of substrings until we reach the max width.
			for (int i = 1; i < string.length(); i++) {
				substring = string.substring(startIndex, i);
				currentWidth = font.getWidth(substring);
				prevChar = curChar;
				curChar = string.charAt(i);
				//Keep track of where words start.
				if (curChar == '\n' || !Character.isWhitespace(curChar) && Character.isWhitespace(prevChar))
					lastStartWord = i;
				//If we passed over the edge, cut the string off and move on to the next line.
				if (currentWidth > maxWidth || curChar == '\n') {
					substring = string.substring(startIndex, lastStartWord);
					if (g != null)
						g.drawString(substring, x, y);
					endPoint.x = x + font.getWidth(substring);
					endPoint.y = y;
					y += font.getLineHeight();
					startIndex = lastStartWord;
				}
			}
			//Draw the last line if we need to.
			if (startIndex < string.length()) {
				substring = string.substring(startIndex);
				if (g != null)
					g.drawString(substring, x, y);
				endPoint.x = x + font.getWidth(substring);
				endPoint.y = y;
			}
			return endPoint;
		}
		//If we don't need to wrap the string, just draw it.
		if (g != null)
			g.drawString(string, x, y);
		endPoint.x = x + font.getWidth(string);
		endPoint.y = y;
		return endPoint;
	}
	
	//REQUIRES: g != null, string != null
	//EFFECTS:	Draw a string with word wrap then return the drawing point.
	public Point drawStringWrapped(Graphics g, String string, int x, int y, int maxWidth) {
		return stringWrap(g, g.getFont(), string, x, y, maxWidth);
	}
	
	//REQUIRES: font != null, string != null
	//EFFECTS:	Get the height of a word wrapped string without drawing it.
	public int getWrappedStringHeight(Font font, String string, int maxWidth) {
		int yEnd = stringWrap(null, font, string, 0, 0, maxWidth).y;
		return yEnd += font.getLineHeight();
	}

}
