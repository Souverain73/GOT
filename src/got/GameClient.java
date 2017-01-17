package got;

import static got.utils.UI.logSystem;
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

import java.awt.font.TextMeasurer;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.minlog.Log;
import got.gameObjects.GameMapObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.houseCards.HouseCardsLoader;
import got.server.GameServer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.gameStates.GameState;
import got.gameStates.MainState;
import got.gameStates.MenuState;
import got.gameStates.StateID;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.Fraction;
import got.model.Player;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.ChangeState;
import got.network.Packages.ServerMessage;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;
import got.utils.UI;
import sun.print.BackgroundLookupListener;

import static got.network.Packages.Ready;

/**
 * Main Game class, handles all initiaization, and implements global Game functions
 * 
 * @author Souverain73
 */
public class GameClient {
	public static class Shared{
		public BattleDeckObject battleDeck = null;
		public GameMapObject gameMap = null;
	}
	private boolean debug = false;
	private static GameClient _instance = null;
	private LinkedList<ModalState> modalStates;
	private Texture background = null;
	public static final Shared shared = new Shared();
	
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
		Log.set(Log.LEVEL_INFO);
		graphics = GraphicModule.instance();
		stm = new StateMachine();
		modalStates = new LinkedList<>();
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
		
		pWindow = glfwCreateWindow(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, "GOT", 0, 0);
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
		background = TextureManager.instance().loadTexture("background.png");
		HouseCardsLoader.instance();
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
				//Небольшой костыль.
				if (object instanceof ChangeState){
					int stateID = ((ChangeState) object).state;
					if (stateID == StateID.MAIN_STATE)
						GameClient.instance().registerTask(() -> stm.setState(StateID.getGameStateByID(stateID)));
				}
				//handle common packages	
				if (object instanceof Packages.InitPlayer){
					Packages.InitPlayer msg = (Packages.InitPlayer)object;
					UI.serverMessage("InitPlayer:\n"+msg.player.toString());
					PlayerManager.instance().initPlayer(msg.player);
				}
				if (object instanceof ServerMessage){
					UI.serverMessage(((ServerMessage)object).message);
				}
				if (object instanceof Packages.PlayerResumeModal) {
					Packages.PlayerResumeModal res = (Packages.PlayerResumeModal) object;
					Player player = PlayerManager.instance().getPlayer(res.player);
					logSystem("Player " + player.getNickname() + " just resumed paused modal state");

					if (!modalStates.isEmpty()) {
						modalStates.getLast().getGameState().recieve(c, object);
					}
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
		
		executeTasks();		
		stm.update();
	}
	
	public void updateInput(){
		glfwPollEvents();
		InputManager.instance().update();
	}
	
	public void updateGraphics(){
		graphics.clear();

		//draw Background
		GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
		background.draw(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		
		stm.draw();

		glfwSwapBuffers(pWindow);
	}
	
	public void tick(){
		stm.tick();
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
	
	public void executeTasks(){
		while(!taskPool.isEmpty()){
			Runnable task = taskPool.poll();
			if (task!=null){
				task.run();
			}
		}
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
		resy = -((winy*2/windowHeight)-1);
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
	
	public void sendReady(boolean ready){
		send(new Ready(ready));
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

	@Deprecated
	public static Player getPlayer() {
		throw new IllegalStateException("getPlayer is not working now. Use PlayerManager.getSelf()");
	}
	
	@Deprecated
	public void setPlayer(Player player){

	}

	public boolean isDebug() {
		return debug;
	}

	public void setTooltipText(String text){
		registerTask(()->{
			GameState st = stm.getCurrentState();
			if (st instanceof MainState) {
				MainState mainState = (MainState) st;
				mainState.setTooltipText(text);
			}
		});
	}
	
	public Client getClient(){
		return client;
	}
	
	/**
	 * Возвращает текущее состояние, с учетом всех особенностей движка.<br>
	 * В простейшем случае это StateMachine.getCurrentState();
	 * Но с учетом модальных состояний и основного состояния текущим состоянием будет:<br>
	 * 1. Если есть модальные состояние - то последнее состояние в стеке модальных состояний.<br>
	 * 2. Если текущее состояние MainState, то текущее состояние из MainState<br>
	 * 3. Иначе текущее стстояние из GameClient;
	 * @return Текущее состояние игры.
	 */
	public GameState getCurrentState(){
		GameState result = null;
		if (!modalStates.isEmpty()){
			return modalStates.peek().getGameState();
		}else{
			result = stm.getCurrentState();
			if (result instanceof MainState){
				result = ((MainState) result).getCurrentState();
			}
		}
		return result;
	}
}

