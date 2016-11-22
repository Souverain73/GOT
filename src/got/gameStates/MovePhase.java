package got.gameStates;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.SelectUnitsDialogState;
import got.model.Action;
import got.model.Game;
import got.model.Unit;
import got.network.Packages;

import java.util.Arrays;

public class MovePhase extends ActionPhase {
	private static final String name = "MovePhase";
	private enum SubState {SELECT_SOURCE, SELECT_TARGET};
	private SubState state = SubState.SELECT_SOURCE;
	private MapPartObject source;
	private Unit[] selectedUnits;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		GameMapObject.instance().forEach(region->{
			region.setAction(Action.MOVE);
		});
		enableRegionsWithMove();
		super.enter(stm);
	}

	@Override
	public void click(GameObject sender) {
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;

			if (state == SubState.SELECT_SOURCE){
				SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits(), InputManager.instance().getMousePosWorld());
				(new ModalState(suds)).run();
				Unit [] selectedUnits = suds.getSelectedUnits();
				if (suds.isOk() && selectedUnits.length!=0){
					System.out.println("Ok");
					source = region;
					changeSubState(SubState.SELECT_TARGET);
				}else{
					System.out.println("Cancel");
				}
			}

			if (state == SubState.SELECT_TARGET){
				GameClient.instance().send(new Packages.Move(
						source.getID(),
						region.getID(),
						selectedUnits
				));
				changeSubState(SubState.SELECT_SOURCE);
			}

		}

		super.click(sender);
	}

	public void enableRegionsWithMove(){
		GameMapObject.instance().setEnabledByCondition((region)->{
			Action act = region.getAction();
			if (act == null) return false;
			if (act == Action.MOVEMINUS || act == Action.MOVEPLUS
					|| act == Action.MOVE) return true;
			return false;
		});
	}

	public void changeSubState(SubState newState){
		state = newState;
		if (state == SubState.SELECT_TARGET) {
			//enable regions where player can go
		}else if (state == SubState.SELECT_SOURCE){
			source = null;
			selectedUnits = null;
		}
	}
}
