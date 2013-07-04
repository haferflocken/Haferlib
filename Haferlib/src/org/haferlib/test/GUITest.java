//A Slick2D game that demonstrates org.haferlib.slick.gui

package org.haferlib.test;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;

import org.haferlib.slick.gui.*;

public class GUITest extends BasicGame {

	private GUIContext ui;

	public GUITest() {
		super("org.haferlib.slick.gui Test");
	}

	public void init(GameContainer container) {
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);

		//MultifieldDialog test.
		ui.addElement(new MultifieldDialog("MultifieldDialog Test 1", new String[] {"field 1", "field 2", "field 3"}, 128, 64, 400, 100, 18));
		ui.addElement(new MultifieldDialog("MultifieldDialog Test 2", new String[] {"field 1", "field 2", "field 3", "field 4", "field 5", "field 6"}, 128, 200, 400, 100, 18));
		ui.addElement(new MultifieldDialog("MultifieldDialog Test 3", new String[] {"field 1", "field 2", "field 3", "field 4", "field 5", "field 6", "field 7", "field 8", "field 9"}, 128, 336, 400, 100, 18));

		//FileBrowser test.
		//ui.addElement(new FileBrowser("FileBrower Test", new Paths("Google Drive").gamePath, "", container.getGraphics().getFont(), 128, 256, 512, 320, Color.white, Color.black, Color.blue, Input.KEY_ENTER));
	}

	public void update(GameContainer container, int delta) {
		ui.update(container.getInput(), delta);
	}

	public void render(GameContainer container, Graphics g) {
		ui.render(g, 0, 0, 800, 600);
	}

	public static void main(String[] argv) throws SlickException {
		AppGameContainer container = new AppGameContainer(new GUITest(), 800, 600, false);
		container.setShowFPS(false);
		container.start();
	}

}