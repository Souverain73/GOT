package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.SelectUnitsDialogState;
import got.model.*;
import got.network.Packages;
import got.server.PlayerManager;

import java.util.List;
import java.util.stream.Collectors;

import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 15.12.2016.
 */
public class BattleResultState extends ActionPhase{
    @Override
    public String getName() {
        return "Battle result state";
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
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
                moveAllUnits(msg.from, msg.to);
            });
        }else if (pkg instanceof Packages.GetBattleResult) {
            if (GameClient.shared.battleDeck.isBattleMember(PlayerManager.getSelf().getFraction())) {
                GameClient.instance().send(new Packages.PlayerDamage(
                        GameClient.shared.battleDeck.attackersPower,
                        GameClient.shared.battleDeck.defendersPower
                ));
            }
        }else if (pkg instanceof Packages.PlayerKillAllUnitsAtRegion) {
            Packages.PlayerKillAllUnitsAtRegion msg = (Packages.PlayerKillAllUnitsAtRegion) pkg;
            GameClient.shared.gameMap.getRegionByID(msg.regionID).removeAllUnits();
        }else if (pkg instanceof Packages.MoveAttackerToAttackRegion) {
            GameClient.instance().registerTask(()->
                moveAllUnits(GameClient.shared.battleDeck.getAttackerRegion(), GameClient.shared.battleDeck.getDefenderRegion())
            );
        }
    }

    private void moveAllUnits(int regionFromId, int regionToId) {
        MapPartObject regionFrom = GameClient.shared.gameMap.getRegionByID(regionFromId);
        MapPartObject regionTo = GameClient.shared.gameMap.getRegionByID(regionToId);

        moveAllUnits(regionFrom, regionTo);
    }

    private void moveAllUnits(MapPartObject regionFrom, MapPartObject regionTo){
        Unit[] units = regionFrom.getUnits();

        regionFrom.removeUnits(units);
        regionTo.addUnits(units);
        regionTo.setFraction(regionFrom.getFraction());
        if (!regionFrom.havePowerToket()){
            regionFrom.setFraction(Fraction.NONE);
        }
        logAction("Игрок перемещает юнитов из " + regionFrom.getName() + " в " + regionTo.getName());
    }

    private void onBattleResult(Packages.BattleResult battleResult) {
        Player winner = PlayerManager.instance().getPlayer(battleResult.winnerID);
        Player looser = PlayerManager.instance().getPlayer(battleResult.looserID);
        logAction("Битва закончена игрок " + winner.getNickname() + " победил, игрок " + looser.getNickname() + " проиграл.");
        //Убираем приказы
        GameClient.shared.battleDeck.getAttackerRegion().setAction(null);
        if (GameClient.shared.battleDeck.isAttacker(PlayerManager.instance().getPlayer(battleResult.winnerID).getFraction())){
            GameClient.shared.battleDeck.getDefenderRegion().setAction(null);
        }
        GameClient.shared.gameMap.getRegionByID(battleResult.looserRegionID).killUnits();
        if (battleResult.looserID == PlayerManager.getSelf().id){
            //Если ты проигравший
            MapPartObject playerRegion = GameClient.shared.battleDeck.getPlayerRegion(PlayerManager.getSelf());
            if (battleResult.killUnits>0) {
                Unit[] unitsToKill = null;
                while(unitsToKill == null) {
                    unitsToKill = showKillUnitsDialog(playerRegion.getUnits(), battleResult.killUnits);
                }
                playerRegion.removeUnits(unitsToKill);
                GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
            }
            if (GameClient.shared.battleDeck.isDefender(PlayerManager.getSelf().getFraction())){
                //Если ты оборонялся, ты можешь выбрать куда отступить
                enableRegionsToRetreatOrKillUnits();
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

            //Активным может быть регион не проходящий по снабжению, поэтому проверяем снабжение
            if (!Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(), playerRegion, moveRegion, playerRegion.getUnitsCount())){
                //Считаем сколько юнитов надо убить что бы совершить этот ход
                int i = 0;
                while(i<4 && Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(), playerRegion, moveRegion, i+1)){
                    i++;
                }
                if (i==0){
                    throw new IllegalStateException("Активен регион в который пользователь не может совершить ход");
                }
                int unitsToKillCount = playerRegion.getUnitsCount() - i;
                Unit[] unitsToKill = showKillUnitsDialog(playerRegion.getUnits(), unitsToKillCount);
                if (unitsToKill != null) {
                    playerRegion.removeUnits(unitsToKill);
                    GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
                    GameClient.instance().send(new Packages.Move(playerRegion.getID(), moveRegion.getID(), playerRegion.getUnits()));
                    GameClient.instance().send(new Packages.LooserReady());
                }
                //TODO: сказать, что так нельзя. Надо либо выбрать другой регион, либо убивать юнитов.
            }else{
                GameClient.instance().send(new Packages.Move(playerRegion.getID(), moveRegion.getID(), playerRegion.getUnits()));
                GameClient.instance().send(new Packages.LooserReady());
            }
        }

    }

    private void enableRegionsToRetreatOrKillUnits() {
        GameClient.shared.gameMap.disableAllRegions();
        MapPartObject regionFrom = GameClient.shared.battleDeck.getDefenderRegion();
        List<MapPartObject> regionsToMove = regionFrom.getRegionsToMove();
        //Ищем регионы куда можно отступить не нарушая снабжения
        List<MapPartObject> regionsToRetreat =
                regionsToMove.stream().filter(r->{
                    //нельзя отступить в чужой регион
                    if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE) return false;

                    return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, regionFrom.getUnitsCount());
                }).collect(Collectors.toList());

        if (regionsToRetreat.size() > 0){
            regionsToRetreat.forEach(r->r.setEnabled(true));
            GameClient.instance().setTooltipText("Выберите регион для отступления");
            return;
        }

        //Если нет регионов в которые можно пойти без убийства юнитов, ищем регион в который можно пойти хотя бы 1-м юнитом
        regionsToRetreat =
                regionsToMove.stream().filter(r->{
                    //нельзя отступить в чужой регион
                    if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE) return false;

                    return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, 1);
                }).collect(Collectors.toList());

        if (regionsToRetreat.size() > 0){
            regionsToRetreat.forEach(r->r.setEnabled(true));
            GameClient.instance().setTooltipText("Выберите регион для отступления");
            return;
        }

        //Если нет регионов куда вообще можно пойти, убиваем всех юнитов.
        logAction("Некуда отступать, все юниты умирают");
        GameClient.instance().send(new Packages.KillAllUnitsAtRegion(regionFrom.getID()));
        //Сообщаем, что проигравций закончил отступление.
        GameClient.instance().send(new Packages.LooserReady());
    }

    private Unit[] showKillUnitsDialog(Unit[] units, int countToKill){
        SelectUnitsDialogState suds = new SelectUnitsDialogState(units, InputManager.instance().getMousePosWorld());
        (new ModalState(suds)).run();

        if (suds.isOk()) {
            return suds.getSelectedUnits();
        } else return null;
    }

    @Override
    public void exit() {
        GameClient.shared.battleDeck.finish();
        GameClient.shared.battleDeck = null;
    }
}
