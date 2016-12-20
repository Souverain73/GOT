package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.server.GameServer.PlayerConnection;

//TODO: реализовать отображение карт и выбор по необходимым картам.
public class VesterosPhase extends AbstractGameState {
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
	}

	@Override
	public void update() {
	}

	@Override
	public void recieve(Connection connection, Object pkg) {

	}

}
