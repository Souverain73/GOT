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

    private final String playerName;
    private final Fraction playerFraction;
    private final int battlePower;
    private final boolean detailed;

    private final ContainerObject detailedView;
    private final ContainerObject minView;


    public PlayerCardObject(String playerName, Fraction playerFraction, int battlePower, boolean detailed){
        this.playerName = playerName;
        this.playerFraction = playerFraction;
        this.battlePower = battlePower;
        this.detailed = detailed;

        detailedView = createDetailedView();
        minView = createMinView();
    }

    private ContainerObject createMinView() {
        //TODO: implement simplified player card
        return createDetailedView();
    }

    private ContainerObject createDetailedView() {
        ContainerObject result = new ContainerObject();
        ///BG
        result.addChild(new ImageObject("battlePlayerPanelDetailed.png", new Vector2f(0, 0), 100, 50)
                .setSpace(DrawSpace.SCREEN));
        //Fraction icon
        result.addChild(new ImageObject(playerFraction.getBackTexture(), new Vector2f(10,10), 30, 30)
                .setSpace(DrawSpace.SCREEN));
        //PlayerName
        result.addChild(new TextObject(playerName)
                .setPos(new Vector2f(50, 10))
                .setSpace(DrawSpace.SCREEN));
        //Battle power
        result.addChild(new TextObject(String.valueOf(battlePower))
                .setPos(new Vector2f(80, 10))
                .setSpace(DrawSpace.SCREEN));

        return result;
    }
}
