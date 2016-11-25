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
import got.server.PlayerManager;

public class MovePhase extends ActionPhase {
	private static final String name = "MovePhase";
	private enum SubState {SELECT_SOURCE, SELECT_TARGET};
	private SubState state = SubState.SELECT_SOURCE;
	private MapPartObject source;
	private MapPartObject usedRegion = null;
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

					//Включаем регионы, в которые можно пойти.
					enableRegionsToMove(region);

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
					usedRegion = source;
					if (source.getUnits().length == selectedUnits.length
							&& source.havePowerToket()
							&& PlayerManager.getSelf().getMoney() > 0){

						Dialogs.Dialog confirmDialog = Dialogs.createConfirmDialog(InputManager.instance().getMousePosWorld());

						(new ModalState(confirmDialog)).run();

						if (confirmDialog.getResult() == Dialogs.DialogResult.OK){
							//TODO: send package about it
							source.placePowerToken();
						}
					}
					if (usedRegion.getUnits().length == 0){
						endTurn();
					}
				}else{
					GameClient.instance().send(new Packages.Attack(source.getID(), region.getID()));

					//На время отладки упростим бой до:
					//У кого больше силы, тот победил. При равных условиях смотри трек.

				}
				GameMapObject.instance().disableAllRegions();
				changeSubState(SubState.SELECT_SOURCE);
			}

		}

		super.click(event);
	}

	private void endTurn() {
		GameClient.instance().sendReady(true);
	}

	private void enableRegionsToMove(MapPartObject region) {
		region.getRegionsToMove().forEach(obj->{
            //проверяем, позволяет ли снабжение совершить такой ход
            if (    //На вражеские территории можно ходить не зависимо от снабжения.
                    obj.getFraction() != region.getFraction()
                    ||  Game.instance().getSuplyTrack().canMove(
                        region.getFraction(),
                        GameMapObject.instance().getArmySizesForFraction(region.getFraction()),
                        region.getUnitsCount(),
                        obj.getUnitsCount(),
                        selectedUnits.length)
                    )
                obj.setEnabled(true);
        });
	}

	@Override
	public void recieve(Connection c, Object pkg) {
		if (pkg instanceof Packages.PlayerTurn) {
			Packages.PlayerTurn msg = (Packages.PlayerTurn) pkg;

			if (msg.playerID == PlayerManager.getSelf().id){
				usedRegion = null;
				if (!enableRegionsWithMoveAction()){
					GameClient.instance().sendReady(false);
				}
			}else{
				GameMapObject.instance().disableAllRegions();
			}
		}
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

	public boolean enableRegionsWithMoveAction(){
		if (usedRegion != null){
			usedRegion.setEnabled(true);
			return true;
		}else {
			return GameMapObject.instance().setEnabledByCondition((region) -> {
				Action act = region.getAction();
				if (act == null) return false;
				if ((act == Action.MOVEMINUS || act == Action.MOVEPLUS
						|| act == Action.MOVE) &&
						region.getFraction() == PlayerManager.getSelf().getFraction()) return true;
				return false;
			}) > 0;
		}
	}

	public void changeSubState(SubState newState){
		state = newState;
		if (state == SubState.SELECT_TARGET) {
			//enable regions where player can go
		}else if (state == SubState.SELECT_SOURCE){
			enableRegionsWithMoveAction();
			source = null;
			selectedUnits = null;
		}
	}
}
