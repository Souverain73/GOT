package DGE.gameStates;

import java.util.LinkedList;

public class StateMachine {
	private LinkedList<GameState> _states;

	public StateMachine(){
		_states = new LinkedList<GameState>();
	}
	
	
	public void setState(GameState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		state.enter();
		_states.push(state);
	}
	
	public void pushState(GameState state){
		state.enter();
		_states.push(state);
	}
	
	public void removeState(){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
	}
	
	public void update(){
		_states.peek().update();
	}
	
	public void draw(){
		_states.peek().draw();
	}
	
	protected GameState getCurrentState(){
		return _states.peek();
	}
}
