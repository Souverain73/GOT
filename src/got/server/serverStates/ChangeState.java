package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import got.model.Player;
import got.network.Packages;
import got.network.Packages.Ready;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;

public class ChangeState implements ServerState {
	private String name = "ChangeGameState";
	private StateMachine stm;
	private ServerState nextState;
	private boolean replace;
	
	public ChangeState(ServerState nextState, boolean replace){
		this.nextState = nextState;
		this.replace = replace;
		this.name = "ChangeStateTo:"+nextState.getName();
	}
	
	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = ((PlayerConnection)c);
		Player player = connection.player;
		if (pkg instanceof Packages.StateReady){
			Packages.StateReady msg = ((Packages.StateReady)pkg);
			player.setReady(true);
			
			//if all clients load state on client side server can run this state too
			if (PlayerManager.instance().isAllPlayersReady()){
				if (replace){
					stm.setState(nextState);
				}else{
					stm.pushState(nextState);
				}
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		Log.warn("Change state to"+nextState.getName());
		this.stm = stm;
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		//send ChangeState package to all clients
		GameServer.getServer().sendToAllTCP(new Packages.ChangeState(nextState.getID()));
	}

	@Override
	public void exit() {
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
