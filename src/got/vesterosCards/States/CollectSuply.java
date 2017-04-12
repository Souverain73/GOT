package got.vesterosCards.States;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.MapPartObject;
import got.gameStates.StepByStepGameState;
import got.model.Fraction;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.StepByStepState;
import got.utils.UI;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class CollectSuply {

    public static class ClientState extends StepByStepGameState {
        @Override
        protected void onSelfTurn() {
            super.onSelfTurn();
            int suply = GameClient.shared.gameMap.getRegions().stream().filter(r->
                    r.getFraction() == PlayerManager.getSelf().getFraction()
            ).mapToInt(MapPartObject::getResourcesCount).sum();
            GameClient.instance().send(new Packages.ChangeSuply(suply));
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerChangeSuply) {
                Packages.PlayerChangeSuply msg = (Packages.PlayerChangeSuply) pkg;

                Game.instance().getSuplyTrack().setPos(msg.fraction, msg.level);
                if (msg.fraction == PlayerManager.getSelf().getFraction()){
                    checkArmyLimit();
                    endTurn(false);
                }
            }
        }

        public void checkArmyLimit(){
            Fraction playerFraction = PlayerManager.getSelf().getFraction();
            if (!Game.instance().getSuplyTrack().canHaveArmies(playerFraction,
                    GameClient.shared.gameMap.getArmySizesForFraction(playerFraction))){
                UI.systemMessage("Need kill units");
            }
        }
    }


    public static class ServerState extends StepByStepState {
        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
            Player player = c.player;
            if (pkg instanceof Packages.ChangeSuply) {
                Packages.ChangeSuply msg = (Packages.ChangeSuply) pkg;
                Game.instance().getSuplyTrack().setPos(player.getFraction(), msg.level);
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeSuply(player.getFraction(), msg.level));
            }
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, StateMachine.ChangeAction.REMOVE);
        }
    }

}
