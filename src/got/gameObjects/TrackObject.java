package got.gameObjects;

import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
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
        this.w = 500;
        this.h = 100;
        this.track = track;
        setSpace(DrawSpace.SCREEN);

        ImageObject bg = new ImageObject("thronebg.png", new Vector2f(0,0), (int)w, (int)h);
        addChild(bg);

        AbstractGameObject<?> throneIcon = new TextObject(track.getName()).setPos(new Vector2f(10, 30));
        addChild(throneIcon);


//            slots[i] = new ImageObject(blankTexture, new Vector2f(100 + i * 70, 25), 50, 50);

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
        fraction.getBackTexture().draw(cp.x + 100 +position * 70, cp.y + 25, 50, 50);
    }
}
