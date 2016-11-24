package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.SelectUnitsDialogState;
import got.model.*;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;

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
		changeSubState(SubState.SELECT_SOURCE);
		super.enter(stm);
	}


	@Override
	public void click(InputManager.ClickEvent event) {
		System.out.println(event);
		GameObject sender = event.getTarget();
		if (event.getButton() == InputManager.MOUSE_RIGHT){
			changeSubState(SubState.SELECT_SOURCE);
			return;
		}
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;

			if (state == SubState.SELECT_SOURCE){
				SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits(), InputManager.instance().getMousePosWorld());
				(new ModalState(suds)).run();
				selectedUnits = suds.getSelectedUnits();
				if (suds.isOk() && selectedUnits.length!=0){
					System.out.println("Ok");
					source = region;
					changeSubState(SubState.SELECT_TARGET);
					GameMapObject.instance().disableAllRegions();

					region.getRegionsToMove().forEach(obj->{
						//проверяем, позволяет ли снабжение совершить такой ход
						if (    //На вражеские территории можно ходить не зависимо от снабжения.
								obj.getFraction() != region.getFraction() ||
								Game.instance().getSuplyTrack().canMove(
									region.getFraction(),
									GameMapObject.instance().getArmySizesForFraction(region.getFraction()),
									region.getUnitsCount(),
									obj.getUnitsCount(),
									selectedUnits.length)
								)
							obj.setEnabled(true);
					});

				}else{
					System.out.println("Cancel");
				}
			}else if (state == SubState.SELECT_TARGET){
				if (region == source)
					changeSubState(SubState.SELECT_SOURCE);

				if (region.getFraction() == Fraction.NEUTRAL ||
						region.getFraction() == PlayerManager.getSelf().getFraction()){
					GameClient.instance().send(new Packages.Move(
							source.getID(),
							region.getID(),
							selectedUnits
					));
					if (source.getUnits().length == selectedUnits.length){
						Dialogs.Dialog confirmDialog = Dialogs.createConfirmDialog(InputManager.instance().getMousePosWorld());

						(new ModalState(confirmDialog)).run();

						if (confirmDialog.getResult() == Dialogs.DialogResult.OK){
							region.placePowerToken();
						}
					}
				}else{
					//Инициировать бой.
				}
				changeSubState(SubState.SELECT_SOURCE);
			}

		}

		super.click(event);
	}

	@Override
	public void recieve(Connection c, Object pkg) {
		if (pkg instanceof Packages.PlayerMove) {
			Packages.PlayerMove msg = (Packages.PlayerMove) pkg;
			GameClient.instance().registerTask(()->{
				Player player = PlayerManager.instance().getPlayer(msg.player);

				MapPartObject regionFrom = GameMapObject.instance().getRegionByID(msg.from);
				regionFrom.removeUnits(msg.units);

				MapPartObject regionTo = GameMapObject.instance().getRegionByID(msg.to);
				regionTo.addUnits(msg.units);
				regionTo.setFraction(player.getFraction());
			});
		}
	}

	public void enableRegionsWithMove(){
		GameMapObject.instance().setEnabledByCondition((region)->{
			Action act = region.getAction();
			if (act == null) return false;
			if ((act == Action.MOVEMINUS || act == Action.MOVEPLUS
					|| act == Action.MOVE) &&
					region.getFraction() == PlayerManager.getSelf().getFraction()) return true;
			return false;
		});
	}

	public void changeSubState(SubState newState){
		state = newState;
		if (state == SubState.SELECT_TARGET) {
			//enable regions where player can go
		}else if (state == SubState.SELECT_SOURCE){
			enableRegionsWithMove();
			source = null;
			selectedUnits = null;
		}
	}
}
