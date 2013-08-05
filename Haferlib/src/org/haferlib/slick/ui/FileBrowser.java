//Visually shows a folder, its subfolders, their subfolders, and so forth. Also shows a current selected folder's files.

package org.haferlib.slick.ui;

import org.haferlib.util.FileTree;
import org.haferlib.util.NAryTree;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.io.FileFilter;
import java.io.File;

public class FileBrowser implements UIElement, UIEventListener, UIEventGenerator {

	private String title;
	private FileTree fileTree;
	private ScrollableFrame folderPane;
	private int folderPaneWidth;
	private ScrollableFrame filePane;
	private int filePaneWidth;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int centerX;
	private int centerY;
	private int width;
	private int height;
	private Color textColor;
	private Color backgroundColor;
	private Color highlightColor;
	private boolean dead;
	private int buttonKey;
	private HashSet<UIEventListener> listeners;

	public FileBrowser(String title, String filePath, String extension, Font f, int x, int y, int w, int h, Color tColor, Color bgColor, Color hColor, int key) {
		this.title = title;
		fileTree = new FileTree(filePath, extension);
		width = w;
		folderPaneWidth = width / 4;
		filePaneWidth = width - folderPaneWidth;
		height = h;
		setX(x);
		setY(y);
		textColor = tColor;
		backgroundColor = bgColor;
		highlightColor = hColor;
		dead = false;
		buttonKey = key;
		listeners = new HashSet<>();
		buildFolderPane(f);
	}

	private void buildFolderPane(Font f) {
		//Using the file tree, build a list of buttons and place them in a scrollable frame
		int folderPaneY = y1 + f.getLineHeight();
		int folderPaneHeight = height - f.getLineHeight();

		/*ArrayDeque<NAryTree.Node> deque = new ArrayDeque<>();
		ArrayList<Button<ScrollableFrame>> buttons = new ArrayList<>();
		NAryTree.Node current;
		deque.addAll(fileTree.getRoot().getChildren());
		while (deque.size() > 0) {
			//Move through the tree
			current = deque.pop();
			for (int i = 0; i < current.getNumChildren(); i++) {
				deque.push((NAryTree.Node)current.getChildren().get(i));
			}

			//Make the file pane
			File[] files = (File[])current.getValue();
			UIElement[] fileButtons = new UIElement[files.length];
			for (int i = 0; i < files.length; i++) {
				String buttonName = files[i].getName();
				Button<File> filePaneButton = new Button<File>(buttonName, files[i], textColor, Button.LEFT, 8, 0, 0, filePaneWidth, f.getLineHeight(), backgroundColor, highlightColor, -1);
				filePaneButton.addListener(this);
				fileButtons[i] = filePaneButton;
			}
			ScrollableFrame buttonFilePane = new ScrollableFrame(fileButtons, x1 + folderPaneWidth, folderPaneY, filePaneWidth, folderPaneHeight, 0, 8, 0, 0, 0, 0, textColor, backgroundColor);

			//Make the button
			Button<ScrollableFrame> button = new Button<ScrollableFrame>(current.getKey(), buttonFilePane, textColor, Button.LEFT, current.getDepth() * 8, 0, 0, folderPaneWidth, f.getLineHeight(), backgroundColor, highlightColor, -1);
			button.addListener(this);
			buttons.add(button);
		}

		UIElement[] folderPaneElements = new UIElement[buttons.size()];
		for (int i = 0; i < buttons.size(); i++)
			folderPaneElements[i] = buttons.get(i);*/

		folderPane = new ScrollableFrame(new UIElement[0], x1, folderPaneY, folderPaneWidth, folderPaneHeight, 0, 8, 0, 0, 0, 0, textColor, backgroundColor);
		filePane = new ScrollableFrame(new UIElement[0], x1 + folderPaneWidth, folderPaneY, filePaneWidth, folderPaneHeight, 0, 8, 0, 0, 0 ,0, textColor, backgroundColor);
	}

