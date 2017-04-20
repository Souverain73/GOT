package got.gameStates;

import got.Constants;
import got.GameClient;
import got.gameObjects.gui.NumberSelectorObject;
import got.network.Packages;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class AuctionGameState extends ParallelGameState{
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        addObject(new NumberSelectorObject(0, PlayerManager.getSelf().getMoney(), 0){
            @Override
            protected void onSelect() {
                super.onSelect();
                setEnabled(false);
                GameClient.instance().send(new Packages.Bet(getValue()));
            }
        }.setPos((Constants.SCREEN_WIDTH-100)/2, 300));
    }
}
