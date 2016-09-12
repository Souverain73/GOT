package DGE;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.joml.Vector2f;
import org.joml.Vector3f;

import DGE.graphics.GraphicModule;

public class InputManager {
//	CONSTANTS
	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_RIGHT = 1;
	public static final int MOUSE_MIDDLE = 2;
	
	
	private static InputManager _instance = null;
	private boolean camMove;
	private Vector2f lastMousePosWin;
	private Vector2f lastMousePosWorld;
	private int states[];

	private InputManager() {
		states = new int[3];
		camMove = false;
		lastMousePosWin = new Vector2f(0,0);
		lastMousePosWorld = new Vector2f(0,0);
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
			glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
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
		
		lastMousePosWorld = Game.instance().calcWorldCoord((float)posx, (float)posy);
		lastMousePosWin.x = (float)posx;
		lastMousePosWin.y  =(float)posy;
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

}
