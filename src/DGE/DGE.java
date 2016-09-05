package DGE;

import org.lwjgl.glfw.GLFW;

public class DGE {

	private static Game game;
	private static long pWindow;
	
	public static void main(String[] args) {
		game = new Game();
		pWindow = game.init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			game.updateInput();
			game.updateLogic();
			game.updateGraphics();
		}

		game.finish();
	}

}
