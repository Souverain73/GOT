package got.gameStates.modals;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.InputManager;
import got.gameObjects.ImageButton;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;
import got.server.GameServer.PlayerConnection;

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
		GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
		text.draw(x, y, 1, 1);
	}

	@Override
	public void update() {
		if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1 
				&& !(InputManager.instance().getMouseTarget() instanceof ImageButton)){
			close();
		}
	}
	
	private void close(){
		GameClient.instance().closeModal();
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return -1;
	}

}
