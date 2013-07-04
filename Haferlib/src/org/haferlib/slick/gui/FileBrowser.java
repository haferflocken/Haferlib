//Visually shows a folder, its subfolders, their subfolders, and so forth. Also shows a current selected folder's files.

package org.haferlib.slick.gui;

import org.haferlib.util.FileTree;
import org.haferlib.util.NAryTree;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.io.File;

public class FileBrowser extends GUISubcontext implements GUIEventListener, GUIEventGenerator {

	private String title;
	private FileTree fileTree;
	private ScrollableFrame folderPane;
	private int folderPaneWidth;
	private ScrollableFrame filePane;
	private int filePaneWidth;
	private int x2, y2;
	private int centerX, centerY;
	private int width, height;
	private Color textColor;
	private Color backgroundColor;
	private Color highlightColor;
	private boolean dead;
	private int buttonKey;
	private HashSet<GUIEventListener> listeners;

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

		ArrayDeque<NAryTree.Node> deque = new ArrayDeque<>();
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
			GUIElement[] fileButtons = new GUIElement[files.length];
			for (int i = 0; i < files.length; i++) {
				String buttonName = files[i].getName();
				Button<File> filePaneButton = new Button<File>(buttonName, files[i], textColor, Button.LEFT, 8, 0, 0, filePaneWidth, f.getLineHeight(), backgroundColor, highlightColor, -1);
				filePaneButton.addListener(this);
				fileButtons[i] = filePaneButton;
			}
			ScrollableFrame buttonFilePane = new ScrollableFrame(fileButtons, x1 + folderPaneWidth, folderPaneY, filePaneWidth, folderPaneHeight, 0, 8, textColor);

			//Make the button
			Button<ScrollableFrame> button = new Button<ScrollableFrame>(current.getKey(), buttonFilePane, textColor, Button.LEFT, current.getDepth() * 8, 0, 0, folderPaneWidth, f.getLineHeight(), backgroundColor, highlightColor, -1);
			button.addListener(this);
			buttons.add(button);
		}

		GUIElement[] folderPaneElements = new GUIElement[buttons.size()];
		for (int i = 0; i < buttons.size(); i++)
			folderPaneElements[i] = buttons.get(i);

		folderPane = new ScrollableFrame(folderPaneElements, x1, folderPaneY, folderPaneWidth, folderPaneHeight, 0, 8, textColor);
		filePane = buttons.get(0).getData();

		subcontext.addElement(folderPane);
		subcontext.addElement(filePane);
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

		//Draw the subcontext.
		subcontext.render(g, x1, y1, x2, y2);
	}

	//@see UIElement.setX(int)
	public void setX(int x) {
		super.setX(x);
		x2 = x1 + width;
		centerX = (x2 + x1)/2;
	}

	//@see UIElement.setY(int)
	public void setY(int y) {
		super.setY(y);
		y2 = y1 + height;
	}

	//Set and get the width.
	public void setWidth(int w) {
		width = w;
		x2 = x1 + width;
	}

	public int getWidth() {
		return width;
	}

	//Set and get the height.
	public void setHeight(int h) {
		height = h;
		y2 = y1 + height;
	}

	public int getHeight() {
		return height;
	}

	//@see UIElement.pointIsWithin(int, int)
	public boolean pointIsWithin(int x, int y) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}


	//@see UIElement.getDepth()
	public int getDepth() {
		return 10;
	}

	//@see UIElement.dead()
	public boolean dead() {
		return dead;
	}

	//@see UIEventListener.uiEvent(UIEvent)
	public void guiEvent(GUIEvent event) {
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
		GUIEvent<File> out = new GUIEvent<>(this, file);
		for (GUIEventListener l : listeners) {
			l.guiEvent(out);
		}
		dead = true;
	}

	//@see UIEventGenerator.addListener(UIEventListener)
	public void addListener(GUIEventListener l) {
		listeners.add(l);
	}

	//@see UIEventGenerator.removeListener(UIEventListener)
	public void removeListener(GUIEventListener l) {
		listeners.remove(l);
	}

}