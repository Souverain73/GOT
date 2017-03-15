package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.MapPartObject;
import got.network.Packages;
import got.utils.UI;

/**
 * Created by Souverain73 on 10.03.2017.
 */
public class GameConfigState extends AbstractGameState {
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.forEachRegion(region->{
            region.setAction(null);
        });
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);

        if (pkg instanceof Packages.ChangeUnits) {
            Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            region.setUnits(msg.units);
            UI.systemMessage("Server change units in region " + region.getName());
        }

        if (pkg instanceof Packages.ChangeRegionFraction) {
            Packages.ChangeRegionFraction msg = (Packages.ChangeRegionFraction) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            region.setFraction(msg.fraction);
            UI.systemMessage("Server change fraction in region " + region.getName());
        }
    }

    @Override
    public int getID() {
        return StateID.GAME_CONFIG_STATE;
    }
}