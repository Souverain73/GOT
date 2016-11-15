package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.Fraction;
import got.GameClient;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.ActionObject.Action;
import got.gameObjects.MapPartObject.RegionType;
import got.network.Packages;
import got.server.PlayerManager;

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
	public void click(GameObject sender) {
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject)sender;
			if (state == SubState.SELECT_SOURCE){
				//remember source
				source = region;
				//change state
				state = SubState.SELECT_TARGET;
				
				GameMapObject.instance().disableAllRegions();
				//enable regions, that can be affected by fire.
				if (region.getType() == RegionType.SEA){
					//if Fire placed in sea you can use it on all neighbors.
					region.getNeighbors().forEach(obj->obj.setEnabled(true));
				}else if(region.getType() == RegionType.GROUND){
					//you can't affect SEA and ports.
					region.getNeighbors().forEach(obj->{
						if (obj.getType()==RegionType.GROUND){
							obj.setEnabled(true);
						}
					});
				}else if(region.getType() == RegionType.PORT){
					//if you are in port you can use Fire only on neighboring sea.
					region.getNeighbors().forEach(obj->{
						if (obj.getType()==RegionType.SEA){
							obj.setEnabled(true);
						}
					});
				}
				//TODO: remove regions where is nothing to ignite and your own regions.
				Fraction selfFraction = PlayerManager.getSelf().getFraction();
				GameMapObject.instance().getEnabledRegions().forEach(obj->{
					//disable if region have no action
					if (obj.getAction() == null || obj.getFraction() == selfFraction){
						obj.setEnabled(false);
					}else{
						//Fire can't affect move action
						Action type = obj.getAction().getType();
						if (type == Action.MOVE || type == Action.MOVEMINUS
								|| type == Action.MOVEPLUS){
							obj.setEnabled(false);
						}
						//Only FirePlus can affect defend actions
						if ((type == Action.DEFEND || type == Action.DEFENDPLUS) 
								&& region.getAction().getType() == Action.FIRE){
							obj.setEnabled(false);
						}
					}
					
				});
				
				//if you use Fire on itself you just remove it, so you can do it in all cases.
				region.setEnabled(true);
			}else if (state == SubState.SELECT_TARGET){
				GameClient.instance().send(new Packages.Act(source.getID(), region.getID()));
				GameMapObject.instance().disableAllRegions();
				GameClient.instance().send(new Packages.Ready(true));
				state = SubState.SELECT_SOURCE;
				source = null;
			}
		}

		super.click(sender);
	}
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.PlayerTurn){
			Packages.PlayerTurn msg = ((Packages.PlayerTurn)pkg);
			if (PlayerManager.getSelf().id == msg.playerID){
				if (!enableRegionsWithFire()){
					//Если не был активирован ни один регион, значит текущий игрок не может совершить ход.
					//В таком случае необходлимо сообщить об этом серверу пакетом Ready.
					GameClient.instance().send(new Packages.Ready(false));
				}
			}else{
				//Если чужой ход, отключаем все регионы, так как в чужой ход мы не можем совершать действий.
				GameMapObject.instance().disableAllRegions();
			}
		}
		
		if (pkg instanceof Packages.PlayerAct){
			Packages.PlayerAct msg = ((Packages.PlayerAct)pkg);
			MapPartObject source = GameMapObject.instance().getRegionByID(msg.from);
			MapPartObject target = GameMapObject.instance().getRegionByID(msg.to);
			//TODO: if target action is Money or Moneyplus get 1 money from target and put 1 money to source
			
			//remove actions from source and target region
			source.setAction(null);
			
			if (source!=target)
				target.setAction(null);
		}
		
	}
	
	/**
	 * Функция активирует регионы с приказами набега текущего игрока и отключает остальные.
	 * @return true - Если был активирован хотя бы 1 регион, иначе - false.
	 */
	private boolean enableRegionsWithFire(){
		Fraction selfFraction = PlayerManager.getSelf().getFraction();
		
		//обход ограничения на изменение переменных внутри лямбд
		boolean []result = new boolean[1];
		result[0] = false;
		
		GameMapObject.instance().setEnabledByCondition(region->{
			boolean enable = region.getFraction() == selfFraction
					&& region.getAction() != null 
					&& (region.getAction().getType() == Action.FIRE ||
						region.getAction().getType() == Action.FIREPLUS);
			result[0] |= enable;
			return enable;
		});
		
		return result[0];
	}
	
}
