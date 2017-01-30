package got.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import got.gameObjects.MapPartObject;
import got.model.Fraction;
import got.model.Game;
import got.model.Player;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.InitPlayer;
import got.network.Packages.LogIn;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayerDisconnected;
import got.network.Packages.ServerMessage;
import got.server.serverStates.ChangeState;
import got.server.serverStates.NetworkRoomState;
import got.server.serverStates.ServerState;
import got.server.serverStates.StateMachine;
import got.translation.Language;
import got.translation.Translator;

public class GameServer {
	public static class Shared{
		public int attackerRegionID;
		public int defenderRegionID;
		public int attackerID;
		public int defenderID;
	}
	public static Shared shared = new Shared();
	private static StateMachine stm = new StateMachine();
	private static Server server = null;
	private ConcurrentLinkedQueue<Runnable> taskPool = new ConcurrentLinkedQueue<>();
	
	public static Server getServer(){
		Log.set(Log.LEVEL_DEBUG);
		return server;
	}
	
	public static void main(String[] args) throws IOException {
		new GameServer(true);
	}
	
	public static class PlayerConnection extends Connection{
		public Player player;
	}

	public GameServer() throws IOException{
		this(false);
	}
	
	public GameServer(boolean console) throws IOException{
		if (server!=null) throw new IOException("Server already exist");
		Server server = new Server(){
			@Override
			protected Connection newConnection() {
				return new PlayerConnection();
			}
			
		};
		Translator.init(Language.RUSSIAN);
		
		Network.register(server);
		
		int retries = 0;
		int maxRetries = 3;
		
		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				super.connected(connection);
			}

			@Override
			public void disconnected(Connection c) {
				
				PlayerConnection connection = (PlayerConnection)c;
				Player player = connection.player;
				if (player != null){
					PlayerManager.instance().disconnect(player.id);
					Log.debug("Player:"+player.getNickname()+" disconnected");
					server.sendToAllExceptTCP(connection.getID(), new PlayerDisconnected(player));
				}
				
				connection.close();
				super.disconnected(connection);
				if (server.getConnections().length == 0){
					server.close();
					System.exit(0);
				}
			}

			@Override
			public void received(Connection c, Object pkg) {
				PlayerConnection connection = (PlayerConnection)c;
				Player player = connection.player;
				//Handle all common packages
				//pass state specified package to game state
				if (pkg instanceof Packages.ResumeModal) {
					getServer().sendToAllTCP(new Packages.PlayerResumeModal(player.id));
					return;
				}
				if (pkg instanceof Packages.WaitForModal) {
					//todo: обработать паузу, проверить все ли игроки ждут. Если не все игрока ждут, нельзя посылать пакет Resume
				}
				if (pkg instanceof Packages.Confirm) {
					getServer().sendToAllTCP(new Packages.PlayerConfirm(player.id));
				}
				if (pkg instanceof Packages.Cancel) {
					getServer().sendToAllTCP(new Packages.PlayerCancel(player.id));
				}


				stm.recieve(connection, pkg);
			}

			@Override
			public void idle(Connection connection) {
				super.idle(connection);
			}
		});
		
		while (true){
			retries++;
			try {
				server.bind(Network.portTCP, Network.portUDP);
				break;
			} catch (IOException e) {
				System.out.println("Can't bind server. Trying again");
				if (retries>=maxRetries){
					e.printStackTrace();
					return;
				}
			}
		}
		
		server.start();
		GameServer.server = server;
		stm.setState(new NetworkRoomState());
		
		System.out.println("Server running on port:"+Network.portTCP+"/"+Network.portUDP);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//main server loop
		//handles console commands and execute tasks
		if (console){
			while (true){
				executeTasks();
				
				String line = br.readLine().trim();
				String [] command = line.split("\\s");
				if (command.length == 0){
					continue;
				}
				if (command[0].equals("stop")) {
					server.close();
					System.exit(0);
				}else if(command[0].equals("say")){
					ServerMessage msg = new ServerMessage();
					msg.message = command[1];
					server.sendToAllTCP(msg);
				}else if(command[0].equals("dump")){
					System.out.println(PlayerManager.instance().toString());
					System.out.println("Current State Dump: " + stm.getCurrentState());
				}
			}
		}
	}
	
	public void registerTask(Runnable task){
		taskPool.add(task);
	}
	
	public void executeTasks(){
		while(!taskPool.isEmpty()){
			Runnable task = taskPool.poll();
			if (task!=null){
				task.run();
			}
		}
	}
}
