package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.network.Packages.PlayerConnected;
import got.utils.UI;

public class NetworkRoomState implements GameState {
	private static String name = "NetworkRoomState";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		System.out.println("Entering "+name);
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
		if (pkg instanceof PlayerConnected){
			PlayerConnected msg = (PlayerConnected)pkg;
			UI.systemMessage(("Player "+msg.nickname+" connected"));
		}
	}

	
}
