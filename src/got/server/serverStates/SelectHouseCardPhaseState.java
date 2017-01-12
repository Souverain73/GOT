package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.GameState;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;

/**
 * Created by Souverain73 on 29.11.2016.
 */
public class SelectHouseCardPhaseState implements ServerState{

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
//        stm.changeState(new BattleResultState(), StateMachine.ChangeAction.SET);
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
    }
}
