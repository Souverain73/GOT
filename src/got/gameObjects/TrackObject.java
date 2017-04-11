package got.gameObjects;

import got.gameStates.GameState;
import got.graphics.*;
import got.model.Fraction;
import got.model.Track;
import org.joml.Vector2f;

import java.util.stream.IntStream;


/**
 * Created by Souverain73 on 22.03.2017.
 */
public class TrackObject extends AbstractGameObject<TrackObject>{
    @Override protected TrackObject getThis() { return this;}
    private Track track;
    private Texture blankTexture;

    public TrackObject(Track track){
        blankTexture = TextureManager.instance().loadTexture("fractions/back.png");
        this.w = 65;
        this.h = 480;
        this.track = track;
        setSpace(DrawSpace.SCREEN);

        ImageObject bg = new ImageObject("tracks/" + track.getName() + "_BG.png", new Vector2f(0,60), 65, 420);
        addChild(bg);

        AbstractGameObject<?> trackIcon = new ImageObject("tracks/" + track.getName() + "_ICON.png", new Vector2f(0,0), 65, 60);
        addChild(trackIcon);
    }


    @Override
    public void draw(GameState state) {
        if (!isVisible()) return;
        super.draw(state);
        GraphicModule.instance().setDrawSpace(this.space);
        Vector2f cp = getPos();
        Fraction[] data = track.getData();

        for (int i = 0; i < data.length; i++) {
            drawFraction(cp, i, data[i]);
        }
    }

    protected void drawFraction(Vector2f cp, int position, Fraction fraction){
        fraction.getBackTexture().draw(cp.x + 12 , cp.y + 82 + position * 70, 45, 45);
    }
}
