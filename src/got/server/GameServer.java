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

import got.model.Game;
import got.model.Player;
import got.network.Network;
import got.network.Packages.InitPlayer;
import got.network.Packages.LogIn;
import got.network.Packages.PlayerConnected;
import got.network.Packages.PlayerDisconnected;
import got.network.Packages.ServerMessage;
import got.server.serverStates.ChangeState;
import got.server.serverStates.NetworkRoomState;
import got.server.serverStates.ServerState;
import got.server.serverStates.StateMachine;

public class GameServer {	
	
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
		if (server!=null) throw new IOException("Server aldeady exist");
		// TODO create server code here
		Server server = new Server(){
			@Override
			protected Connection newConnection() {
				return new PlayerConnection();
			}
			
		};
		
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
