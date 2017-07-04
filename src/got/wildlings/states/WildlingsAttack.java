package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.animations.Animator;
import got.gameObjects.ImageObject;
import got.gameStates.ParallelGameState;
import got.gameStates.StateID;
import got.graphics.DrawSpace;
import got.model.Fraction;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.AuctionState;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;
import got.utils.Timers;
import got.wildlings.Wildlings;
import got.wildlings.WildlingsCard;
import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class WildlingsAttack {
    public static class ClientState extends ParallelGameState{
        ImageObject cardImage;
        @Override
        public void recieve(Connection connection, Object pkg) {
            if (pkg instanceof Packages.WildlingsData) {
                stm.saveParam(StateMachine.WILDLINGS_DATA_PARAM, pkg);
                Packages.WildlingsData msg = (Packages.WildlingsData) pkg;
                WildlingsCard card = Wildlings.instance().getCard(msg.card);

                GameClient.instance().registerTask(()->{
                    addObject(cardImage = new ImageObject(card.getTexture(), 100, 200).setSpace(DrawSpace.SCREEN).setPos(Constants.SCREEN_WIDTH, 50));
                    Animator.animateVector2f(cardImage.getAbsolutePos(), new Vector2f(Constants.SCREEN_WIDTH/2-50, 50), 1000, cardImage::setPos);
                });

                Timers.getTimer(1000, ()->{
                    GameClient.instance().registerTask(()->{
                        card.onOpenClient(msg);
                        Timers.getTimer(1000, ()->{
                            Animator.animateVector2f(cardImage.getAbsolutePos(), new Vector2f(-100, 50), 1000, cardImage::setPos);
                            Timers.getTimer(1000, ()->GameClient.instance().sendReady(true)).start(true);
                        }).start(true);
                    });
                }).start(true);
            }
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_ATTACK;
        }

        @Override
        public void exit() {
            Wildlings.instance().resetLevel();
            super.exit();
        }
    }

    public static class ServerState extends ParallelState{
        private Packages.WildlingsData data;
        private int[] bets;
        private Fraction[] results;
        private WildlingsCard card;
        private int level;

        public ServerState(WildlingsCard card, int level) {
            this.card = card;
            this.level = level;
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_ATTACK;
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            bets = (int[]) stm.getParam(AuctionState.AUCTION_BETS_PARAM);
            results = (Fraction[]) stm.getParam(AuctionState.AUCTION_RESULTS_PARAM);
            Fraction maxBet = results[0];
            Fraction minBet = results[results.length - 1];

            if (Arrays.stream(bets).sum() >= level) {
                data = new Packages.WildlingsData(card.getID(), maxBet, true, bets[0]);
            }else{
                data = new Packages.WildlingsData(card.getID(), minBet, false, bets[0]);
            }
            GameServer.getServer().sendToAllTCP(data);
            stm.saveParam(StateMachine.WILDLINGS_DATA_PARAM, data);
        }

        @Override
        protected void onReadyToChangeState() {
            card.onOpenServer(stm, data);
        }
    }
}