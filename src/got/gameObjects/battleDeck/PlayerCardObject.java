package got.gameObjects.battleDeck;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ContainerObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.graphics.DrawSpace;
import got.model.Fraction;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public class PlayerCardObject extends AbstractGameObject<PlayerCardObject>{
    @Override protected PlayerCardObject getThis() {return this;}

    private final Fraction playerFraction;
    private final int battlePower;

    public PlayerCardObject(Fraction playerFraction, int battlePower){
        this.playerFraction = playerFraction;
        this.battlePower = battlePower;
        addChild(createView());
    }

    private ContainerObject createView() {
        ContainerObject result = new ContainerObject();
        ///BG
        result.addChild(new ImageObject("PlayerCardBG.png", new Vector2f(0, 0), 100, 200)
                .setSpace(DrawSpace.SCREEN));
        //Fraction icon
        result.addChild(new ImageObject(playerFraction.getBackTexture(), new Vector2f(10,10), 80, 80)
                .setSpace(DrawSpace.SCREEN));
        //Battle power
        result.addChild(new TextObject(String.valueOf(battlePower))
                .setPos(new Vector2f(10, 110))
                .setSpace(DrawSpace.SCREEN));

        return result;
    }

    public int getBattlePower() {
        return battlePower;
    }
}
