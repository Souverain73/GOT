package got.server;

import java.util.HashMap;
import java.util.Set;

import got.Player;

public class PlayerManager {
	private static PlayerManager _instance = null;
	private HashMap<Integer, Player> players;
	private int playersCount;
	private int maxPlayers = 6;
	
	private PlayerManager() {
		players = new HashMap<>();
		playersCount = 0;
	}

	public static PlayerManager instance() {
		if (_instance == null) {
			_instance = new PlayerManager();
		}
		return _instance;
	}
	
	public boolean isLoggedIn(int id){
		return players.containsKey(id);
	}
	
	public int LogIn(Player player){
		if (isLoggedIn(player.getId()) || maxPlayers == playersCount) return -1;
		
		Set<Integer> keys = players.keySet();
		int id=-1;
		for (id=0; id<maxPlayers; id++){
			if (!keys.contains(id)){
				break;
			}
		}
		
		players.put(id, player);
		return id;
	}
	
	public int getPlayersCount(){
		return playersCount;
	}
}
