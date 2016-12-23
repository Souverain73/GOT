package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.InputManager;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.MapPartObject.RegionType;
import got.model.Action;
import got.model.Fraction;
import got.network.Packages;
import got.server.PlayerManager;
import got.utils.UI;

import java.util.List;
import java.util.stream.Stream;

public class FirePhase extends ActionPhase {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private final String name = "FirePhase";

	
	private SubState state;
	
	private MapPartObject source; //Region with source Fire Action point source
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void enter(StateMachine stm) {
		state = SubState.SELECT_SOURCE;
		super.enter(stm);
	}


	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject)sender;
			if (state == SubState.SELECT_SOURCE){
				//remember source
				source = region;
				//change state
				state = SubState.SELECT_TARGET;
				enableRegionsAffectedByFire(region);


			}else if (state == SubState.SELECT_TARGET){
				GameClient.instance().send(new Packages.Act(source.getID(), region.getID()));
				GameClient.shared.gameMap.disableAllRegions();
				GameClient.instance().send(new Packages.Ready(true));
				state = SubState.SELECT_SOURCE;
				source = null;
			}
		}

		super.click(event);
	}

	private void enableRegionsAffectedByFire(MapPartObject regionFrom) {
		GameClient.shared.gameMap.disableAllRegions();
		Fraction selfFraction = regionFrom.getFraction();
		Stream<MapPartObject> affectedRegions = regionFrom.getNeighbors().stream();

		if (regionFrom.getType() == RegionType.SEA){
			//Можно напасть на всех
		}else if(regionFrom.getType() == RegionType.GROUND){
			//Можно напасть только на землю
			affectedRegions = affectedRegions.filter(obj->obj.getType()== RegionType.GROUND);
		}else if(regionFrom.getType() == RegionType.PORT){
			//Можно нападать только на море
			affectedRegions = affectedRegions.filter(obj->obj.getType()== RegionType.SEA);
		}

		//убираем регионы без дприказов и свои регионы
		affectedRegions = affectedRegions.filter(reg->!(reg.getAction() == null || reg.getFraction() == selfFraction));
		//Убираем регионы с приказами походов
		affectedRegions = affectedRegions.filter(reg->!(reg.getAction() == Action.MOVE || reg.getAction() == Action.MOVEMINUS
				|| reg.getAction() == Action.MOVEPLUS));
		//Если это обычный набег, убираем регионы с приказом обороны
		if (regionFrom.getAction() == Action.FIRE){
			affectedRegions = affectedRegions.filter(reg->!(reg.getAction() == Action.DEFEND || reg.getAction() == Action.DEFENDPLUS));
		}

		affectedRegions.forEach(reg->reg.setEnabled(true));

		//if you use Fire on itself you just remove it, so you can do it in all cases.
		regionFrom.setEnabled(true);
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.PlayerTurn){
			Packages.PlayerTurn msg = ((Packages.PlayerTurn)pkg);
			if (PlayerManager.getSelf().id == msg.playerID){
				if (!enableRegionsWithFire()){
					GameClient.instance().send(new Packages.Ready(false));
				}
			}else{
				GameClient.shared.gameMap.disableAllRegions();
			}
		}
		
		if (pkg instanceof Packages.PlayerAct){
			Packages.PlayerAct msg = ((Packages.PlayerAct)pkg);
			MapPartObject source = GameClient.shared.gameMap.getRegionByID(msg.from);
			MapPartObject target = GameClient.shared.gameMap.getRegionByID(msg.to);

			//if target action is Money or Moneyplus get 1 money from target and put 1 money to source
			if (target.getAction() == Action.MONEY || target.getAction() == Action.MONEYPLUS) {
				source.getOwnerPlayer().addMoney(1);
				target.getOwnerPlayer().addMoney(-1);
			}

			//remove actions from source and target region
			source.setAction(null);
			
			if (source!=target)
				target.setAction(null);
		}
		
	}

	private boolean enableRegionsWithFire(){
		Fraction selfFraction = PlayerManager.getSelf().getFraction();

		return GameClient.shared.gameMap.setEnabledByCondition(region->{
			boolean enable = region.getFraction() == selfFraction
					&& region.getAction() != null 
					&& (region.getAction() == Action.FIRE ||
						region.getAction() == Action.FIREPLUS);
			return enable;
		}) > 0;

	}
	
}
