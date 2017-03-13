package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages.PlayerReady;
import got.network.Packages.PlayerSetAction;
import got.network.Packages.Ready;
import got.network.Packages.SetAction;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;

public class PlanningPhaseState extends ParallelState{
	private static final String name = "PlanningPhaseState";
	private Server server;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		server = GameServer.getServer();
		GameServer.instance().execGameConfig();
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
		super.recieve(c, pkg);
		PlayerConnection connection = ((PlayerConnection)c);
		Player player = connection.player;
		
		if (pkg instanceof SetAction){
			SetAction msg = ((SetAction)pkg);
			//if all is ok notify all clients
			server.sendToAllTCP(new PlayerSetAction(msg.region, msg.action));
		}
	}

	@Override
	protected void onReadyToChangeState() {
		server.sendToAllTCP("Next phase");
		stm.changeState(new FirePhaseState(), StateMachine.ChangeAction.SET);
	}

}
