package got.gameStates;

import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.gameObjects.DebugPanel;
import got.interfaces.INetworkListener;

public class StateMachine implements INetworkListener {
	private LinkedList<GameState> _states;
	
	public StateMachine(){
		_states = new LinkedList<GameState>();
	}
	
	public void setState(GameState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		_states.push(state);
		state.enter(this);
	}
	
	/**
	 * Push state in the states stack
	 * @param state
	 */
	public void pushState(GameState state){
		_states.push(state);
		state.enter(this);
	}
	
	/**
	 * Remove last state from states stack
	 */
	public void removeState(){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
	}
	
	public void update(){
		if (!_states.isEmpty())
			getCurrentState().update();
	}
	
	public void draw(){
		if (!_states.isEmpty())
			getCurrentState().draw();
	}
	
	public void tick(){
		if (!_states.isEmpty()){
			getCurrentState().tick();
		}
	}
	
	public GameState getCurrentState(){
		return _states.peek();
	}
	
	public void recieve(Connection connection, Object pkg){
		GameState st = getCurrentState();
		if (st!=null){
			st.recieve(connection, pkg);
		}
	}
}
