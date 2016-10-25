package got.gameStates;

import com.esotericsoftware.kryonet.Listener;

public interface GameState {
	public String getName();
	public void enter(StateMachine stm);
	public void exit();
	public void draw();
	public void update();
}
