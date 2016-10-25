package got.gameStates;

import com.esotericsoftware.kryonet.Listener;

import got.interfaces.INetworkListener;

public interface GameState extends INetworkListener{
	public String getName();
	public void enter(StateMachine stm);
	public void exit();
	public void draw();
	public void update();
}
