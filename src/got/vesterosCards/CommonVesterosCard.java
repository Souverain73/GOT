package got.vesterosCards;

import got.graphics.Texture;
import got.graphics.TextureManager;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class CommonVesterosCard implements VesterosCard {
    Texture texture = null;
    String textureName;
    String title;

    public CommonVesterosCard(Texture texture, String textureName, String title) {
        this.textureName = textureName;
        this.title = title;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getID() {
        return title.hashCode();
    }

    @Override
    public Texture getTexture() {
        if (texture == null){
            texture = TextureManager.instance().loadTexture(textureName);
        }
        return texture;
    }
}
