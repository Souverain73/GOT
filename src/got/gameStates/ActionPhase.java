package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.InputManager;
import got.gameObjects.GameObject;
import got.interfaces.IClickListener;
import got.server.GameServer.PlayerConnection;

import static got.utils.UI.logAction;

public class ActionPhase extends AbstractGameState implements IClickListener{
	private static final String name = "ActionPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		logAction("Entering " + getName() + " state");
	}

	@Override
	public void click(InputManager.ClickEvent event){
		
	}
}
