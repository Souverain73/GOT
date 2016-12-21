package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.network.Packages.ConnectionError;
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
	private Thread timerThread;
	private StateMachine stm;
	
	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = ((PlayerConnection)c);
		Player player = connection.player;
		//if player logged in.
		if (pkg instanceof LogIn){
			Player pl = new Player();
			pl.setNickname(((LogIn)pkg).nickname);
			pl.id = PlayerManager.instance().LogIn(pl);
			if (pl.id == -1){
				connection.sendTCP(new ConnectionError(ConnectionError.LobbyIsFull));
				return;
			}
			
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
			Log.debug("Player:"+pl.getNickname()+" connected");
		}
		
		if (pkg instanceof Ready){
			Ready msg = ((Ready)pkg);
			player.setReady(msg.ready);
			server.sendToAllTCP(new PlayerReady(connection.player.id, msg.ready));
			
			if (PlayerManager.instance().isAllPlayersReady()){
				//Start game start countdown
				timerThread = new Thread(new Runnable() {
					@Override
					public void run() {
						int time = 1000;
						int step = 1000;
						while (time>0){
							server.sendToAllTCP(new Packages.ServerMessage(String.format("Game start in %d seconds", time/1000)));
							time-=step;
							try{
								Thread.sleep(step);
							}catch (InterruptedException e){
								
							}
						}
						PlayerManager.instance().initRandomFractions();
						
						Game.init(PlayerManager.instance().getPlayersCount());
						GameServer.getServer().sendToAllTCP(new Packages.SetFractions(PlayerManager.instance().getFractions()));
						
						connection.sendTCP(new Packages.ServerMessage("Start!!"));
						stm.changeState(new MainState(), StateMachine.ChangeAction.SET);
					}
				}, "TimerThread");
				timerThread.start();
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
		server = GameServer.getServer();
	}

	@Override
	public void exit() {

	}

	@Override
	public int getID() {
		return 0;
	}

}
