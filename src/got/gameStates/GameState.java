package got.gameStates;

import got.interfaces.INetworkListener;

public interface GameState extends INetworkListener{
	public String getName();
	public int getID();
	public void enter(StateMachine stm);
	public void exit();
	public void draw();
	public void update();
	public void tick();
}
