package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ParallelState;

/**
 * Created by Souverain73 on 06.04.2017.
 */
public class CollectUnitsState extends ParallelState {
    @Override
    public int getID() {
        return StateID.COLLECT_UNITS;
    }

    @Override
    protected void onReadyToChangeState() {
        stm.changeState(null, StateMachine.ChangeAction.REMOVE);
    }

    @Override
    public void recieve(Connection c, Object pkg) {
        super.recieve(c, pkg);
        GameServer.PlayerConnection connection = (GameServer.PlayerConnection)c;
        Player player = connection.player;

        if (pkg instanceof Packages.ChangeUnits){
            Packages.ChangeUnits msg = ((Packages.ChangeUnits)pkg);
            GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(
                    player.id, msg.region, msg.units
            ));
        }
    }
}
