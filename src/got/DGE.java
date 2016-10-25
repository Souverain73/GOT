package got;

import org.lwjgl.glfw.GLFW;

import got.gameObjects.DebugPanel;

/**
 * This class creates Game object and handles main loop
 * 
 * @author Souverain73
 */
public class DGE {

	private static long pWindow;
	
	public static void main(String[] args) {
		pWindow = GameClient.instance().init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			DebugPanel.instance().resetFlags();
			GameClient.instance().updateInput();
			GameClient.instance().updateLogic();
			GameClient.instance().updateGraphics();
		}

		GameClient.instance().finish();
	}

}
