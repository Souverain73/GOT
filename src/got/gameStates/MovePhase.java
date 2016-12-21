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
					source = region;
					changeSubState(SubState.SELECT_TARGET);
					GameClient.shared.gameMap.disableAllRegions();

					//Включаем регионы, в которые можно пойти.
					enableRegionsToMove(region);

				}else{
					endTurn(region);
				}
			}else if (state == SubState.SELECT_TARGET){
				if (region.getFraction() == Fraction.NEUTRAL ||
						region.getFraction() == PlayerManager.getSelf().getFraction()){

					GameClient.instance().send(new Packages.Move(
							source.getID(),
							region.getID(),
							selectedUnits
					));
					usedRegion = source;
					//Если покидаем регион без жетона власти и есть свободные жетоны
					if (source.getUnits().length == selectedUnits.length
							&& !source.havePowerToket()
							&& PlayerManager.getSelf().getMoney() > 0){
						//оставить ли жетон?
						Dialogs.Dialog confirmDialog = Dialogs.createConfirmDialog(InputManager.instance().getMousePosWorld());

						(new ModalState(confirmDialog)).run();

						if (confirmDialog.getResult() == Dialogs.DialogResult.OK){
							//TODO: отправить на сервер пакет о том что игрок оставляет жетон власти
							source.placePowerToken();
						}
					}
					if (usedRegion.getUnits().length == selectedUnits.length){
						endTurn(usedRegion);
					}
				}else{
					GameClient.instance().send(new Packages.Attack(source.getID(), region.getID(),
							PlayerManager.instance().getPlayerByFraction(source.getFraction()).id,
							PlayerManager.instance().getPlayerByFraction(region.getFraction()).id));

					//На время отладки упростим бой до:
					//У кого больше силы, тот победил. При равных условиях смотри трек.
				}
				GameClient.shared.gameMap.disableAllRegions();
				changeSubState(SubState.SELECT_SOURCE);
			}

		}

		super.click(event);
	}

	private void endTurn(MapPartObject regionWithUsedAction) {
		if (regionWithUsedAction != null){
			GameClient.instance().send(new Packages.Act(regionWithUsedAction.getID(), 0));
		}
		GameClient.instance().sendReady(true);
	}

	private void enableRegionsToMove(MapPartObject region) {
		region.getRegionsToMove().forEach(obj->{
            //проверяем, позволяет ли снабжение совершить такой ход
            if (    //На вражеские территории можно ходить не зависимо от снабжения.
                    obj.getFraction() != region.getFraction()
                    ||  Game.instance().getSuplyTrack().canMove(
                        region.getFraction(),
                        GameClient.shared.gameMap.getArmySizesForFraction(region.getFraction()),
                        region.getUnitsCount(),
                        obj.getUnitsCount(),
                        selectedUnits.length)
                    )
                obj.setEnabled(true);
        });
		region.setEnabled(true);
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
				GameClient.shared.gameMap.disableAllRegions();
			}
		}
		if (pkg instanceof Packages.PlayerMove) {
			Packages.PlayerMove msg = (Packages.PlayerMove) pkg;
			GameClient.instance().registerTask(()->{
				Player player = PlayerManager.instance().getPlayer(msg.player);

				MapPartObject regionFrom = GameClient.shared.gameMap.getRegionByID(msg.from);
				regionFrom.removeUnits(msg.units);

				MapPartObject regionTo = GameClient.shared.gameMap.getRegionByID(msg.to);
				regionTo.addUnits(msg.units);
				regionTo.setFraction(player.getFraction());
			});
		}
		if (pkg instanceof Packages.PlayerAct) {
			GameClient.shared.gameMap.getRegionByID(((Packages.PlayerAct) pkg).from).setAction(null);
		}
	}

	public boolean enableRegionsWithMoveAction(){
		if (usedRegion != null){
			usedRegion.setEnabled(true);
			return true;
		}else {
			return GameClient.shared.gameMap.setEnabledByCondition((region) -> {
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
