package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.utils.UI;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * Created by Souverain73 on 05.12.2016.
 */
public class BattleResultState implements ServerState{
    private enum Winner { ATTACKER, DEFENDER}

    private String name = "BattleRresult state";
    private boolean defenderSendResult = false;
    private boolean attackerSendResult = false;
    private Packages.PlayerDamage defenderResult;
    private Packages.PlayerDamage attackerResult;
    private Winner winner = null;
    private StateMachine stm;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getID() {
        return StateID.BATTLE_RESULT_PHASE;
    }

    @Override
    public void enter(StateMachine stm){
        this.stm = stm;
        GameServer.getServer().sendToAllTCP(new Packages.GetBattleResult());
    }

    @Override
    public void exit() {
        
    }

    @Override
    public void recieve(Connection c, Object pkg) {
        GameServer.PlayerConnection connection = (GameServer.PlayerConnection) c;
        Player player = connection.player;

        if (pkg instanceof Packages.PlayerDamage){
            Packages.PlayerDamage msg = (Packages.PlayerDamage) pkg;
            if (player.id == GameServer.shared.attackerID){
                attackerSendResult = true;
                attackerResult = msg;
                debug("attacker send damage");
            }else if(player.id == GameServer.shared.defenderID){
                defenderSendResult = true;
                defenderResult = msg;
                debug("defender send damage");
            }
            if (defenderSendResult == attackerSendResult == true){
                debug("Send battle result");
                if (!defenderResult.equals(attackerResult)){
                    UI.systemMessage("Bad battle result data. Continue with defender's data");
                }
                Packages.BattleResult battleResult;
                //TODO: get home cards effects, calculate units killed and send it to players
                if (defenderResult.defenderDamage >= defenderResult.attackerDamage){
                    battleResult = new Packages.BattleResult(GameServer.shared.defenderID, GameServer.shared.attackerID, 0);
                    winner = Winner.DEFENDER;
                }else{
                    battleResult = new Packages.BattleResult(GameServer.shared.attackerID, GameServer.shared.defenderID, 0);
                    winner = Winner.ATTACKER;
                }
                GameServer.getServer().sendToAllTCP(battleResult);
            }

        }else if (pkg instanceof Packages.ChangeUnits) {
            Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(player.id, msg.region, msg.units));

        }else if (pkg instanceof Packages.Move) {
            Packages.Move msg = (Packages.Move) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerMove(player.id, msg.from, msg.to, msg.units));

        }else if (pkg instanceof Packages.LooserReady) {
            if (winner == Winner.ATTACKER){
                GameServer.getServer().sendToAllTCP(new Packages.MoveAttackerToAttackRegion());
            }
            stm.setState(new ChangeState(new MovePhaseState(), true));

        }else if (pkg instanceof Packages.KillAllUnitsAtRegion) {
            Packages.KillAllUnitsAtRegion msg = (Packages.KillAllUnitsAtRegion) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerKillAllUnitsAtRegion(player.id, msg.regionID));
        }
    }
}
