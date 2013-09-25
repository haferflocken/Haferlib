package org.haferlib.test;

import org.haferlib.slick.gui.ListEditor;
import org.haferlib.slick.gui.GUIContext;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class GUITest extends BasicGame {

	private GUIContext ui;
	private ListEditor editorTest;

	public GUITest() {
		super("GUI Text");
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		ui = new GUIContext();
		container.getInput().addKeyListener(ui);
		
		editorTest = new ListEditor(0, 0, 512, 256, 0, 2,
			"List Editor", container.getDefaultFont(), Color.white, Color.gray, Color.darkGray, Color.red);
		for (int i = 0; i < 40; i++) {
			editorTest.addField("" + i, null, null);
		}
		
		ui.addElement(editorTest);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		ui.update(container.getInput(), delta);
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		ui.render(g, 0, 0, container.getWidth(), container.getHeight());
	}
	
	@Override
	public boolean closeRequested() {
		String[] strings = editorTest.getFieldContents();
		for (String s : strings) {
			System.out.println(s);
		}
		return super.closeRequested();
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer container = new AppGameContainer(new GUITest(), 800, 600, false);
		container.start();
	}

}
