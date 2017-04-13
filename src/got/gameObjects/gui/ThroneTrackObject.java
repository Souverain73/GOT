package got.gameObjects.gui;

import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.model.Fraction;
import got.model.Track;
import got.server.PlayerManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class ThroneTrackObject extends TrackObject {
    public enum HighlightMode{
        HIGHLIGHT_CURRENT_PLAYER,
        HIGHLIGHT_READY,
        NO_HIGHLIGHT
    }

    private HighlightMode hm;

    public ThroneTrackObject(Track track) {
        super(track);
        hm = HighlightMode.NO_HIGHLIGHT;
    }

    public void setHighlightMode(HighlightMode hm) {
        if (hm == null) return;
        this.hm = hm;
    }

    @Override
    protected void drawFraction(Vector2f cp, int position, Fraction fraction) {
        if (hm == HighlightMode.HIGHLIGHT_READY){
            if (PlayerManager.instance().getPlayerByFraction(fraction).isReady()){
                GraphicModule.instance().setEffect(new Effect().Overlay(new Vector3f(0.0f, 0.3f, 0.0f)));
            }
        }else if(hm == HighlightMode.HIGHLIGHT_CURRENT_PLAYER){
            Fraction cf = PlayerManager.instance().getCurrentPlayer().getFraction();
            if (cf == fraction){
                GraphicModule.instance().setEffect(new Effect().Overlay(new Vector3f(0.0f, 0.3f, 0.0f)));
            }
        }
        super.drawFraction(cp, position, fraction);
        GraphicModule.instance().resetEffect();
    }
}
