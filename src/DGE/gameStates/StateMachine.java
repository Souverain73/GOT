package DGE.gameStates;

import java.util.LinkedList;

public class StateMachine {
	private LinkedList<GameState> _states;
	
	private static StateMachine _instance = null;
	private StateMachine(){
		_states = new LinkedList<GameState>();
	}
	
	public static StateMachine instance(){
		if (_instance == null){
			_instance = new StateMachine();
		}
		return _instance;
	}
	
	public void setState(GameState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		state.enter();
		_states.push(state);
	}
}
