//A console-like display that can be written to.
//TODO: Add newline character support.

package org.haferlib.slick.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import org.haferlib.slick.GraphicsUtils;

import java.awt.Point;

public class OutputFrame extends GUISubcontext {
	
	public static final int MAX_IMAGE_HEIGHT = 512; //Textures in OpenGL only like to be so big. This helps keep the size down.
	
	private int x2, y2;							//The bottom right corner position.
	private int width, height;					//The dimensions.
	private int depth;							//The display depth.
	private boolean dead;						//Is this dead?
	private Font font;							//The font we render with.
	private Color textColor;					//The color we render the text as.
	private ScrollableListFrame scrollFrame;	//The scrollable list frame that the output is displayed in.
	private StringBuilder currentLine;			//A string builder to build the current line.
	private ImageFrame currentImageFrame;		//The image frame currently being drawn to.
	private Graphics imageG;					//The graphics for drawing to the image frame.
	private GraphicsUtils gUtils;				//The graphics utils class for word wrapping.
	private Point drawPoint;					//The position we are drawing to on the current image frame.
	
	//Constructor.
	public OutputFrame(int x, int y, int width, int height, int depth, Font font, Color textColor, int scrollBarWidth, Color scrollBarColor) throws SlickException {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		this.depth = depth;
		dead = false;
		this.font = font;
		this.textColor = textColor;
		scrollFrame = new ScrollableListFrame(x1, y1, this.width, this.height, 0, scrollBarWidth, scrollBarColor);
		subcontext.addElement(scrollFrame);
		makeNewCurrentImageFrame();
		gUtils = new GraphicsUtils();
		currentLine = new StringBuilder();
	}
	
	//Print an object to this.
	public void print(Object o) {
		currentLine.append(o);
		flush();
	}
	
	//Print a character to this.
	public void print(char c) {
		currentLine.append(c);
		flush();
	}
	
	//Print a double to this.
	public void print(double d) {
		currentLine.append(d);
		flush();
	}
	
	//Print a long to this.
	public void print(long l) {
		currentLine.append(l);
		flush();
	}
	
	//Print a float to this.
	public void print(float f) {
		currentLine.append(f);
		flush();
	}
	
	//Print an int to this.
	public void print(int i) {
		currentLine.append(i);
		flush();
	}
	
	//Print a short to this.
	public void print(short s) {
		currentLine.append(s);
		flush();
	}
	
	//Print a byte to this.
	public void print(byte b) {
		currentLine.append(b);
		flush();
	}
	
	//Print a boolean to this.
	public void print(boolean b) {
		currentLine.append(b);
		flush();
	}
	
	//Print an object to this and then add a new line.
	public void println(Object o) {
		print(o);
		newLine();
	}
	
	//Print a character to this and then add a new line.
	public void println(char c) {
		print(c);
		newLine();
	}
		
	//Print a double to this and then add a new line.
	public void println(double d) {
		print(d);
		newLine();
	}
		
	//Print a long to this and then add a new line.
	public void println(long l) {
		print(l);
		newLine();
	}
		
	//Print a float to this and then add a new line.
	public void println(float f) {
		print(f);
		newLine();
	}
		
	//Print an int to this and then add a new line.
	public void println(int i) {
		print(i);
		newLine();
	}
		
	//Print a short to this and then add a new line.
	public void println(short s) {
		print(s);
		newLine();
	}
		
	//Print a byte to this and then add a new line.
	public void println(byte b) {
		print(b);
		newLine();
	}
		
	//Print a boolean to this and then add a new line.
	public void println(boolean b) {
		print(b);
		newLine();
	}
	
	//Print a new line.
	public void println() {
		newLine();
	}
	
	//Make a new line happen.
	private void newLine() {
		//Move the draw position and resize the current image frame.
		drawPoint.x = 0;
		int dY = font.getLineHeight();
		drawPoint.y += dY;
		expandCurrentImageFrame(dY);
	}
	
	//Flush the current line to the image.
	private void flush() {
		//Dead frames don't bother with flushing.
		if (dead) 
			return;
		
		String line = currentLine.toString(); //Get the line.
		currentLine.delete(0, currentLine.length()); //Clear the string builder.

		Point lastDrawPoint = drawPoint;
		drawPoint = gUtils.drawStringWrapped(imageG, line, drawPoint.x, drawPoint.y, currentImageFrame.getWidth() - drawPoint.x); //Draw the string.
		imageG.flush(); //Flush the graphics;
		//Expand the frame.
		int expandAmount = drawPoint.y - ((lastDrawPoint.y < 0)? 0 : lastDrawPoint.y);
		boolean newFrame = expandCurrentImageFrame(expandAmount);
		//Draw any bits that got cut off.
		if (newFrame) {
			drawPoint = lastDrawPoint;
			drawPoint.y -= MAX_IMAGE_HEIGHT;
			currentLine.append(line);
			flush();
		}
	}
	
	//Expand the current image frame by some amount, creating a new one if necessary.
	private boolean expandCurrentImageFrame(int amount) {
		currentImageFrame.setHeight(currentImageFrame.getHeight() + amount);
		//If the current image frame has gotten too tall, make a new one.
		if (currentImageFrame.getHeight() >= MAX_IMAGE_HEIGHT) {
			imageG.destroy();
			try {
				makeNewCurrentImageFrame();
				return true;
			}
			catch (SlickException e) {
				//If this exception is thrown, the frame stops accepting input and marks itself for death.
				e.printStackTrace();
				imageG = null;
				dead = true;
				return false;
			}
		}
		scrollFrame.recalculateListHeight();
		return false;
	}
	
	//Create a new current image frame.
	private void makeNewCurrentImageFrame() throws SlickException {
		Image frameImage = Image.createOffscreenImage(width - scrollFrame.getScrollBarWidth(), MAX_IMAGE_HEIGHT);
		imageG = frameImage.getGraphics();
		imageG.setFont(font);
		imageG.setColor(textColor);
		imageG.setAntiAlias(false);
		currentImageFrame = new ImageFrame(frameImage, 0, 0, frameImage.getWidth(), font.getLineHeight(), 0);
		drawPoint = new Point();
		scrollFrame.addElement(currentImageFrame);
	}

	@Override
	public void render(Graphics g) {
		//Render the subcontext.
		renderSubcontext(g, x1, y1, x2, y2);
	}

	@Override
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}
	
	@Override
	public void setX(int x) {
		super.setX(x);
		x2 = x1 + width;
	}
	
	@Override
	public void setY(int y) {
		super.setY(y);
		y2 = y1 + height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setHeight(int h) {
		height = h;
		y2 = y1 + height;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
	}
	
	@Override
	public void setDepth(int d) {
		depth = d;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean dead() {
		return dead;
	}

}
