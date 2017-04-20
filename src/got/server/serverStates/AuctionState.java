package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.ParallelState;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class AuctionState extends ParallelState {
    private int playersCount;
    private int[] bets;

    @Override
    public void enter(StateMachine stm) {
        playersCount = PlayerManager.instance().getPlayersCount();
        bets = new int[playersCount];
    }

    @Override
    protected void onReadyToChangeState() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
        Player player = c.player;

        if (pkg instanceof Packages.Bet) {
            Packages.Bet msg = (Packages.Bet) pkg;
            bets[player.id] = msg.value;
            onReady(player, true);
        }
    }
}
