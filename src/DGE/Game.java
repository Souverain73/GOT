package DGE;

import static org.lwjgl.glfw.GLFW.*;

import javax.swing.text.GlyphView;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import DGE.gameObjects.GameMapObject;
import DGE.graphics.*;
import DGE.utils.LoaderParams;

import org.lwjgl.opengl.GL;

public class Game {
	public long pWindow;
	private GraphicModule graphics;
	private boolean camMove;
	private float camX;
	private float camY;
	private float lastx;
	private float lasty;
	private int windowWidth;
	private int windowHeight;
	
	private GameMapObject gmo;
	
	Game(){
		graphics = GraphicModule.instance();
	}
	
	public long init(){
		camMove = false;
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
		
		//TODO: Create keyboard callback
		//Something like:
		glfwSetKeyCallback(pWindow, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in our rendering loop
		});
		
		glfwSetCursorPosCallback(pWindow, (window, posx, posy)->{
			float dx = lastx-(float)posx;
			float dy = lasty-(float)posy;
			
			if (camMove){
//				System.out.println("MoveCameraBy dx:"+dx+"  dy:"+dy);
				GraphicModule.instance().getCamera().moveCamera(new Vector3f(-dx, -dy, 0));
			}
			
			if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT)==GLFW_PRESS){
				camMove = true;
			}else{
				camMove = false;
			}
			
			lastx = (float)posx;
			lasty  =(float)posy;
		});
		
		glfwSetScrollCallback(pWindow, (window, xoffset, yoffset)->{
			GraphicModule.instance().getCamera().scale((float)yoffset/30);
		});
		//But many times better
		
		//windwo resize callback
		glfwSetWindowSizeCallback(pWindow, (window, w, h)->{
			windowWidth = w;
			windowHeight = h;
			GraphicModule.resizeCallback(window, w, h);
		});
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(pWindow);
		GL.createCapabilities();
		graphics.initOpenGl();
		// Enable v-sync
		glfwSwapInterval(1);
		
		//show window
		glfwShowWindow(pWindow);
		
		gmo = new GameMapObject();
		gmo.init(new LoaderParams(new String [] {"filename", "data/map.xml"}));
		
		return pWindow;
	}
	
	public void finish(){
		glfwDestroyWindow(pWindow);
		glfwSetErrorCallback(null).free();
		glfwTerminate();
	}
	
	public void updateLogic(){
		
	}
	
	public void updateInput(){
		glfwPollEvents();
	}
	
	public void updateGraphics(){
		graphics.clear();
		gmo.draw();
		glfwSwapBuffers(pWindow);
	}
	
	public void updateCamera(){
		
	}
}

