package DGE.gameStates;

import DGE.gameObjects.ActionObject.Action;
import DGE.gameObjects.MapPartObject.RegionType;
import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.MapPartObject;

public class FirePhase extends ActionPhase {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private SubState state;
	
	private MapPartObject source; //Region with source Fire Action point source
	
	@Override
	public void enter(StateMachine stm) {
		//First player must select Fire action that he will perform
		state = SubState.SELECT_SOURCE;
		
		//enable only regions with Fire and FirePlus actions.
		enableRegionsWithFire();
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
					//if you are in port ypu can use Fire only on neighboring sea.
					region.getNeighbors().forEach(obj->{
						if (obj.getType()==RegionType.SEA){
							obj.setEnabled(true);
						}
					});
				}
				//TODO: remove regions where is nothing to ignite.
				GameMapObject.instance().getEnabledRegions().forEach(obj->{
					//disable if region have no action
					if (obj.getAction() == null){
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
				//TODO: if target action is Money or Moneyplus get 1 money from target and put 1 money to source
				
				//remove actions from source and target region
				source.setAction(null);
				if (source!=region)
					region.setAction(null);
				source = null;
				//change phase state
				state = SubState.SELECT_SOURCE;
				//enable only regions with Fire and FirePlus actions.
				enableRegionsWithFire();
			}
		}

		super.click(sender);
	}
	
	private void enableRegionsWithFire(){
		GameMapObject.instance().setEnabledByCondition(region->{
			return region.getAction() != null 
					&& (region.getAction().getType() == Action.FIRE ||
						region.getAction().getType() == Action.FIREPLUS);
		});
	}
}
