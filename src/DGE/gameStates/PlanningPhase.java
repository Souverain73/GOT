package DGE.gameStates;

import java.util.Vector;

import javax.management.relation.RelationNotification;

import org.joml.Vector2f;

import DGE.InputManager;
import DGE.gameObjects.ActionObject;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageButton;
import DGE.gameObjects.MapPartObject;

public class PlanningPhase implements GameState {
	private static final String name = "PlanningPhase";
	private Vector<ActionObject> actions;
	private MapPartObject regionToPlace;
	private boolean removeActionSelector = false;
	
	private Vector<GameObject> objects;
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void enter() {
		actions = ActionObject.getAllActionObjects();
		objects = new Vector<GameObject>();
	}

	@Override
	public void exit() {
	}

	@Override
	public void draw() {
		objects.forEach((obj)->{obj.draw(this);});
	}

	@Override
	public void update() {
		objects.forEach((obj)->{obj.update(this);});
		if (removeActionSelector){
			objects.clear();
			removeActionSelector = false;
		}
	}

	public void createActionSelector(Vector2f pos){
		float x = pos.x;
		float y = pos.y-200;
		for (ActionObject action : actions) {
			ImageButton button = new ImageButton(action.texture, (int)x, (int)y, 1.0f, action);
			button.setCallback(this::selectAction);
			objects.add(button);
			x+=60;	
		}
	}
		
	public void selectAction(GameObject sender, Object params){
		removeActionSelector = true;
		regionToPlace.setAction((ActionObject)params);
		regionToPlace = null;
	}
	
	public void placeAction(MapPartObject region){
		regionToPlace = region;
		createActionSelector(InputManager.instance().getMousePosWorld());
	}
	
	public void removeAction(MapPartObject region){
		region.setAction(null);
	}
}
