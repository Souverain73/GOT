package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

import got.gameStates.StateID;
import got.server.GameServer;

public class MainState implements ServerState {
	private static String name = "MainState";
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		stm.changeState(new PlanningPhaseState(), StateMachine.ChangeAction.SET);
	}

	@Override
	public void exit() {
		
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}

}
