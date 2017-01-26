package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
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

            GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
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
                player.getDeck().useCard(card);
                GameClient.shared.battleDeck.placeCard(card, player);
                logAction("Игрок " + player.getNickname() + " выбрал карту дома " + card.getTitle());
            });
        }
        if (pkg instanceof Packages.PlayerKillUnit) {
            Packages.PlayerKillUnit msg = (Packages.PlayerKillUnit) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.player);
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            logAction("Игрок " + player.getNickname() + " убивает юнита " + msg.unit + " в регионе " + region.getName());
            region.removeUnit(msg.unit);
        }
        if (pkg instanceof Packages.PlayerSetAction) {
            Packages.PlayerSetAction msg = (Packages.PlayerSetAction) pkg;
            GameClient.instance().registerTask(()->{
                MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
                if (msg.action == null) {
                    logAction("Игрок убирает приказ с региона " + region.getName());
                    region.setAction(null);
                }else{
                    logAction("Игрок устанавливает приказ в регионе " + region.getName());
                    region.setAction(msg.action);
                }
            });
        }
    }

    private HouseCard showSelectHouseCardDialog() {
        CustomModalState<HouseCard> cms = Dialogs.createSelectHouseCardDialog();

        (new ModalState(cms)).run();

        return cms.getResult();
    }
}
