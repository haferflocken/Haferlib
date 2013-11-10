package org.haferlib.gdx.gui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * An interface for an element of a user interface.
 * All coordinate parameters are always screen coordinates.
 * 
 * @author John Werner
 * 
 */

public interface GUIElement {

	void update(int delta);

	void render(Camera camera, ShapeRenderer sR, float viewportX, float viewportY, float viewportWidth, float viewportHeight);

	void setX(int x);

	int getX();

	void setY(int y);

	int getY();

	void setWidth(int w);

	int getWidth();

	void setHeight(int h);

	int getHeight();
	
	Rectangle getBounds();

	boolean pointIsWithin(int x, int y);
	
	public GUIElement[] getSubElements();

	void pointerJustPressed(int x, int y, int pointer, int button);

	void pointerJustPressedElsewhere(int pointer, int button);
	
	void pointerDown(int x, int y, int pointer, int button);

	void pointerDownElsewhere(int pointer, int button);
	
	void pointerJustReleased(int x, int y, int pointer, int button);

	void pointerJustReleasedElsewhere(int pointer, int button);

	void pointerHovering(int x, int y, int pointer);
	
	void pointerHoveringElsewhere(int pointer);
	
	void setDepth(int d);

	int getDepth();

	void keyPressed(int keycode);

	void keyInputDone();

	boolean dead();

	void destroy();

}