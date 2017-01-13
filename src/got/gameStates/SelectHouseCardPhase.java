package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.graphics.DrawSpace;
import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import org.joml.Vector2f;

import java.util.List;

import static got.utils.UI.logAction;


/**
 * Created by Souverain73 on 01.12.2016.
 */
public class SelectHouseCardPhase extends ActionPhase {
    @Override
    public int getID() {
        return StateID.SELECT_HOUSE_CARD_PHASE;
    }

    @Override
    public String getName() {
        return "Select house card phase";
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        if (GameClient.shared.battleDeck.isBattleMember(PlayerManager.getSelf().getFraction())){
            HouseCard selectedCard = showSelectHouseCardDialog();
            //todo: послать пакет, о том, что игрок сыграл карту.
            GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
            GameClient.instance().sendReady(true);
        }else{
            GameClient.instance().sendReady(true);
        }
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.PlayerSelectHouseCard) {
            GameClient.instance().registerTask(()-> {
                Packages.PlayerSelectHouseCard msg = (Packages.PlayerSelectHouseCard) pkg;
                Player player = PlayerManager.instance().getPlayer(msg.player);
                HouseCard card = HouseCardsLoader.instance().getCardById(msg.card);
                GameClient.shared.battleDeck.placeCard(card, player);
                logAction("Player " + player.getNickname() + "place house card " + card.getTitle());
            });
        }
    }

    private HouseCard showSelectHouseCardDialog() {
        CustomModalState<HouseCard> cms = new CustomModalState<>(null, false);
        Player player = PlayerManager.getSelf();
        List<HouseCard> cardsToSelect = player.getDeck().getActiveCards();
        ContainerObject cnt = new ContainerObject().setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0, 0));
        int cx = 290; int cy = 200;

        for (HouseCard card : cardsToSelect){
            ImageButton ib = new ImageButton(card.getTexture(), cx, cy, 100, 200, card).setSpace(DrawSpace.SCREEN);
            ib.setCallback((sender, param) ->{
                cms.setResult((HouseCard) param);
                cms.close();
            });
            cnt.addChild(ib);
            cx+=100;
        }

        cms.addObject(cnt);

        (new ModalState(cms)).run();

        return cms.getResult();
    }
}
