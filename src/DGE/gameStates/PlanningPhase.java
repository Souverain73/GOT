package DGE.gameStates;

import java.util.Vector;

import org.joml.Vector2f;

import DGE.Constants;
import DGE.Game;
import DGE.ModalState;
import DGE.gameObjects.ActionObject;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageButton;
import DGE.gameObjects.MapPartObject;

public class PlanningPhase implements GameState {
	private static final String name = "PlanningPhase";
	private Vector<ActionObject> actions;
	private ActionObject result;
	
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
	}
	
	public ActionObject createActionSelector(Vector2f pos){
		ActionSelector selector = new ActionSelector(actions, pos);
		
		(new ModalState(selector)).run();
		
		return selector.result;
	}
	
	public void placeAction(MapPartObject region, ActionObject act){
		region.setAction(act);
	}
	
	public void removeAction(MapPartObject region){
		region.setAction(null);
	}
	
	private class ActionSelector implements GameState{
		private static final String name = "ActionSelector";
		private Vector<GameObject> objects;
		public ActionObject result;
		
		public ActionSelector(Vector<? extends GameObject> actions, Vector2f pos) {
			objects = new Vector<GameObject>();
			float x = pos.x;
			float y = pos.y;
			float angle = 0;
			float step = (float)(32.7f*Math.PI/180.0f);
			float radius = Constants.ACTION_SELECTOR_RADIUS;
			
			for (GameObject gameObject : actions) {
				float cx = (float)(x+Math.cos(angle)*radius);
				float cy = (float)(y+Math.sin(angle)*radius);
				angle += step;
				ImageButton button = new ImageButton(((ActionObject)gameObject).texture, (int)cx, (int)cy, Constants.ACTION_IMAGE_SCALE, gameObject);
				button.setCallback((sender, param)->{
					result = (ActionObject)param;
					close();
				});
				objects.add(button);
			}
			
		}
		
		private void close(){
			Game.instance().closeModal();
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public void enter() {
			
		}

		@Override
		public void exit() {
			objects.clear();
		}

		@Override
		public void draw() {
			objects.forEach((obj)->{obj.draw(this);});
			
		}

		@Override
		public void update() {
			objects.forEach((obj)->{obj.update(this);});
		}
	}
}
