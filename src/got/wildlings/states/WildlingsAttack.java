package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.ParallelGameState;
import got.model.ChangeAction;
import got.model.Fraction;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.AuctionState;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;
import got.wildlings.Wildlings;

import java.util.Arrays;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class WildlingsAttack {
    public static class ClientState extends ParallelGameState{
        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.WildlingsData){
                Packages.WildlingsData msg = (Packages.WildlingsData) pkg;

                if (msg.victory){
                    if (PlayerManager.getSelf().getFraction() == msg.maxBet){
                        maxBet();
                    }else{
                        defaultBet();
                    }
                }else{
                    if (PlayerManager.getSelf().getFraction() == msg.minBet){
                        minBet();
                    }
                }
            }
        }

        protected void maxBet(){}
        protected void minBet(){}
        protected void defaultBet(){}
    }

    public static class ServerState extends ParallelState{
        private int[] bets;
        private Fraction[] results;

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            bets = (int[]) stm.getParam(AuctionState.AUCTION_BETS_PARAM);
            results = (Fraction[]) stm.getParam(AuctionState.AUCTION_RESULTS_PARAM);
            Fraction maxBet = results[0];
            Fraction minBet = results[results.length - 1];
            GameServer.getServer().sendToAllTCP(new Packages.WildlingsData(minBet, maxBet, Arrays.stream(bets).sum() >= Wildlings.instance().getLevel()));
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
        }
    }
}
