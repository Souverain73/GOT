package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.server.GameServer.PlayerConnection;

public class VesterosPhase implements GameState {
	private static final String name = "VesterosPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {

	}

	@Override
	public void exit() {
	}

	@Override
	public void draw() {
		//TODO: draw cards here
	}

	@Override
	public void update() {
		//TODO: update cards

	}

	@Override
	public void recieve(Connection connection, Object pkg) {

	}

}
