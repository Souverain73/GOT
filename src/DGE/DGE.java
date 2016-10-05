package DGE;

import org.lwjgl.glfw.GLFW;

import DGE.gameObjects.DebugPanel;

/**
 * This class creates Game object and handles main loop
 * 
 * @author Souverain73
 */
public class DGE {

	private static long pWindow;
	
	public static void main(String[] args) {
		pWindow = Game.instance().init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			DebugPanel.instance().resetFlags();
			Game.instance().updateInput();
			Game.instance().updateLogic();
			Game.instance().updateGraphics();
		}

		Game.instance().finish();
	}

}
