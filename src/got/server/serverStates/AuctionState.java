package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
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
    public int getID() {
        return StateID.AUCTION_STATE;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        playersCount = PlayerManager.instance().getPlayersCount();
        bets = new int[playersCount];
    }

    @Override
    protected void onReadyToChangeState() {
        GameServer.instance().getServer().sendToAllTCP(new Packages.BetsList(bets));
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

        if (pkg instanceof Packages.ResolvePosition) {
            Packages.ResolvePosition msg = (Packages.ResolvePosition) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerResolvePosition(player.id, msg.position, msg.fraction));
        }


    }
}