	//@see UIElement.update(int)
	public void update(int delta) {
	}

	//@see UIElement.render(Graphics)
	public void render(Graphics g) {
		//Fill the background
		g.setColor(backgroundColor);
		g.fillRect(x1, y1, width, height);
		g.setColor(textColor);
		g.drawRect(x1, y1, width, height);

		//Draw the title bar
		g.drawString(title, centerX - g.getFont().getWidth(title)/2, y1);
		int titleBarLineY = y1 + g.getFont().getLineHeight();
		g.drawLine(x1, titleBarLineY, x2, titleBarLineY);

		//Draw the folder pane
		folderPane.render(g);

		//Draw the file pane
		filePane.render(g);
	}

	//@see UIElement.setX(int)
	public void setX(int x) {
		x1 = x;
		x2 = x + width;
		centerX = (x2 + x1)/2;
	}

	//@see UIElement.getX()
	public int getX() {
		return x1;
	}

	//@see UIElement.setY(int)
	public void setY(int y) {
		y1 = y;
		y2 = y + height;
	}

	//@see UIElement.getY()
	public int getY() {
		return y1;
	}

	//@see UIElement.getWidth()
	public int getWidth() {
		return width;
	}

	//@see UIElement.getHeight()
	public int getHeight() {
		return height;
	}

	//@see UIElement.pointIsWithin(int, int)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	//@see UIElement.click(int, int)
	public void click(int x, int y) {
		if (x < x1 + folderPaneWidth) {
			folderPane.click(x, y);
			filePane.clickedElsewhere();
		}
		else {
			filePane.click(x, y);
			folderPane.clickedElsewhere();
		}
	}

	//@see UIElement.hover(int, int)
	public void hover(int x, int y) {
		if (x < x1 + folderPaneWidth) {
			folderPane.hover(x, y);
			filePane.hoveredElsewhere();
		}
		else {
			filePane.hover(x, y);
			folderPane.hoveredElsewhere();
		}
	}

	//@see UIElement.clickedElsewhere()
	public void clickedElsewhere() {
		folderPane.clickedElsewhere();
		filePane.clickedElsewhere();
	}

	//@see UIElement.hoveredElsewhere()
	public void hoveredElsewhere() {
		folderPane.hoveredElsewhere();
		filePane.hoveredElsewhere();
	}

	//@see UIElement.getDepth()
	public int getDepth() {
		return 10;
	}

	//@see UIElement.keyPressed(int, char)
	public void keyPressed(int key, char c) {
	}

	//@see UIElement.keyInputDone()
	public void keyInputDone() {
	}

	//@see UIElement.dead()
	public boolean dead() {
		return dead;
	}

	//@see UIEventListener.uiEvent(UIEvent)
	public void uiEvent(UIEvent event) {
		//If it's from a button
		if (event.getGenerator() instanceof Button) {
			Object data = ((Button)event.getGenerator()).getData();
			//If it's from the folderPane
			if (data instanceof ScrollableFrame) {
				System.out.println("folder pane clicked");
				//Switch the file pane
				filePane = (ScrollableFrame)data;
			}
			//If it's from a button in the filePane
			else if (data instanceof File) {
				System.out.println("file pane clicked");
				//Get the file
				File f = (File)data;
				//Notify the listeners
				notifyListeners(f);
			}
			else {
				System.out.println("what");
			}
		}
	}

	private void notifyListeners(File file) {
		UIEvent<File> out = new UIEvent<>(this, file);
		for (UIEventListener l : listeners) {
			l.uiEvent(out);
		}
		dead = true;
	}

	//@see UIEventGenerator.addListener(UIEventListener)
	public void addListener(UIEventListener l) {
		listeners.add(l);
	}

	//@see UIEventGenerator.removeListener(UIEventListener)
	public void removeListener(UIEventListener l) {
		listeners.remove(l);
	}

}