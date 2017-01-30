package got.gameStates;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import com.esotericsoftware.kryo.io.Input;
import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.model.Action;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.MapPartObject;
import got.gameObjects.GameMapObject;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.model.Player;
import got.network.Packages.PlayerSetAction;
import got.network.Packages.Ready;
import got.network.Packages.SetAction;
import got.server.PlayerManager;

import static got.utils.UI.logAction;

public class PlanningPhase extends AbstractGameState implements IClickListener {
	private static final String name = "PlanningPhase";
	private List<Action> actions;
	private int specials;
	private EnumMap<Action, MapPartObject> placed;
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void enter(StateMachine stm) {
		GameClient.instance().setTooltipText("planning.selectRegion");
		//готовим игровую карту.
		//TODO: перенести в фазу вестероса, когда она будет готова
		GameClient.shared.gameMap.forEachRegion(region->{
			region.setAction(null);
			region.resurectUnits();
		});

		placed = new EnumMap<>(Action.class);
		actions = Arrays.asList(Action.values());
		specials = 0;
		
		//Add ready button
		ImageButton btn = new ImageButton("buttons/ready.png", 1070, 610, 200, 100, null)
			.setSpace(DrawSpace.SCREEN)
			.setCallback((sender, param)->{
			GameClient.instance().send(new Ready(!PlayerManager.getSelf().isReady()));
		});
		addObject(btn);
		
		//all players are not ready at beginning of that phase
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		
		GameClient.shared.gameMap.enableAllRegions();
		super.enter(stm);
	}

	@Override
	public void draw() {
		super.draw();
	}

	@Override
	public void update() {
		super.update();
	}
	
	public Action createActionSelector(Vector2f pos){
		ActionSelector selector = new ActionSelector(actions, pos);
		
		(new ModalState(selector)).run();
		
		return selector.result;
	}
	
	public void placeAction(MapPartObject region, Action act){
		if (act!= null){
			placed.put(act, region);
			if (act.isSpecial()) 
				specials++;
		}
	}
	
	public void click(InputManager.ClickEvent event){
		if (event.getTarget() instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) event.getTarget();
			
			//if player click not owned region
			if (region.getFraction() != PlayerManager.getSelf().getFraction())
				return;
			
			//if region have no action, have placed units, and it owned by player
			//player can place her action
			if (region.getAction() == null && region.getUnitObjects().size()>0) {
				
				//create Action selector and wait until user select something
				GameClient.instance().setTooltipText("planning.selectAction");
				Action action = createActionSelector(InputManager.instance().getMousePosWorld());
				GameClient.instance().setTooltipText("planning.selectRegion");

				//player can just close selector without choice anything
				if (action!=null)
					//notify server about placed action
					GameClient.instance().send(new SetAction(region.getID(), action));
			} else if (region.getAction()!=null){
				//if package passed with null it means player remove action
				GameClient.instance().send(new SetAction(region.getID(), null));
			}
		}
	}
	
	public void removeAction(MapPartObject region){
		Action oldAction = region.getAction();
		placed.remove(oldAction);
		if(oldAction.isSpecial()) 
			specials--;
	}
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof PlayerSetAction){
			PlayerSetAction msg = ((PlayerSetAction)pkg);
			MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					if (msg.action==null){
						logAction("common.playerRemoveAction", region.getName());
						//if it's your action. in this case it must be handled before remove action from map.
						if (region.getFraction() == PlayerManager.getSelf().getFraction())
							//do some routine to handle your actions set
							removeAction(region);
						//remove object from region
						region.setAction(null); 
					}else{
						Action act = msg.action;
						logAction("common.playerSetAction", region.getName());
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



	public class ActionSelector extends AbstractGameState implements IClickListener{
		private static final String name = "ActionSelector";
		public Action result;
		
		public ActionSelector(List<Action> actions, Vector2f pos) {
			float x = pos.x;
			float y = pos.y;
			float angle = 0;
			float step = (float)(32.7f*Math.PI/180.0f);
			float radius = Constants.ACTION_SELECTOR_RADIUS;
			
			for (Action action : actions) {
				float cx = (float)(x+Math.cos(angle)*radius);
				float cy = (float)(y+Math.sin(angle)*radius);
				ImageButton button = new ImageButton(action.getTexture(), (int)cx, (int)cy, Constants.ACTIONSELECTOR_IMAGE_SCALE, action);
				button.setCallback((sender, param)->{
					result = (Action)param;
					close();
				});
				if ((action.isSpecial() && specials>=PlayerManager.getSelf().getSpecials())
						||	placed.containsKey(action)){
					button.setEnabled(false);
				}
				addObject(button);
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
		public int getID() {
			return -1;
		}

		@Override
		public void click(InputManager.ClickEvent event) {
			if (event.getTarget() == null){
				close();
			}
		}
	}
}
