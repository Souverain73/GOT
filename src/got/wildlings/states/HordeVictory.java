package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateID;
import got.gameStates.StateMachine;
import got.gameStates.modals.HireMenuState;
import got.interfaces.IClickListener;
import got.model.ChangeAction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;

/**
 * Created by КизиловМЮ on 04.07.2017.
 */
public class HordeVictory {
    public static class ClientState extends AbstractGameState implements IClickListener {
        Packages.WildlingsData data;

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            data = (Packages.WildlingsData) stm.getParam(StateMachine.WILDLINGS_DATA_PARAM);
            enableRegionsToHire();
        }

        private void enableRegionsToHire() {
            GameClient.shared.gameMap.enableByCondition(r ->
                    r.getFraction() == PlayerManager.getSelf().getFraction() && r.getBuildingLevel() > 0
            );
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            //todo: проверить снабжение
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                int pointsToHire = region.getBuildingLevel();
                HireMenuState hms = new HireMenuState(region.getUnitObjects(),
                        InputManager.instance().getMousePosWin(), pointsToHire,
                        region.getType() == MapPartObject.RegionType.SEA);
                region.hideUnits();
                (new ModalState(hms)).run();
                if (hms.isHired()){
                    GameClient.instance().send(new Packages.ChangeUnits(region.getID(), region.getUnits()));
                }
                region.showUnits();
            }
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerChangeUnits) {
                Packages.PlayerChangeUnits msg = (Packages.PlayerChangeUnits) pkg;
                MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
                region.setUnits(msg.units);
            }
        }
    }

    public static class ServerState implements got.server.serverStates.base.ServerState{
        got.server.serverStates.StateMachine stm;

        @Override
        public String getName() {
            return "HordeVicroty state";
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_HORDE_VICTORY;
        }

        @Override
        public void enter(got.server.serverStates.StateMachine stm) {
            this.stm = stm;
        }

        @Override
        public void exit() {

        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
            Player player = c.player;
            if (pkg instanceof Packages.ChangeUnits) {
                Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(player.id, msg.region, msg.units));
                stm.changeState(null, ChangeAction.REMOVE);
            }
        }
    }
}
