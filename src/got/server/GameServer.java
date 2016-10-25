package got.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sun.org.apache.bcel.internal.classfile.PMGClass;

import got.Player;
import got.network.Network;
import got.network.Packages.InitPlayer;
import got.network.Packages.LogIn;
import got.network.Packages.PlayerConnected;
import got.network.Packages.ServerMessage;

public class GameServer {	

	public static void main(String[] args) throws IOException {
		new GameServer();
	}
	
	protected static class PlayerConnection extends Connection{
		public Player player;
	}

	
	public GameServer() throws IOException{
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
					PlayerManager.instance().disconnect(player);
				}
				
				System.out.println("Player disconnected");
				connection.close();
				super.disconnected(connection);
			}

			@Override
			public void received(Connection c, Object pkg) {
				PlayerConnection connection = (PlayerConnection)c;
				Player player = connection.player;
				
				if (pkg instanceof LogIn){
					Player pl = new Player();
					PlayerManager.instance().LogIn(pl);
					//send response to player, and init player object;
					InitPlayer response = new InitPlayer();
					response.player = pl;
					connection.sendTCP(response);
					
					PlayerConnected pc = new PlayerConnected();
					pc.nickname = "Hello";
					server.sendToAllTCP(pc);
					System.out.println("Player connected");
				}
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
		System.out.println("Server running on port:"+Network.portTCP+"/"+Network.portUDP);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true){
			String line = br.readLine();
			if (line.equals("stop")) {
				server.close();
				System.exit(0);
			}else{
				ServerMessage msg = new ServerMessage();
				msg.message = line;
				server.sendToAllTCP(msg);
			}
		}
	}
}
