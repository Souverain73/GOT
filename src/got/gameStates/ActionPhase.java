package got.gameStates;

import got.gameObjects.GameObject;
import got.interfaces.IClickListener;

public class ActionPhase implements GameState, IClickListener{
	protected StateMachine stm;
	private static final String name = "ActionPhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
//		stm.pushState(new MovePhase());
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
