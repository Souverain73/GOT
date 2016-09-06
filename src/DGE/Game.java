package DGE;

import static org.lwjgl.glfw.GLFW.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.text.GlyphView;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import DGE.gameObjects.GameMapObject;
import DGE.graphics.*;
import DGE.utils.LoaderParams;

import org.lwjgl.opengl.GL;

public class Game {
	private static Game _instance = null;

	public static Game instance(){
		if (_instance == null){
			_instance = new Game();
		}
		return _instance;
	}
	public long pWindow;
	private GraphicModule graphics;
	private boolean camMove;
	private float camX;
	private float camY;
	private float lastx;
	private float lasty;
	
	private int windowWidth;
	private int windowHeight;
	
	private static Vector2f worldCursorPos;
	
	private GameMapObject gmo;
	
	private Game(){
		graphics = GraphicModule.instance();
		worldCursorPos = new Vector2f(0,0);
	}
	
	public long init(){
		camMove = false;
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
			
			if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS){
				System.out.println("Window:"+posx+"  "+posy);
				System.out.println("World");
				System.out.println(worldCursorPos);
				
			}
			getWorldCoord((float)posx, (float)posy);
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
		gmo.update();
	}
	
	public void updateInput(){
		glfwPollEvents();
	}
	
	public void updateGraphics(){
		graphics.clear();
		gmo.draw();
		glfwSwapBuffers(pWindow);
	}
	
	public void getWorldCoord(float winx, float winy){
		float x = winx*2.0f/windowWidth-1;
		float y = -(winy*2.0f/windowHeight-1);
		
		Vector4f point = new Vector4f(x,y,0,1);
		
		Matrix4f invproj = new Matrix4f();
		
		GraphicModule.instance().getCamera().getProjection().invert(invproj);
		
		invproj.transform(point);
		
		worldCursorPos.x = point.x;
		worldCursorPos.y = point.y;
		
	}
	
	public Vector2f getWorldCursorPos(){
		return worldCursorPos;
	}
}

