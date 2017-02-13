package got.vesterosCards;

import got.graphics.Texture;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public interface VesterosCard {
    /**
     * Событие происходящее при открытии карты
     */
    void onOpen();
    String getTitle();
    int getID();
    Texture getTexture();
}
