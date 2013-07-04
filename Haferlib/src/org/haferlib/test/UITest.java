//A Slick2D game that demonstrates org.haferlib.slick.ui

package org.haferlib.test;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;

import org.haferlib.slick.ui.*;

public class UITest extends BasicGame {

	private UIManager gui;

	public UITest() {
		super("HaferLib UI Test");
	}

	public void init(GameContainer container) {
		gui = new UIManager();
		container.getInput().addKeyListener(gui);
	}

	public void update(GameContainer container, int delta) {
		gui.update(container.getInput(), delta);
	}

	public void render(GameContainer container, Graphics g) {
		gui.render(g);
	}

	public static void main(String[] argv) throws SlickException {
		AppGameContainer container = new AppGameContainer(new UITest(), 800, 600, false);
		container.setShowFPS(false);
		container.start();
	}

}