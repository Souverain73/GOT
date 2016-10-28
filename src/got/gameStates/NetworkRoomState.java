package got.gameStates;

import java.io.IOException;
import java.util.Random;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.Player;
import got.gameObjects.AbstractButtonObject;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.NetPlayersPanel;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.network.Packages;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayerDisconnected;
import got.network.Packages.PlayerReady;
import got.network.Packages.PlayersList;
import got.network.Packages.Ready;
import got.server.PlayerManager;
import got.utils.UI;

public class NetworkRoomState extends AbstractGameState implements IClickListener {
	private static String name = "NetworkRoomState";
	NetPlayersPanel npp;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		System.out.println("Entering "+name);
		npp = new NetPlayersPanel();
		npp.setPos(new Vector2f(440,0));
		addObject(npp);
		AbstractButtonObject btn = new ImageButton("buttons/ready.png", 460, 400, 80, 40, null);
		btn.setSpace(DrawSpace.SCREEN);
		btn.setCallback((sender, param)->{
			GameClient.instance().send(new Ready(true));
		});
		addObject(btn);
		btn = new ImageButton("buttons/exit.png", 550, 400, 80, 40, null);
		btn.setSpace(DrawSpace.SCREEN);
		btn.setCallback((sender, param)->{
			GameClient.instance().disconnect();
			GameClient.instance().getStateMachine().removeState();
		});
		addObject(btn);
		
		String host = UI.getString("Enter host", "host", "localhost");
		try{
			GameClient.instance().connect(host);
		}catch(IOException e){
			System.out.println("Can't connect to host:"+host);
			GameClient.instance().getStateMachine().removeState();
		}
		GameClient.instance().send(new Packages.LogIn().Nickname(String.format("%010d", (new Random()).nextLong()%10000)));
		System.out.println("Connection sccessfull");
		
	}
	

	@Override
	public void exit() {
		
	}

	@Override
	public void draw() {
		super.draw();
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void click(GameObject sender) {
		
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof PlayerConnected){
			PlayerConnected msg = (PlayerConnected)pkg;
			Player player = msg.player;
			System.out.println("PlayerConnected:"+player.getNickname()+" ID:"+player.id);
			UI.systemMessage(("Player "+player.getNickname()+" connected"));
			PlayerManager.instance().LogIn(player);
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					npp.addPlayer(player);
				}
			});
		}
		
		if (pkg instanceof PlayersList){
			PlayersList list = (PlayersList)pkg;
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					npp.addPlayers(list.players);	
				}
			});
		}
		
		if (pkg instanceof PlayerDisconnected){
			PlayerDisconnected msg = ((PlayerDisconnected)pkg);
			Player player = msg.player;
			PlayerManager.instance().disconnect(player.id);
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					npp.removePlayer(player.id);
				}
			});
		}
		
		if (pkg instanceof PlayerReady){
			PlayerReady msg = (PlayerReady)pkg;
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					npp.setPlayerReady(msg.playerID, msg.ready);
				}
			});
		}
	}	
}
