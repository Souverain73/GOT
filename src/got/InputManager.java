package got;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import java.util.ArrayList;
import java.util.Collections;

import org.joml.Vector2f;
import org.joml.Vector3f;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.graphics.GraphicModule;
import got.interfaces.IClickable;


import got.graphics.DrawSpace;
/**
 * This class handle all input
 * @author Souverain73
 *
 */
public class InputManager {
//	CONSTANTS
	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_RIGHT = 1;
	public static final int MOUSE_MIDDLE = 2;
	
	
	private static InputManager _instance = null;
	private boolean camMove;
	private Vector2f lastMousePosWin;
	private Vector2f lastMousePosWorld;
	private Vector2f lastMousePosNative;
	private int states[];
	private ArrayList<IClickable> clickables;
	private GameObject mouseTarget;

	private InputManager() {
		states = new int[3];
		camMove = false;
		lastMousePosWin = new Vector2f(0, 0);
		lastMousePosWorld = new Vector2f(0, 0);
		lastMousePosNative = new Vector2f(0, 0);
		clickables = new ArrayList<IClickable>();
	}

	public static InputManager instance() {
		if (_instance == null) {
			_instance = new InputManager();
		}
		return _instance;
	}
	
	public Vector2f getMousePosWin(){
		return lastMousePosWin;
	}
	
	public Vector2f getMousePosWorld(){
		return lastMousePosWorld;
	}
	
	public int getMouseButtonState(int button){
		return states[button];
	}
	
	public void keyboardCallback(long window, int key, int scancode, int action, int mods){
		if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
			glfwSetWindowShouldClose(window, true);
	}
	
	public void mouseMoveCallback(long window, double posx, double posy){
		float dx = lastMousePosWin.x-(float)posx;
		float dy = lastMousePosWin.y-(float)posy;
		
		if (camMove){
			GraphicModule.instance().getCamera().moveCamera(new Vector3f(-dx, -dy, 0));
		}
		
		if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT)==GLFW_PRESS){
			camMove = true;
		}else{
			camMove = false;
		}
		
		lastMousePosWorld = GameClient.instance().calcWorldCoord((float)posx, (float)posy);
		lastMousePosWin.x = (float)posx;
		lastMousePosWin.y  = (float)posy;
		lastMousePosNative = GameClient.instance().calcNativeCoord((float)posx, (float)posy); 
	}
	
	public void mouseButtonCallback(long window, int button, int action, int mods){
		int index = -1;
		switch (button){
			case GLFW_MOUSE_BUTTON_LEFT: index = MOUSE_LEFT; break;
			case GLFW_MOUSE_BUTTON_RIGHT: index = MOUSE_RIGHT; break;
			case GLFW_MOUSE_BUTTON_MIDDLE: index = MOUSE_MIDDLE; break;
		}
		if (index<0) return;
		if (action == GLFW_PRESS){
			states[index] = 1;
		}
		if (action == GLFW_RELEASE){
			states[index] = 0;
		}
	}
	
	public void mouseScrollCallback(long window, double xoffset, double yoffset){
		GraphicModule.instance().getCamera().scale((float)yoffset/30);
	}

	public boolean keyPressed(int key){
		return glfwGetKey(GameClient.instance().pWindow, key) == GLFW_PRESS;
	}
	
	public void update(){
		boolean f = false;
		for (IClickable cl : clickables) {
			if (!cl.isActive()){
				continue;
			}
			if (f){
				cl.setMouseIn(false);
			}else {
				Vector2f mouseCoord;
				if (cl.getSpace() == DrawSpace.WORLD){
					mouseCoord = lastMousePosWorld;
				}else if (cl.getSpace() == DrawSpace.SCREEN){
					mouseCoord = lastMousePosWin;
				}else{
					mouseCoord = lastMousePosNative;
				}
				if (cl.ifMouseIn(mouseCoord)){
					mouseTarget = (GameObject) cl;
					cl.setMouseIn(f = true);
				}else{
					cl.setMouseIn(false);
				}
			}
		}
		if (!f){
			mouseTarget = null;
		}
		
	}
	
	public GameObject getMouseTarget() {
		return mouseTarget;
	}

	//IClickable support
	public void registerClickable(IClickable clickable){
		clickables.add(clickable);
		Collections.sort(clickables, (c1, c2)-> c2.getPriority() - c1.getPriority());
	}
	
	public void removeClickable(IClickable clickable){
		clickables.remove(clickable);
	}
}
