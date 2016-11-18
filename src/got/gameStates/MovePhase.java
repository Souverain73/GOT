package got.gameStates;

import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.model.Action;

public class MovePhase extends ActionPhase {
	private static final String name = "MovePhase";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		GameMapObject.instance().setEnabledByCondition((region)->{
			Action act = region.getAction();
			if (act == null) return false;
			if (act == Action.MOVEMINUS || act == Action.MOVEPLUS
					|| act == Action.MOVE) return true;
			return false;
		});
	}

	@Override
	public void click(GameObject sender) {
		
		super.click(sender);
	}
	
}
