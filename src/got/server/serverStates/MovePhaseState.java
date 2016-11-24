package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;

/**
 * Created by Souverain73 on 22.11.2016.
 */
public class MovePhaseState implements ServerState {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getID() {
        return StateID.MOVE_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        System.out.println("Entering move phase");
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection c, Object pkg) {
        GameServer.PlayerConnection connection = (GameServer.PlayerConnection) c;
        Player player = connection.player;

        if (pkg instanceof Packages.Move){
            Packages.Move msg = (Packages.Move) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerMove(player.id, msg.from, msg.to, msg.units));
        }
    }
}
