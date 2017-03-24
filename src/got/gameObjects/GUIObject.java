package got.gameObjects;

import got.Constants;
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
    private ThroneTrackObject throneTrack;

    public GUIObject(){
        setSpace(DrawSpace.SCREEN);

        addChild(new PlayerPanelObject(PlayerManager.getSelf()));

        addChild(tooltipText = new TextObject("Tooltip").setPos(new Vector2f(300,10)));

        addChild(glo = new GameLogObject(300, 100, 32).setPos(new Vector2f(0, Constants.SCREEN_HEIGHT-100)));

        throneTrack = new ThroneTrackObject(Game.instance().getTrack(THRONE_TRACK));
        throneTrack.setPos(new Vector2f(300, Constants.SCREEN_HEIGHT-100));

        addChild(throneTrack);
    }

    public ThroneTrackObject getThroneTrack() {
        return throneTrack;
    }

    public void setTooltipText(String text){
        tooltipText.setText(text);
    }

    public void logMessage(String message){
        glo.addMessage(message);
    }
}
