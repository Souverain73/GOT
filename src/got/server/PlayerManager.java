package got.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import got.Player;

public class PlayerManager {
	private static PlayerManager _instance = null;
	public Set<Player> connected = Collections.synchronizedSet(new HashSet<Player>());
	private int connectedPlayers;
	private int maxPlayers = 6;
	
	private PlayerManager() {
		connectedPlayers = 0;
	}

	public static PlayerManager instance() {
		if (_instance == null) {
			_instance = new PlayerManager();
		}
		return _instance;
	}
	
	public boolean isLoggedIn(Player player){
		return connected.contains(player);
	}
	
	public boolean LogIn(Player player){
		if (connectedPlayers == maxPlayers) return false;
		
		connectedPlayers++;
		connected.add(player);
		
		return true;
	}
	
	public boolean disconnect(Player player){
		if (!connected.contains(player)) return false;
		
		connected.remove(player);
		connectedPlayers--;
		
		return true;
	}
	
	public int getPlayersCount(){
		return connectedPlayers;
	}
}
