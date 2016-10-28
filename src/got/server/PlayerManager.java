package got.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import got.Player;

public class PlayerManager {
	private static PlayerManager _instance = null;
	public ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
	private int playersCount;
	private int maxPlayers = 6;
	
	private PlayerManager() {
		playersCount = 0;
	}

	public static PlayerManager instance() {
		if (_instance == null) {
			_instance = new PlayerManager();
		}
		return _instance;
	}
	
	public boolean isLoggedIn(int id){
		return players.contains(id);
	}
	
	
	/**
	 * LogIn player on the server
	 * @param player
	 * @return
	 */
	public int LogIn(Player player){
		//if max players already logged in return -1
		if (playersCount == maxPlayers) return -1;
		
		//get free id
		int id = 0;
		for (id = 0; id<maxPlayers; id++){
			if (!players.containsKey(id))
				break;
		}
		//if all id already exists return -1
		if (id == maxPlayers) return -1;
		
		playersCount++;
		players.put(id, player);
		
		return id;
	}
	
	/**
	 * register player on client
	 * @param player
	 */
	public void register(Player player){
		playersCount++;
		players.put(player.id, player);
	}
	
	public Player getPlayer(int id){
		return players.get(id);
	}
	
	public boolean disconnect(int id){
		if (!players.containsKey(id)) return false;
		
		players.remove(id);
		playersCount--;
		
		return true;
	}
	
	public int getPlayersCount(){
		return playersCount;
	}
	
	public Player[] getPlayersList(){
		return (Player[]) players.values().toArray(new Player[0]);
	}
}
