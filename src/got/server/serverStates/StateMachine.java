package got.server.serverStates;

import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;

import got.interfaces.INetworkListener;
import got.server.GameServer.PlayerConnection;

public class StateMachine implements INetworkListener{
	private LinkedList<ServerState> _states;
	
	public StateMachine(){
		_states = new LinkedList<ServerState>();
	}
	
	public void setState(ServerState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		_states.push(state);
		state.enter(this);
	}
	
	public void pushState(ServerState state){
		_states.push(state);
		state.enter(this);
	}
	
	public void removeState(){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
	}
	
	public ServerState getCurrentState(){
		return _states.peek();
	}
	
	public void recieve(Connection connection, Object pkg){
		ServerState st = getCurrentState();
		if (st!=null){
			st.recieve(connection, pkg);
		}
	}
}
