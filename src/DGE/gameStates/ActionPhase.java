package DGE.gameStates;

import DGE.gameObjects.GameObject;
import DGE.interfaces.IClickListener;

public class ActionPhase implements GameState, IClickListener{
	private StateMachine stm;
	private static final String name = "ActionPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
	}

	@Override
	public void exit() {
		
	}

	@Override
	public void draw() {
		
	}

	@Override
	public void update() {
		
	}
	
	
	public void click(GameObject sender){
		
	}
}
