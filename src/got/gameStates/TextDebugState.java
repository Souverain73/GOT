package got.gameStates;

import got.Constants;
import got.InputManager;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.TextureManager;
import got.interfaces.IClickListener;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 21.03.2017.
 */
public class TextDebugState extends AbstractGameState implements IClickListener{
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        TextObject to;
        addObject(new ImageObject(TextureManager.instance().loadTexture("backgroundMain.png"),
                new Vector2f(0,0), Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT).setSpace(DrawSpace.SCREEN));

        addObject(to = new TextObject(new Font("test", 128), "128: Hello").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 100)));
        addObject(new TextObject(new Font("test", 64), "64: Hello").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 250)));
        addObject(new TextObject(new Font("test", 32), "32: Hello").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 320)));
        addObject(new TextObject(new Font("test", 16), "16: Hello").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 360)));
        addObject(new TextObject(new Font("test", 8), "8: Hello").setSpace(DrawSpace.SCREEN).setPos(new Vector2f( 100, 380)));
    }

    @Override
    public void click(InputManager.ClickEvent event) {

    }
}
