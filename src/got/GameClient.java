package got;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_MOUSE_BUTTONS;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.gameStates.MenuState;
import got.gameStates.StateMachine;
import got.graphics.GraphicModule;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.ServerMessage;
import got.server.GameServer.PlayerConnection;
import got.utils.UI;

/**
 * Main Game class, handles all initiaization, and implements global Game functions
 * 
 * @author Souverain73
 */
public class GameClient {
	private boolean debug = false;
	private static GameClient _instance = null;
	private LinkedList<ModalState> modalStates;
	private Player player;
	private Client client;
	private ConcurrentLinkedQueue<Runnable> taskPool = new ConcurrentLinkedQueue<>();

	public static GameClient instance(){
		if (_instance == null){
			_instance = new GameClient();
		}
		return _instance;
	}
	protected long pWindow;
	private GraphicModule graphics;
	private StateMachine stm;
	
	private int windowWidth;
	private int windowHeight;	
	
	private GameClient(){
		graphics = GraphicModule.instance();
		stm = new StateMachine();
		modalStates = new LinkedList<ModalState>();
	}
	
	public long init(){	
		initWindow();
		initResources();
		initNetwork();
		
		return pWindow;
	}
	
	public void initWindow(){
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit()) 
			throw new RuntimeException("Failed to init GLFW");
		
		//set window options
		glfwDefaultWindowHints();
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
	
	public void initNetwork(){
		client = new Client();
		Network.register(client);
		client.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {

				super.connected(connection);
			}

			@Override
			public void disconnected(Connection connection) {

				super.disconnected(connection);
			}

			@Override
			public void received(Connection c, Object object) {
				//handle common packages
				if (object instanceof Packages.InitPlayer){
					Packages.InitPlayer msg = (Packages.InitPlayer)object;
					UI.serverMessage("InitPlayer:\n"+msg.player.toString());
				}else if (object instanceof ServerMessage){
					UI.serverMessage(((ServerMessage)object).message);
				}
				//handle state specific packages
				stm.recieve(c, object);
				
				super.received(c, object);
			}

			@Override
			public void idle(Connection connection) {

				super.idle(connection);
			}
			
		});
		
		client.start();
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
		
		while(!taskPool.isEmpty()){
			Runnable task = taskPool.poll();
			if (task!=null){
				task.run();
			}
		}
		
		stm.update();
	}
	
	public void updateInput(){
		glfwPollEvents();
		InputManager.instance().update();
		if (debug){
			if(InputManager.instance().keyPressed(GLFW_KEY_1)){
				player.setFraction(Fraction.BARATEON);
			}
			if(InputManager.instance().keyPressed(GLFW_KEY_2)){
				player.setFraction(Fraction.LANISTER);
			}
			if(InputManager.instance().keyPressed(GLFW_KEY_3)){
				player.setFraction(Fraction.STARK);
			}
			if(InputManager.instance().keyPressed(GLFW_KEY_4)){
				player.setFraction(Fraction.TIREL);
			}
			if(InputManager.instance().keyPressed(GLFW_KEY_5)){
				player.setFraction(Fraction.GREYJOY);
			}
			if(InputManager.instance().keyPressed(GLFW_KEY_6)){
				player.setFraction(Fraction.MARTEL);
			}
		}
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
	
	public void registerTask(Runnable task){
		taskPool.add(task);
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
	
	public Vector2f calcNativeCoord(float winx, float winy){
		float resx, resy;
		resx = (winx*2/windowWidth)-1;
		resy = (winx*2/windowHeight)-1;
		return new Vector2f(resx, resy);
	}
	
	public void connect(String host) throws IOException{
		client.connect(5000, host, Network.portTCP, Network.portUDP);
	}
	
	public void disconnect(){
		client.close();
	}
	
	public void send(Packages.ClientServerPackage pkg){
		client.sendTCP(pkg);
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
	
	public void setPlayer(Player player){
		this.player = player;
	}

	public boolean isDebug() {
		return debug;
	}
	
	public Client getClient(){
		return client;
	}
}

