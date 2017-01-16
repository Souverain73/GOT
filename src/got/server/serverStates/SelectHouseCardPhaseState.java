package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.GameState;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 29.11.2016.
 */
public class SelectHouseCardPhaseState implements ServerState{
    private StateMachine stm;
    private boolean attackerReady = false;
    private boolean defenderReady = false;
    public SelectHouseCardPhaseState(){

    }

    @Override
    public String getName() {
        return "Select house card state";
    }

    @Override
    public int getID() {
        return StateID.SELECT_HOUSE_CARD_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
       this.stm = stm;

    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
        Player player = c.player;
        if (pkg instanceof Packages.SelectHouseCard) {
            Packages.SelectHouseCard msg = (Packages.SelectHouseCard) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerSelectHouseCard(player.id, msg.card));
        }
        if (pkg instanceof Packages.Ready) {
            Packages.Ready ready = (Packages.Ready) pkg;
            if (player.id == GameServer.shared.attackerID) attackerReady = true;
            if (player.id == GameServer.shared.defenderID) defenderReady = true;
            if (defenderReady && attackerReady){
                stm.changeState(new BattleResultState(), StateMachine.ChangeAction.SET);
            }
        }
    }
}
