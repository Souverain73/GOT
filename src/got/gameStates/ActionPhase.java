package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.gameObjects.GameObject;
import got.interfaces.IClickListener;
import got.server.GameServer.PlayerConnection;

public class ActionPhase extends AbstractGameState implements IClickListener{
	private static final String name = "ActionPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
	}

	@Override
	public void click(GameObject sender){
		
	}
}
