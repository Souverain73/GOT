package DGE;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import DGE.gameStates.GameState;
import DGE.gameStates.StateMachine;
import DGE.graphics.GraphicModule;

public class ModalState implements Runnable{
	private boolean running;
	private GameState state;
	private boolean updateMain;
	private boolean drawMain;
	
	public ModalState(GameState st){
		this(st, false, true);
	}
	
	public ModalState(GameState st, boolean updateMain, boolean drawMain) {
		state = st;
		this.updateMain = updateMain;
		this.drawMain = drawMain;
	}
	
	@Override
	public void run() {
		Game.instance().registerModalState(this);
		StateMachine stm = Game.instance().getStateMachine();
		
		state.enter(null);
		running = true;
		
		while(running && Game.instance().isRunning()){
			Game.instance().updateInput();
			if (updateMain){
				stm.update();
			}
			state.update();
			
			GraphicModule.instance().clear();

			if (drawMain){
				stm.draw();
			}
			state.draw();
			glfwSwapBuffers(Game.instance().pWindow);
		}
		
		state.exit();
	}
	
	public void close(){
		running = false;
	}
}
