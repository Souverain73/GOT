package got.vesterosCards.cards;

import got.GameClient;
import got.gameObjects.MapPartObject;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;

/**
 * Created by Souverain73 on 11.04.2017.
 */
public class GameOfThrones extends CommonVesterosCard {
    public GameOfThrones(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenClient() {
        int money = GameClient.shared.gameMap.getRegions().stream().filter(r->
            r.getFraction() == PlayerManager.getSelf().getFraction()
        ).mapToInt(MapPartObject::getInfluencePoints).sum();

        GameClient.instance().logMessage("common.collectInfluence", money);
        PlayerManager.getSelf().addMoney(money);
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        super.onOpenServer(stm, param);
    }
}
