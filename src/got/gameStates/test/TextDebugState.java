package got.gameStates.test;

import got.Constants;
import got.InputManager;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.text.FontBitmap;
import got.graphics.TextureManager;
import got.graphics.text.FontTrueType;
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
               Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT).setSpace(DrawSpace.SCREEN));
        addObject(new TextObject(new FontTrueType("trajan", 16), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 30)));
        addObject(new TextObject(new FontTrueType("trajan", 10), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 50)));
        addObject(new TextObject(new FontTrueType("trajan", 4), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 60)));

        addObject(new TextObject(new FontTrueType("calibri", 16), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 100)));
        addObject(new TextObject(new FontTrueType("calibri", 10), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 120)));
        addObject(new TextObject(new FontTrueType("calibri", 4), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 130)));

        addObject(new TextObject(new FontTrueType("BKANT", 16), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 200)));
        addObject(new TextObject(new FontTrueType("BKANT", 10), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 220)));
        addObject(new TextObject(new FontTrueType("BKANT", 4), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 230)));

    }

    @Override
    public void click(InputManager.ClickEvent event) {

    }
}
