package DGE.gameStates.modals;

import DGE.Game;
import DGE.InputManager;
import DGE.gameObjects.ImageButton;
import DGE.gameStates.GameState;
import DGE.gameStates.StateMachine;
import DGE.graphics.DrawSpace;
import DGE.graphics.Font;
import DGE.graphics.Text;

public class MessageBoxState implements GameState{
	//TODO create BG and Buttons.
	private static Font msgFont = new Font("test");
	private Text text;

	private float x, y;
	
	public MessageBoxState(String text, int x, int y) {
		this.text = Text.newInstance(text, msgFont);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String getName() {
		return "MessageBox";
	}

	@Override
	public void enter(StateMachine stm) {
		
	}

	@Override
	public void exit() {
		
	}

	@Override
	public void draw() {
		text.draw(x, y, 1, 1, DrawSpace.SCREEN);
	}

	@Override
	public void update() {
		if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1 
				&& !(InputManager.instance().getMouseTarget() instanceof ImageButton)){
			close();
		}
	}
	
	private void close(){
		Game.instance().closeModal();
	}

}
