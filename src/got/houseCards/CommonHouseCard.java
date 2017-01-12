package got.houseCards;

import got.GameClient;
import got.graphics.Texture;
import got.model.Fraction;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import got.utils.LoaderParams;

/**
 * Created by Souverain73 on 09.01.2017.
 */
public class CommonHouseCard implements HouseCard {
    private int power;
    private int swords;
    private int towers;
    private Texture texture;
    private String title;
    protected Fraction placerFraction;

    @Override
    public void init(LoaderParams params) {
        this.power = (Integer)params.get("power");
        this.swords = (Integer)params.get("swords");
        this.towers = (Integer)params.get("towers");
        this.texture = (Texture)params.get("texture");
        this.title = (String)params.get("title");
    }

    @Override   public void onPlace(Fraction fraction) {this.placerFraction = fraction;}
    @Override   public void onWin() {   }
    @Override   public void onLoose() {   }
    @Override   public void onBattleEnd() {   }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public int getSwords() {
        return swords;
    }

    @Override
    public int getTowers() {
        return towers;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public int getID() {
        return title.hashCode();
    }

    protected void send(Packages.ClientServerPackage pkg){
        if (placerFraction == PlayerManager.getSelf().getFraction()){
            GameClient.instance().send(pkg);
        }
    }

    protected Player owner(){
        return PlayerManager.instance().getPlayerByFraction(placerFraction);
    }
}
