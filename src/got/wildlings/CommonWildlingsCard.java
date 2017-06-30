package got.wildlings;

import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.ChangeAction;
import got.model.NetworkSide;
import got.server.serverStates.StateMachine;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class CommonWildlingsCard implements WildlingsCard {
    private static String TEXTURE_BASE = "Wildlings cards/";
    private Texture texture = null;
    private String textureName;
    private String internalName;
    private String title;


    public CommonWildlingsCard(String textureName, String internalName, String title) {
        this.textureName = textureName;
        this.internalName = internalName;
        this.title = title;
    }

    @Override
    public void onOpenClient() {

    }

    @Override
    public void onOpenServer(StateMachine stm) {
        stm.changeState(null, ChangeAction.REMOVE);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public int getID() {
        return internalName.hashCode();
    }

    @Override
    public Texture getTexture() {
        if (texture == null){
            texture = TextureManager.instance().loadTexture(TEXTURE_BASE + textureName);
        }
        return texture;
    }
}
