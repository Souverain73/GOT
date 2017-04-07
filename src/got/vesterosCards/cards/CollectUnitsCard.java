package got.vesterosCards.cards;

import got.server.serverStates.CollectUnitsState;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;

/**
 * Created by Souverain73 on 06.04.2017.
 */
public class CollectUnitsCard extends CommonVesterosCard {
    public CollectUnitsCard(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        stm.changeState(new CollectUnitsState(), StateMachine.ChangeAction.SET);
    }
}
