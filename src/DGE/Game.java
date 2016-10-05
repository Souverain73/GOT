package DGE;

import static org.lwjgl.glfw.GLFW.*;

import java.util.LinkedList;


import DGE.gameStates.MenuState;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import DGE.gameStates.StateMachine;
import DGE.graphics.*;

import org.lwjgl.opengl.GL;

/**
 * Main Game class, handles all initiaization, and implements global Game functions
 * 
 * @author Souverain73
 */
public class Game {
	private static Game _instance = null;
	private LinkedList<ModalState> modalStates;
	private Player player;

	public static Game instance(){
		if (_instance == null){
			_instance = new Game();
		}
		return _instance;
	}
	protected long pWindow;
	private GraphicModule graphics;
	private StateMachine stm;
	
	private int windowWidth;
	private int windowHeight;	
	
	private Game(){
		graphics = GraphicModule.instance();
		stm = new StateMachine();
		player = new Player();
		player.setSpecials(2);
		modalStates = new LinkedList<ModalState>();
	}
	
	public long init(){	
		initWindow();
		initResources();
		
		return pWindow;
	}
	
	public void initWindow(){
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) 
			throw new RuntimeException("Failed to init GLFW");
		
		//set window options
		glfwDefaultWindowHints();
//		glfwWindowHint(GLFW_FSAA_SAMPLES, 4); // 4x �����������
//		glfwWindowHint(GLFW_OPENGL_VERSION_MAJOR, 3); // �� ����� ������������ OpenGL 3.3
//		glfwWindowHint(GLFW_OPENGL_VERSION_MINOR, 3);
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
//		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // �� �� ����� ������ OpenGL
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		pWindow = glfwCreateWindow(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, "GOT", 0, 0);
		if (pWindow == 0){
			throw new RuntimeException("Failed to create GLFW Window");
		}
		
		glfwSetKeyCallback(pWindow, InputManager.instance()::keyboardCallback);
		
		glfwSetCursorPosCallback(pWindow, InputManager.instance()::mouseMoveCallback);
				
		glfwSetScrollCallback(pWindow, InputManager.instance()::mouseScrollCallback);
		
		glfwSetMouseButtonCallback(pWindow, InputManager.instance()::mouseButtonCallback);
		
		glfwSetInputMode(pWindow, GLFW_STICKY_MOUSE_BUTTONS, 1);
		
		//window resize callback
		glfwSetWindowSizeCallback(pWindow, this::windowSizeCallback);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(pWindow);
		GL.createCapabilities();
		graphics.initOpenGl();
		// Enable v-sync
		glfwSwapInterval(1);
		
		//show window
		glfwShowWindow(pWindow);
	}
	
	public void initResources(){
		stm.setState(new MenuState());
	}
	
	
	public void windowSizeCallback(long window , int w, int h){
		windowWidth = w;
		windowHeight = h;
		GraphicModule.resizeCallback(window, w, h);
	}
	
	public void finish(){
		glfwDestroyWindow(pWindow);
		glfwSetErrorCallback(null).free();
		glfwTerminate();
	}
	
	public void updateLogic(){
		stm.update();
	}
	
	public void updateInput(){
		glfwPollEvents();
		InputManager.instance().update();
	}
	
	public void updateGraphics(){
		graphics.clear();

		stm.draw();

		glfwSwapBuffers(pWindow);
	}
	
	public void exit(){
		glfwSetWindowShouldClose(pWindow, true);
	}
	
	public StateMachine getStateMachine(){
		return stm;
	}
	
	public Vector2f calcWorldCoord(float winx, float winy){
		float x = winx*2.0f/windowWidth-1;
		float y = -(winy*2.0f/windowHeight-1);
		
		Vector4f point = new Vector4f(x,y,0,1);
		
		Matrix4f invproj = new Matrix4f();
		
		GraphicModule.instance().getCamera().getProjection().invert(invproj);
		
		invproj.transform(point);
		
		return new Vector2f(point.x, point.y);	
	}
	
	public void registerModalState(ModalState mst){
		this.modalStates.push(mst);
	}
	
	public void closeModal(){
		if (!modalStates.isEmpty()){
			modalStates.poll().close();
		}
	}
	
	public boolean isRunning(){
		return !glfwWindowShouldClose(pWindow);
	}

	public Player getPlayer() {
		return player;
	}
}

