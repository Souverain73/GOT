package got;

import org.lwjgl.glfw.GLFW;

import got.gameObjects.DebugPanel;
import got.server.GameServer;;

/**
 * This class creates Game object and handles main loop
 * 
 * @author Souverain73
 */
public class GOT {

	private static long pWindow;
	
	public static void main(String[] args) {
		pWindow = GameClient.instance().init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			DebugPanel.instance().resetFlags();
			GameClient.instance().updateInput();
			GameClient.instance().updateLogic();
			GameClient.instance().updateGraphics();
			GameClient.instance().tick();
		}

		GameClient.instance().finish();
	}

}
