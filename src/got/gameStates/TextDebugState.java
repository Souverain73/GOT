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

        addObject(new TextObject(new Font("bolyar",  64), "bolyar  64: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 100)));
        addObject(new TextObject(new Font("trojan",  64), "trojan  64: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 170)));
        addObject(new TextObject(new Font("antiqua", 64), "antiqua 64: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 240)));
        addObject(new TextObject(new Font("test",    64), "test    64: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 310)));

        addObject(new TextObject(new Font("bolyar",  32), "bolyar  32: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 380)));
        addObject(new TextObject(new Font("trojan",  32), "trojan  32: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 420)));
        addObject(new TextObject(new Font("antiqua", 32), "antiqua 32: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 460)));
        addObject(new TextObject(new Font("test",    32), "test    32: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 500)));

        addObject(new TextObject(new Font("bolyar",  16), "bolyar  16: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 540)));
        addObject(new TextObject(new Font("trojan",  16), "trojan  16: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 560)));
        addObject(new TextObject(new Font("antiqua", 16), "antiqua 16: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 580)));
        addObject(new TextObject(new Font("test",    16), "test    16: Hello world").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 600)));

    }

    @Override
    public void click(InputManager.ClickEvent event) {

    }
}
