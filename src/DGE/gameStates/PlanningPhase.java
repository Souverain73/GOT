package DGE.gameStates;

import java.util.Vector;

import org.joml.Vector2f;

import DGE.gameObjects.ActionObject;
import DGE.gameObjects.MapPartObject;

public class PlanningPhase implements GameState {
	private static final String name = "PlanningPhase";
	private Vector<ActionObject> actions;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter() {
		actions = new Vector<ActionObject>();
		//TODO: add actions depending on player;
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

	public void drawActionsSelector(Vector2f pos){
		//Implement actions selector
	}
	
	public void placeAction(MapPartObject region){
		
	}
	
	public void removeAction(MapPartObject region){
		
	}
}
