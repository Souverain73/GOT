package got.gameObjects;

import got.graphics.DrawSpace;
import got.graphics.Text;
import got.model.Player;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 24.11.2016.
 */
public class PlayerPanelObject extends AbstractGameObject<PlayerPanelObject>{
    @Override protected PlayerPanelObject getThis() {return this;}

    TextObject fraction;
    TextObject money;

    public PlayerPanelObject(Player player ){
        ImageObject bg = new ImageObject("PlayerPanel.png", new Vector2f(0,0), 100, 60)
                .setSpace(DrawSpace.SCREEN);
        addChild(bg);

        fraction = new TextObject(player.getFraction().toString())
                .setSpace(DrawSpace.SCREEN)
                .setPos(new Vector2f(10,10));
        addChild(fraction);

        money = new TextObject(String.valueOf(player.getMoney()))
                .setSpace(DrawSpace.SCREEN)
                .setPos(new Vector2f(10, 30));
        addChild(money);
    }
}
