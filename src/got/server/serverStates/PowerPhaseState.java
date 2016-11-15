package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

import got.gameStates.StateID;

public class PowerPhaseState implements ServerState{

	@Override
	public void recieve(Connection connection, Object pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		return StateID.POWER_PHASE;
	}

	@Override
	public void enter(StateMachine stm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

}
