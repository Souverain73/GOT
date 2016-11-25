package got.gameObjects.battleDeck;

import got.Constants;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.MapPartObject;
import got.graphics.DrawSpace;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 25.11.2016.
 */

public class BattleDeckObject extends AbstractGameObject<BattleDeckObject> {
    @Override protected BattleDeckObject getThis() {return this;}

    public BattleDeckObject(MapPartObject atacker, MapPartObject defender){
        ImageObject background = new ImageObject("BattleDeck.png", new Vector2f(0,0),
                Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)
                .setSpace(DrawSpace.SCREEN);
    }
}
