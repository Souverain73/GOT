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
		_states.push(state);
		state.enter(this);
	}
	
	public void pushState(GameState state){
		_states.push(state);
		state.enter(this);
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
	
	public GameState getCurrentState(){
		return _states.peek();
	}
}
