package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.SelectUnitsDialogState;
import got.model.*;
import got.network.Packages;
import got.server.PlayerManager;

import static got.utils.UI.logAction;
import static got.utils.UI.logSystem;

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
				if (region == source){
					GameClient.shared.gameMap.disableAllRegions();
					changeSubState(SubState.SELECT_SOURCE);
					return;
				}
				//можно пойти
				if (region.getFraction() == Fraction.NONE || //в нейтральные земли
						region.getFraction() == PlayerManager.getSelf().getFraction() || //в свои земли
						region.getUnitsCount() == 0 ){ //в земли врага, где нет войск

					usedRegion = source;

					checkIfPowerTokenNeededAndCanBePlaced(source);


					GameClient.instance().send(new Packages.Move(
							source.getID(),
							region.getID(),
							selectedUnits
					));

					if (usedRegion.getUnits().length == selectedUnits.length){
						endTurn(usedRegion);
					}
				}else{
					//Жетон власти оставляется до начала боя
					selectedUnits = source.getUnits();
					checkIfPowerTokenNeededAndCanBePlaced(source);

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

	private void checkIfPowerTokenNeededAndCanBePlaced(MapPartObject region) {
		//Если покидаем регион без жетона власти и есть свободные жетоны
		if (region.getUnits().length == selectedUnits.length
                && !region.havePowerToket()
                && PlayerManager.getSelf().getMoney() > 0){
            //оставить ли жетон?
            Dialogs.Dialog confirmDialog = Dialogs.createConfirmDialog(InputManager.instance().getMousePosWorld());

            (new ModalState(confirmDialog)).run();

            if (confirmDialog.getResult() == Dialogs.DialogResult.OK){
                GameClient.instance().send(new Packages.PlacePowerToken(region.getID()));
            }
        }
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
			logSystem("Следующий ход");
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

				if (regionFrom.getUnitsCount() == 0 && !regionFrom.havePowerToket()){
					regionFrom.setFraction(Fraction.NONE);
				}

				MapPartObject regionTo = GameClient.shared.gameMap.getRegionByID(msg.to);
				//Если перешел во вражеский регион, надо убрать жетон власти
				if (player.getFraction() != regionTo.getFraction()){
					regionTo.removePowerToken();
				}
				logAction("Игрок " + player.getNickname() + " перемещает юнитов из " + regionFrom.getName() + " в " + regionTo.getName());
				regionTo.addUnits(msg.units);
				regionTo.setFraction(player.getFraction());
			});
		}
		if (pkg instanceof Packages.PlayerAct) {
			GameClient.shared.gameMap.getRegionByID(((Packages.PlayerAct) pkg).from).setAction(null);
		}
		if (pkg instanceof Packages.PlayerPlacePowerToken) {
			Packages.PlayerPlacePowerToken msg = (Packages.PlayerPlacePowerToken) pkg;
			MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.regionId);
			logAction("Игрок кладет жетон власти в регионе " + region.getName());
			PlayerManager.instance().getPlayer(msg.playerId).placePowerTokenAtRegion(region);
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
