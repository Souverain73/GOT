package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.SelectUnitsDialogState;
import got.model.Game;
import got.model.Player;
import got.model.SuplyTrack;
import got.model.Unit;
import got.network.Packages;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 15.12.2016.
 */
public class BattleResultState extends ActionPhase{
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        //Участники боя объявляют результаты.
        GameClient.shared.gameMap.disableAllRegions();
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        if (pkg instanceof Packages.BattleResult) {
            onBattleResult((Packages.BattleResult) pkg);
        }else if (pkg instanceof Packages.PlayerChangeUnits) {
            Packages.PlayerChangeUnits changeUnits = (Packages.PlayerChangeUnits) pkg;
            GameClient.instance().registerTask(()->
                    GameClient.shared.gameMap.getRegionByID(changeUnits.region).setUnits(changeUnits.units)
            );
        }else if (pkg instanceof Packages.PlayerMove) {
            Packages.PlayerMove msg = (Packages.PlayerMove) pkg;
            GameClient.instance().registerTask(()->{
                Player player = PlayerManager.instance().getPlayer(msg.player);

                MapPartObject regionFrom = GameClient.shared.gameMap.getRegionByID(msg.from);
                regionFrom.removeUnits(msg.units);

                MapPartObject regionTo = GameClient.shared.gameMap.getRegionByID(msg.to);
                regionTo.addUnits(msg.units);
                regionTo.setFraction(player.getFraction());
            });
        }else if (pkg instanceof Packages.GetBattleResult) {
            Packages.GetBattleResult getBattleResul = (Packages.GetBattleResult) pkg;
            if (GameClient.shared.battleDeck.isBattleMember(PlayerManager.getSelf().getFraction())) {
                GameClient.instance().send(new Packages.PlayerDamage(
                        GameClient.shared.battleDeck.attackersPower,
                        GameClient.shared.battleDeck.defendersPower
                ));
            }
        }
    }

    private void onBattleResult(Packages.BattleResult battleResult) {
        if (battleResult.looserID == PlayerManager.getSelf().id){
            //Если ты проигравший
            MapPartObject playerRegion = GameClient.shared.battleDeck.getPlayerRegion(PlayerManager.getSelf());
            if (battleResult.killUnits>0) {
                Unit[] unitsToKill = null;
                while(unitsToKill == null) {
                    showKillUnitsDialog(playerRegion.getUnits(), battleResult.killUnits);
                }
                playerRegion.removeUnits(unitsToKill);
                GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
            }
            if (GameClient.shared.battleDeck.isDefender(PlayerManager.getSelf().getFraction())){
                //Если ты оборонялся, ты можешь выбрать куда отсткупить
                MapPartObject region = GameClient.shared.battleDeck.getDefender();
                region.getRegionsToRetreat().forEach(r->r.setEnabled(true));
            }else{
                GameClient.instance().send(new Packages.LooserReady());
            }
        }
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        super.click(event);
        if (event.getTarget() instanceof MapPartObject) {
            MapPartObject playerRegion = GameClient.shared.battleDeck.getPlayerRegion(PlayerManager.getSelf());
            MapPartObject moveRegion = (MapPartObject) event.getTarget();
            //todo: передвинуть войска и установить им флаг усталости.
            //todo: проверить снабжение
            if (!Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(),
                    playerRegion, moveRegion, playerRegion.getUnitsCount())){
                //Надо коого то убить.
                //Считаем сколько юнитов должны умереть
                int countUnitsToKill = playerRegion.getUnitsCount() - (4-moveRegion.getUnitsCount());
                countUnitsToKill = countUnitsToKill < 0 ? 0 : countUnitsToKill;

                int i=playerRegion.getUnitsCount()-countUnitsToKill;
                for (;i>0; i--){
                    if(Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(),
                        playerRegion, moveRegion, i)) return;
                }
                countUnitsToKill = playerRegion.getUnitsCount() - i;

                if (countUnitsToKill == playerRegion.getUnitsCount()){
                    //todo: show message: Сюда отступить нельзя. Убить всех юнитов?
                    return;
                }
                Unit[] unitsToKill = showKillUnitsDialog(playerRegion.getUnits(), countUnitsToKill);
                if (unitsToKill == null){//Если пользователь нажал отмена
                    return;
                }
                playerRegion.removeUnits(unitsToKill);
                GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
                GameClient.instance().send(new Packages.Move(playerRegion.getID(), moveRegion.getID(), playerRegion.getUnits()));
                GameClient.instance().send(new Packages.LooserReady());
            }
        }

    }

    private Unit[] showKillUnitsDialog(Unit[] units, int countToKill){
        SelectUnitsDialogState suds = new SelectUnitsDialogState(units, InputManager.instance().getMousePosWorld());
        (new ModalState(suds)).run();

        if (suds.isOk()) {
            return suds.getSelectedUnits();
        } else return null;
    }
}
