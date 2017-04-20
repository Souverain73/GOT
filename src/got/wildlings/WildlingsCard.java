package got.wildlings;

import got.graphics.Texture;
import got.model.NetworkSide;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public interface WildlingsCard {
    void onWin(NetworkSide side);
    void onLoose(NetworkSide side);
    String getTitle();
    String getInternalName();
    int getID();
    Texture getTexture();
}
