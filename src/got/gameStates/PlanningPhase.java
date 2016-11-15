package got.gameStates;

import java.util.EnumMap;
import java.util.Vector;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.Player;
import got.gameObjects.AbstractButtonObject;
import got.gameObjects.ActionObject;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.MapPartObject;
import got.gameObjects.ActionObject.Action;
import got.gameObjects.GameMapObject;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.network.Packages.PlayerSetAction;
import got.network.Packages.Ready;
import got.network.Packages.SetAction;
import got.server.PlayerManager;

public class PlanningPhase extends AbstractGameState implements IClickListener {
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
		
		//Add ready button
		AbstractButtonObject btn = new ImageButton("buttons/ready.png", 1070, 610, 200, 100, null);
		btn.setSpace(DrawSpace.SCREEN);
		btn.setCallback((sender, param)->{
			GameClient.instance().send(new Ready(!PlayerManager.getSelf().isReady()));
		});
		addObject(btn);
		
		//all players are not ready at beginning of that phase
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		
		GameMapObject.instance().enableAllRegions();
		
		System.out.println("Entering PlanningPhase");
		super.enter(stm);
	}

	@Override
	public void exit() {
		super.exit();
	}

	@Override
	public void draw() {
		objects.forEach((obj)->{obj.draw(this);});
		super.draw();
	}

	@Override
	public void update() {
		objects.forEach((obj)->{obj.update(this);});
		super.update();
	}
	
	public Action createActionSelector(Vector2f pos){
		ActionSelector selector = new ActionSelector(actions, pos);
		
		(new ModalState(selector)).run();
		
		ActionObject selected = selector.result;
		
		if (selected == null) return null;
		
		return selected.getType();
	}
	
	public void placeAction(MapPartObject region, ActionObject act){
		if (act!= null){
			act.setOwner(region);
			placed.put(act.getType(), region);
			if (act.isSpecial()) 
				specials++;
		}
	}
	
	public void click(GameObject sender){
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;
			
			//if player click not owned region
			if (region.getFraction() != PlayerManager.getSelf().getFraction())
				return;
			
			//if region have no action, have placed units, and it owned by player
			//player can place her action
			if (region.getAction() == null && region.getUnits().size()>0) {
				
				//create Action selector and wait until user select something
				Action action = createActionSelector(InputManager.instance().getMousePosWorld());
				
				/*//place object on client side
				 * you can do something on client side only after server let you
					placeAction(region, action);
				*/
				
				//player can just close selector without choice anything
				if (action!=null)
					//notify server about placed action
					GameClient.instance().send(new SetAction(region.getID(), action));
			} else if (region.getAction()!=null){
				//if package passed with null it means player remove action
				GameClient.instance().send(new SetAction(region.getID(), null));
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

	}
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof PlayerSetAction){
			PlayerSetAction msg = ((PlayerSetAction)pkg);
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.region);
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					if (msg.action==null){
						//if it's your action. in this case it must be handled before remove action from map.
						if (region.getFraction() == PlayerManager.getSelf().getFraction())
							//do some routine to handle your actions set
							removeAction(region);
						//remove object from region
						region.setAction(null); 
					}else{
						ActionObject act = ActionObject.getActionObject(msg.action);
						//add action to region
						region.setAction(act);
						//if it's your action
						if (region.getFraction() == PlayerManager.getSelf().getFraction())
							//handle your actions set
							placeAction(region, act);
					}
				}
			});
		}
		
	}
	
	@Override
	public int getID() {
		return StateID.PLANNING_PHASE;
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
				if ((actionObject.isSpecial() && specials>=PlayerManager.getSelf().getSpecials())
						||	placed.containsKey(actionObject.getType())){
					button.setEnabled(false);
				}
				objects.add(button);
				angle += step;
			}
			
		}
		
		private void close(){
			GameClient.instance().closeModal();
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

		@Override
		public void recieve(Connection connection, Object pkg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getID() {
			return -1;
		}
	}
}
