package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.gameObjects.GameObject;
import got.interfaces.IClickListener;
import got.server.GameServer.PlayerConnection;

public class ActionPhase extends AbstractGameState implements IClickListener{
	protected StateMachine stm;
	private static final String name = "ActionPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
//		stm.pushState(new MovePhase());
	}

	@Override
	public void exit() {
		
	}

	@Override
	public void draw() {
		
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void click(GameObject sender){
		
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		
	}
	
	
}
