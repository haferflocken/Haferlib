//A console-like display that can be written to.
//TODO: Add newline character support.

package org.haferslick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class OutputFrame extends GUISubcontext implements Appendable {
	
	private TextDisplay textDisplay;			// The element that displays the output.
	private ScrollableListFrame scrollFrame;	// The scrollable list frame that the output is displayed in.
	private StringBuilder contents;				// A string builder to build the output.
	
	//Constructor.
	public OutputFrame(int x, int y, int width, int height, int depth, Font font, Color textColor, int scrollBarWidth, Color scrollBarColor) {
		super(x, y, width, height, depth);

		scrollFrame = new ScrollableListFrame(x1, y1, this.width, this.height, 0, scrollBarWidth, scrollBarColor);
		subcontext.addElement(scrollFrame);
		
		textDisplay = new TextDisplay(x1, y1, scrollFrame.getWidth() - scrollFrame.getScrollBarWidth(), Integer.MAX_VALUE, 0, "", font, textColor);
		textDisplay.addListener(scrollFrame);
		scrollFrame.addElement(textDisplay);
		
		contents = new StringBuilder();
	}
	
	// Append to this.
	@Override
	public Appendable append(char c) {
		contents.append(c);
		textDisplay.setText(contents.toString());
		return this;
	}
	
	@Override
	public Appendable append(CharSequence csq) {
		contents.append(csq);
		textDisplay.setText(contents.toString());
		return this;
	}
	
	@Override
	public Appendable append(CharSequence csq, int start, int end) {
		contents.append(csq, start, end);
		textDisplay.setText(contents.toString());
		return this;
	}
	
	// Print an object to this.
	public void print(Object o) {
		contents.append(o);
		textDisplay.setText(contents.toString());
	}
	
	// Print a character to this.
	public void print(char c) {
		contents.append(c);
		textDisplay.setText(contents.toString());
	}
	
	// Print a double to this.
	public void print(double d) {
		contents.append(d);
		textDisplay.setText(contents.toString());
	}
	
	// Print a long to this.
	public void print(long l) {
		contents.append(l);
		textDisplay.setText(contents.toString());
	}
	
	// Print a float to this.
	public void print(float f) {
		contents.append(f);
		textDisplay.setText(contents.toString());
	}
	
	// Print an int to this.
	public void print(int i) {
		contents.append(i);
		textDisplay.setText(contents.toString());
	}
	
	// Print a short to this.
	public void print(short s) {
		contents.append(s);
		textDisplay.setText(contents.toString());
	}
	
	// Print a byte to this.
	public void print(byte b) {
		contents.append(b);
	}
	
	// Print a boolean to this.
	public void print(boolean b) {
		contents.append(b);
	}
	
	// Print an object to this and then add a new line.
	public void println(Object o) {
		contents.append(o);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
	
	// Print a character to this and then add a new line.
	public void println(char c) {
		contents.append(c);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a double to this and then add a new line.
	public void println(double d) {
		contents.append(d);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a long to this and then add a new line.
	public void println(long l) {
		contents.append(l);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a float to this and then add a new line.
	public void println(float f) {
		contents.append(f);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print an int to this and then add a new line.
	public void println(int i) {
		contents.append(i);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a short to this and then add a new line.
	public void println(short s) {
		contents.append(s);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a byte to this and then add a new line.
	public void println(byte b) {
		contents.append(b);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
		
	// Print a boolean to this and then add a new line.
	public void println(boolean b) {
		contents.append(b);
		contents.append("\n");
		textDisplay.setText(contents.toString());
	}
	
	// Print a new line.
	public void println() {
		println("\n");
	}
	
	@Override
	public void render(Graphics g) {
		// Render the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}

}
