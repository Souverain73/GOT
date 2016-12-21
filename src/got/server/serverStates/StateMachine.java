package got.server.serverStates;

import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;

import got.interfaces.INetworkListener;
import got.interfaces.IPauseable;
import got.server.GameServer.PlayerConnection;

public class StateMachine implements INetworkListener{
	public enum ChangeAction {
		SET,
		PUSH,
		REMOVE
	}
	private LinkedList<ServerState> _states;
	
	public StateMachine(){
		_states = new LinkedList<ServerState>();
	}

	public void changeState(ServerState nextState, ChangeAction action){
		pushState(new ChangeState(nextState, action));
	}

	public void setState(ServerState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		_states.push(state);
		state.enter(this);
	}

	public void pushState(ServerState state){
		if (getCurrentState() instanceof IPauseable){
			((IPauseable) getCurrentState()).pause();
		}
		_states.push(state);
		state.enter(this);
	}
	
	public void removeStateAndResume(){
		if (!_states.isEmpty()){
			_states.poll().exit();
			if (getCurrentState() instanceof IPauseable){
				((IPauseable) getCurrentState()).resume();
			}
		}
	}

	public void removeState(){
		if (!_states.isEmpty()) {
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