package got.gameObjects;

import got.graphics.DrawSpace;
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

    private ImageObject[] slots = new ImageObject[6];

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

        IntStream.range(0, 6).forEach(i->{
            slots[i] = new ImageObject(blankTexture, new Vector2f(100 + i * 70, 25), 50, 50);
            addChild(slots[i]);
        });

        updateData();
    }

    public void updateData(){
        Fraction[] fractions = track.getData();
        if (fractions == null) return;

        for (int i = 0; i < fractions.length; i++) {
            Fraction fraction = fractions[i];
            slots[i].setTexture( fraction.getBackTexture());
        }
    }
}
