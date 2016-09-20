package DGE.gameStates;

import DGE.gameObjects.ActionObject.Action;
import DGE.gameObjects.ActionObject;
import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;

public class MovePhase extends ActionPhase {
	private static final String name = "MovePhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		GameMapObject.instance().setEnabledByCondition((region)->{
			ActionObject act = region.getAction();
			if (act == null) return false;
			if (act.getType() == Action.MOVEMINUS || act.getType() == Action.MOVEPLUS
					|| act.getType() == Action.MOVE) return true;
			return false;
		});
	}

	@Override
	public void click(GameObject sender) {
		
		super.click(sender);
	}
	
}
