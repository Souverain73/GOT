package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.animations.Animator;
import got.gameObjects.ImageObject;
import got.graphics.DrawSpace;
import got.network.Packages;
import got.utils.Timers;
import got.utils.UI;
import got.vesterosCards.VesterosCard;
import got.vesterosCards.VesterosCards;
import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Created by Souverain73 on 31.03.2017.
 */
public class PlayVesterosCard extends ParallelGameState{
    ImageObject cardImage;
    @Override
    public String getName() {
        return "PlayVesterosCardPhase";
    }

    @Override
    public void exit() {
        UI.systemMessage("Exit play vesteros");
        super.exit();
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.OpenCard) {
            Packages.OpenCard msg = (Packages.OpenCard) pkg;
            VesterosCard card = VesterosCards.getCard(msg.card);
            GameClient.shared.gui.addSharedObject(VesterosPhase.CURRENT_CARD_SHARED_OBJECT, cardImage = new ImageObject(card.getTexture(), 285, 181).setSpace(DrawSpace.SCREEN).setPos(Constants.SCREEN_WIDTH / 2 - 125, -165));
            Animator.animateVector2f(cardImage.getAbsolutePos(), new Vector2f(Constants.SCREEN_WIDTH / 2 - 142, 55), 1000, cardImage::setPos);
            Timers.getTimer(1000, () ->
                GameClient.instance().registerTask(()->{
                    card.onOpenClient();
                    GameClient.instance().sendReady(true);
                })
            ).start(true);
        }
        if (pkg instanceof Packages.PlayerSelectItem) {
            Packages.PlayerSelectItem msg = (Packages.PlayerSelectItem) pkg;
            GameClient.instance().logMessage("vesteros.playerSelectItem", msg.select);
        }
        if (pkg instanceof Packages.SetRestrictedActions) {
            Packages.SetRestrictedActions msg = (Packages.SetRestrictedActions) pkg;
            GameClient.shared.restrictedActions = Arrays.asList(msg.actions);
            GameClient.instance().logMessage("common.setRestrictedActions");
            UI.systemMessage("Changed restrictedActions: " + Arrays.toString(msg.actions));
        }
    }
}
