// A rectangular field that you can type in.
// Can display a "background message" when the field is empty.

package org.haferlib.slick.gui;

import org.haferlib.slick.WordWrapper;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

public class TextField extends AbstractRectangularElement {

	// An array of the characters that are allowed that aren't letters or digits.
	public static final char[] VALID_SYMBOLS =
		{ ' ', '!', '#', '$', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '=', '?', '@', '[', ']', '_', '~', '\\' };

	private static final int CURSOR_FLASH_PERIOD = 750; // How many miliseconds for each cursor flash.
	
	private StringBuilder text;
	private int cursor;
	private int cursorX, cursorY;
	private boolean cursorFlash;	// Whether the cursor is visible or not due to flashing.
	private int cursorFlashCounter;	// A counter to toggle cursorFlash.
	private boolean displayCursor;	// Whether or not we should display the cursor.
	
	private WordWrapper wordWrapper;
	private String[] textDisplay;
	private String unwrappedBackgroundMessage;
	private String[] backgroundMessage;
	
	private Font font;
	private Color textColor, backgroundColor, backgroundMessageColor;

	// Constructors.
	public TextField(int x, int y, int width, int height, int depth, String startText, String backgroundMessage,
			Font font, Color textColor, Color backgroundMessageColor, Color backgroundColor) {
		super(x, y, width, height, depth);
		text = new StringBuilder(startText);
		cursor = text.length();
		
		wordWrapper = new WordWrapper();
		unwrappedBackgroundMessage = backgroundMessage;
		setFont(font);
		
		cursorFlash = false;
		cursorFlashCounter = 0;
		displayCursor = false;
		
		this.textColor = textColor;
		this.backgroundMessageColor = backgroundMessageColor;
		this.backgroundColor = backgroundColor;
	}
	
	private void rewrapText() {
		textDisplay = wordWrapper.wordWrap(font, text.toString(), width);
	}
	
	private void rewrapBackgroundMessage() {
		if (unwrappedBackgroundMessage != null)
			backgroundMessage = wordWrapper.wordWrap(font, unwrappedBackgroundMessage, width);
	}
	
	private void rethinkCursorPos() {
		// The cursor needs to be visually placed among the display strings,
		// so find which one it is in.
		int sumLength = 0;
		for (int i = 0; i < textDisplay.length; i++) {
			sumLength += textDisplay[i].length();
			// If we have found the line, set the cursor position.
			if (cursor <= sumLength) {
				int prevLength = sumLength - textDisplay[i].length();
				int offset = cursor - prevLength;
				String lineToCursor = textDisplay[i].substring(0, offset);
				cursorX = x1 + font.getWidth(lineToCursor);
				cursorY = y1 + font.getLineHeight() * i;
				return;
			}
		}
		
		// If we fail to place the cursor, place it at the start.
		cursorX = x1;
		cursorY = y1;
	}
	
	// EFFECTS:  Determines if a key/char pair is a valid character to add to our string.
	private boolean isTextKey(int key, char c) {
		return (!Character.isISOControl(c));
	}
	
	public void setFont(Font f) {
		font = f;
		rewrapText();
		rewrapBackgroundMessage();
		rethinkCursorPos();
	}
	
	// EFFECTS: Clears this TextField's text.
	public void clear() {
		text.delete(0, text.length());
		textDisplay = new String[0];
	}

	@Override
	public void update(int delta) {
		cursorFlashCounter += delta;
		if (cursorFlashCounter > CURSOR_FLASH_PERIOD) {
			cursorFlash = !cursorFlash;
			cursorFlashCounter = 0;
		}
	}

	@Override
	public void render(Graphics g) {
		// Draw the background.
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);

		// Set the font.
		g.setFont(font);
		
		// Draw the background message if there's no text.
		if ((backgroundMessage != null) && (text.length() == 0)) {
			g.setColor(backgroundMessageColor);
			for (int i = 0; i < backgroundMessage.length; i++)
				g.drawString(backgroundMessage[i], x1, y1 + font.getLineHeight() * i);
		}
		// Or draw the text if there is text.
		else {
			g.setColor(textColor);
			for (int i = 0; i < textDisplay.length; i++)
				g.drawString(textDisplay[i], x1, y1 + font.getLineHeight() * i);
		}
		
		// Draw the cursor.
		if (displayCursor && cursorFlash) {
			g.setColor(textColor);
			g.fillRect(cursorX, cursorY, 2, font.getLineHeight());
		}
	}
	
	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		rewrapText();
		rewrapBackgroundMessage();
		rethinkCursorPos();
	}

	@Override
	public void click(int x, int y, int button) {
		displayCursor = true;
	}

	@Override
	public void mouseDown(int x, int y, int button) {
		displayCursor = true;
	}

	@Override
	public void hover(int x, int y) {
	}

	@Override
	public void clickedElsewhere(GUIElement target, int button) {
		displayCursor = false;
	}

	@Override
	public void mouseDownElsewhere(GUIElement target, int button) {
		displayCursor = false;
	}

	@Override
	public void hoveredElsewhere(GUIElement target) {
	}

	@Override
	public void keyPressed(int key, char c) {
		// If it's a text key, remember it.
		if (isTextKey(key, c)) {
			text.insert(cursor, c);
			cursor++;
		}
		// If it's a backspace, delete the character before the cursor.
		else if (key == Input.KEY_BACK) {
			if (text.length() > 0) {
				text.deleteCharAt(cursor - 1);
				cursor--;
			}
		}
		// If it's a delete, delete the character after the cursor.
		else if (key == Input.KEY_DELETE) {
			if (text.length() > cursor) {
				text.deleteCharAt(cursor);
			}
		}
		// Left arrow moves the cursor left.
		else if (key == Input.KEY_LEFT) {
			if (cursor > 0)
				cursor--;
		}
		// Right arrow moves the cursor right.
		else if (key == Input.KEY_RIGHT) {
			if (cursor < text.length())
				cursor++;
		}
	}

	@Override
	public void keyInputDone() {
		rewrapText();
		rethinkCursorPos();
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public String toString() {
		return text.toString();
	}
}