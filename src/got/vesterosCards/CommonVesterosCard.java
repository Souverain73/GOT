package got.vesterosCards;

import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.ChangeAction;
import got.server.serverStates.StateMachine;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class CommonVesterosCard implements VesterosCard {
    private static String TEXTURE_BASE = "Vesteros Cards/";
    private Texture texture = null;
    private String textureName;
    private String internalName;
    private String title;

    public CommonVesterosCard(String textureName, String internalName, String title) {
        this.textureName = textureName;
        this.internalName = internalName;
        this.title = title;
    }

    @Override
    public void onOpenClient() {
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        stm.changeState(null, ChangeAction.REMOVE);
    }

    public String getInternalName() {
        return internalName;
    }

    public String getTitle(){
        return title;
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
