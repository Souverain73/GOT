package got.vesterosCards.cards;

import got.GameClient;
import got.gameObjects.MapPartObject;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.States.CollectInfluence;

/**
 * Created by Souverain73 on 11.04.2017.
 */
public class GameOfThrones extends CommonVesterosCard {
    public GameOfThrones(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenClient() {

    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        stm.changeState(new CollectInfluence.ServerState(), StateMachine.ChangeAction.SET);
    }
}
