package got.gameObjects.gui;

import got.Constants;
import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.model.Game;
import got.server.PlayerManager;
import org.joml.Vector2f;

import static got.model.Game.THRONE_TRACK;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class GUIObject extends AbstractGameObject<GUIObject> {
    @Override protected GUIObject getThis() {return this;}

    private TextObject tooltipText;
    private GameLogObject glo;
    private ThronesPanel tp;

    private ContainerObject upperRight;

    public GUIObject(){
        upperRight = new ContainerObject().setPos(new Vector2f(Constants.SCREEN_WIDTH, 0));

        addChild(new PlayerPanelObject(PlayerManager.getSelf()));

        addChild(tooltipText = new TextObject("Tooltip").setPos(new Vector2f(300,10)));

        addChild(glo = new GameLogObject(300, 100, 32).setPos(new Vector2f(0, Constants.SCREEN_HEIGHT-100)));

        upperRight.addChild(tp = new ThronesPanel().setPos(new Vector2f(-70, 0)));
        addChild(upperRight);

        setSpace(DrawSpace.SCREEN);
    }

    public ThroneTrackObject getThroneTrack() {
        return tp.getThroneTrack();
    }

    public void setTooltipText(String text){
        tooltipText.setText(text);
    }

    public void logMessage(String message){
        glo.addMessage(message);
    }
}
