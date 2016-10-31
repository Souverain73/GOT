package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

import got.gameStates.StateID;

public class FirePhase implements ServerState {
	private static final String name = "FirePhase";

	@Override
	public void recieve(Connection connection, Object pkg) {

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return StateID.FIRE_PHASE;
	}

	@Override
	public void enter(StateMachine stm) {

	}

	@Override
	public void exit() {

	}

}
