package DGE.gameStates;

import java.util.EnumMap;
import java.util.Vector;

import org.joml.Vector2f;

import DGE.Constants;
import DGE.Game;
import DGE.InputManager;
import DGE.ModalState;
import DGE.gameObjects.ActionObject;
import DGE.gameObjects.ActionObject.Action;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageButton;
import DGE.gameObjects.MapPartObject;
import DGE.interfaces.IClickListener;

public class PlanningPhase implements GameState, IClickListener {
	private StateMachine stm;
	private static final String name = "PlanningPhase";
	private Vector<ActionObject> actions;
	private int specials;
	private EnumMap<Action, MapPartObject> placed;
	
	private Vector<GameObject> objects;
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
		placed = new EnumMap<Action, MapPartObject>(Action.class);
		actions = ActionObject.getAllActionObjects();
		objects = new Vector<GameObject>();
		specials = 0;
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
		if (InputManager.instance().keyPressed(32)){
			System.out.println("Space pressed");
//			GameMapObject.instance().setEnabledByCondition(region -> region.getAction()!=null);
			stm.setState(new CollectUnitsPhase());
		}
	}
	
	public ActionObject createActionSelector(Vector2f pos){
		ActionSelector selector = new ActionSelector(actions, pos);
		
		(new ModalState(selector)).run();
		
		return selector.result;
	}
	
	public void placeAction(MapPartObject region, ActionObject act){
		region.setAction(act);
		if (act!= null){
			act.setOwner(region);
			placed.put(act.getType(), region);
			if (act.isSpecial()) 
				specials++;
		}
	}
	
	public void click(GameObject sender){
		if (sender instanceof MapPartObject) {
			if (((MapPartObject) sender).getAction() == null) {
				placeAction((MapPartObject) sender, createActionSelector(InputManager.instance().getMousePosWorld()));
			} else {
				removeAction((MapPartObject) sender);
			}
		}else if (sender instanceof ActionObject){
			System.out.println("ClickClick");
		}
	}
	
	public void removeAction(MapPartObject region){
		ActionObject oldAction = region.getAction();
		placed.remove(oldAction.getType());
		if(oldAction.isSpecial()) 
			specials--;
		oldAction.setOwner(0);
		oldAction.finish();
		region.setAction(null);
	}
	
	private class ActionSelector implements GameState{
		private static final String name = "ActionSelector";
		private Vector<GameObject> objects;
		public ActionObject result;
		
		public ActionSelector(Vector<ActionObject> actions, Vector2f pos) {
			objects = new Vector<GameObject>();
			float x = pos.x;
			float y = pos.y;
			float angle = 0;
			float step = (float)(32.7f*Math.PI/180.0f);
			float radius = Constants.ACTION_SELECTOR_RADIUS;
			
			for (ActionObject actionObject : actions) {
				float cx = (float)(x+Math.cos(angle)*radius);
				float cy = (float)(y+Math.sin(angle)*radius);
				ImageButton button = new ImageButton(((ActionObject)actionObject).texture, (int)cx, (int)cy, Constants.ACTIONSELECTOR_IMAGE_SCALE, actionObject);
				button.setCallback((sender, param)->{
					result = (ActionObject)param;
					close();
				});
				if ((actionObject.isSpecial() && specials>=Game.instance().getPlayer().getSpecials())
						||	placed.containsKey(actionObject.getType())){
					button.setEnabled(false);
				}
				objects.add(button);
				angle += step;
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
		public void enter(StateMachine stm) {
			
		}

		@Override
		public void exit() {
			objects.forEach(obj->obj.finish());
			objects.clear();
		}

		@Override
		public void draw() {
			objects.forEach((obj)->{obj.draw(this);});
			
		}

		@Override
		public void update() {
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1 
					&& !(InputManager.instance().getMouseTarget() instanceof ImageButton)){
				close();
			}
			objects.forEach((obj)->{obj.update(this);});
		}
	}
}
