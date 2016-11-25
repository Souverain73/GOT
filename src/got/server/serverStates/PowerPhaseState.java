package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import got.gameStates.StateID;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;

public class PowerPhaseState extends StepByStepState{
	private static final String name = "PowerPhase";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return StateID.POWER_PHASE;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
	}
	
	@Override
	public void recieve(Connection c, Object pkg) {
		super.recieve(c, pkg);
		PlayerConnection connection = (PlayerConnection)c;
		Player player = connection.player;

		if (pkg instanceof Packages.CollectInfluence){
			Packages.CollectInfluence msg = ((Packages.CollectInfluence)pkg);
			GameServer.getServer().sendToAllTCP(msg);
		}
		
		if (pkg instanceof Packages.ChangeUnits){
			Packages.ChangeUnits msg = ((Packages.ChangeUnits)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(
					player.id, msg.region, msg.units
			));
		}
		
		if (pkg instanceof Packages.Act){
			Packages.Act msg = ((Packages.Act)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerAct(msg.from, 0));
		}
	}
}
