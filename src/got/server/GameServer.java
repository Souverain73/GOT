package got.server;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import got.Player;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.InitPlayer;

public class GameServer {	
	
	public static void main(String[] args) {
		new GameServer();
	}
	
	protected static class PlayerConnection extends Connection{
		public Player player;
	}

	
	public GameServer(){
		// TODO create server code here
		Server server = new Server(){
			@Override
			protected Connection newConnection() {
				return new PlayerConnection();
			}
			
		};
		
		Packages.register(server);
		
		int retries = 0;
		int maxRetries = 3;
		
		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				InitPlayer msg = new InitPlayer();
				msg.player = new Player(0);
				connection.sendTCP(msg);
				super.connected(connection);
			}

			@Override
			public void disconnected(Connection connection) {
				// TODO Auto-generated method stub
				super.disconnected(connection);
			}

			@Override
			public void received(Connection connection, Object object) {
				// TODO Auto-generated method stub
				super.received(connection, object);
			}

			@Override
			public void idle(Connection connection) {
				// TODO Auto-generated method stub
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
			} finally {
				server.close();
			}
		}
		
		server.start();
		System.out.println("Server running on port:"+Network.portTCP+"/"+Network.portUDP);
	}
}
