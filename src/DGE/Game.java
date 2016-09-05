package DGE;

import static org.lwjgl.glfw.GLFW.*;

import javax.swing.text.GlyphView;

import org.lwjgl.glfw.GLFWErrorCallback;

import DGE.gameObjects.GameMapObject;
import DGE.graphics.*;
import DGE.utils.LoaderParams;

import org.lwjgl.opengl.GL;

public class Game {
	public long pWindow;
	private GraphicModule graphics;
	private float x;
	private float y;
	private int windowWidth;
	private int windowHeight;
	
	private GameMapObject gmo;
	
	Game(){
		graphics = GraphicModule.instance();
	}
	
	public long init(){
		x = y = 0f;
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) 
			throw new RuntimeException("Failed to init GLFW");
		
		//set window options
		glfwDefaultWindowHints();
//		glfwWindowHint(GLFW_FSAA_SAMPLES, 4); // 4x Сглаживание
//		glfwWindowHint(GLFW_OPENGL_VERSION_MAJOR, 3); // Мы хотим использовать OpenGL 3.3
//		glfwWindowHint(GLFW_OPENGL_VERSION_MINOR, 3);
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
//		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Мы не хотим старый OpenGL
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
			float diff = 10;
			if ( key == GLFW_KEY_UP && action == GLFW_PRESS) y+=diff;
			if ( key == GLFW_KEY_DOWN && action == GLFW_PRESS) y-=diff;
			if ( key == GLFW_KEY_LEFT && action == GLFW_PRESS) x-=diff;
			if ( key == GLFW_KEY_RIGHT && action == GLFW_PRESS) x+=diff;
		});
		
		glfwSetCursorPosCallback(pWindow, (window, posx, posy)->{
			x=(float)posx;
			y=windowHeight-(float)posy;
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
}

