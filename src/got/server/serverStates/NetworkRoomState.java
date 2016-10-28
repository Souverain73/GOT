package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import got.Player;
import got.network.Packages.InitPlayer;
import got.network.Packages.LogIn;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayersList;
import got.network.Packages.Ready;
import got.network.Packages.PlayerReady;
import got.server.GameServer;
import got.server.GameServer.PlayerConnection;
import got.server.PlayerManager;

public class NetworkRoomState implements ServerState {
	private String name = "NetworkRoomState";
	private Server server;
	
	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = ((PlayerConnection)c);
		
		//if player logged in.
		if (pkg instanceof LogIn){
			Player pl = new Player();
			pl.setNickname(((LogIn)pkg).nickname);
			pl.id = PlayerManager.instance().LogIn(pl);
			
			((GameServer.PlayerConnection)connection).player = pl;
			
			//send response to player, and init player object;
			InitPlayer response = new InitPlayer();
			response.player = pl;
			connection.sendTCP(response);
			
			//send players list to new connected player
			connection.sendTCP(new PlayersList(PlayerManager.instance().getPlayersList()));
			
			//notify all players about new player
			PlayerConnected pc = new PlayerConnected();
			pc.player = response.player;
			server.sendToAllExceptTCP(connection.getID(), pc);
			System.out.println("Player connected");
		}
		
		if (pkg instanceof Ready){
			Ready msg = ((Ready)pkg);
			server.sendToAllTCP(new PlayerReady(connection.player.id, msg.ready));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		server = GameServer.getServer();
	}

	@Override
	public void exit() {

	}

}
