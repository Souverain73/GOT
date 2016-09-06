package DGE;

import org.lwjgl.glfw.GLFW;

public class DGE {

	private static long pWindow;
	
	public static void main(String[] args) {
		pWindow = Game.instance().init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			Game.instance().updateInput();
			Game.instance().updateLogic();
			Game.instance().updateGraphics();
		}

		Game.instance().finish();
	}

}
