package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import got.Player;
import got.gameStates.StateID;
import got.network.Packages.PlayerReady;
import got.network.Packages.PlayerSetAction;
import got.network.Packages.Ready;
import got.network.Packages.SetAction;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;

public class PlanningPhaseState implements ServerState{
	private static final String name = "PlanningPhaseState";
	private Server server;
	private StateMachine stm;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		server = GameServer.getServer();
		this.stm = stm;
	}

	@Override
	public void exit() {
		
	}

	@Override
	public int getID() {
		return StateID.PLANNING_PHASE;
	}
	
	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = ((PlayerConnection)c);
		Player player = connection.player;
		if (pkg instanceof Ready){
			Ready msg = ((Ready)pkg);
			player.setReady(msg.ready);
			server.sendToAllTCP(new PlayerReady(connection.player.id, msg.ready));
			//if all players is ready change phase to "FIRE PHASE"
			if (PlayerManager.instance().isAllPlayersReady()){
				server.sendToAllTCP("Next phase");
				stm.setState(new ChangeState(new FirePhase(), true));
			}
		}
		
		if (pkg instanceof SetAction){
			SetAction msg = ((SetAction)pkg);
			//TODO: handle serverside model and do some checks
			
			//if all is ok notify all clients
			server.sendToAllTCP(new PlayerSetAction(msg.region, msg.action));
		}
	}

}
