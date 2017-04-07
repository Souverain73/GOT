package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.animations.Animator;
import got.gameObjects.ImageObject;
import got.graphics.DrawSpace;
import got.network.Packages;
import got.utils.Timers;
import got.vesterosCards.VesterosCard;
import got.vesterosCards.VesterosCards;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 31.03.2017.
 */
public class PlayVesterosCard extends AbstractGameState {
    ImageObject cardImage;
    @Override
    public String getName() {
        return "PlayVesterosCardPhase";
    }

    @Override
    public void exit() {

        super.exit();
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.OpenCard){
            Packages.OpenCard msg = (Packages.OpenCard)pkg;
            VesterosCard card = VesterosCards.getCard(msg.card);
            GameClient.instance().registerTask(()->{
                addObject(cardImage = new ImageObject(card.getTexture(), new Vector2f(Constants.SCREEN_WIDTH, 50), 200, 100).setSpace(DrawSpace.SCREEN));
                Animator.animateVector2f(cardImage.getPos(), new Vector2f(Constants.SCREEN_WIDTH/2-100, 50), 1000, cardImage::setPos);
            });
            Timers.getTimer(1000, ()->{
                card.onOpenClient();
                Timers.getTimer(1000, ()->{
                    Animator.animateVector2f(cardImage.getPos(), new Vector2f(-200, 50), 1000, cardImage::setPos);
                    Timers.getTimer(1000, ()->GameClient.instance().sendReady(true)).start(true);
                }).start(true);
            }).start(true);

        }

    }
}
