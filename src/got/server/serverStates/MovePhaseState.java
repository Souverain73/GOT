package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.GameState;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;

/**
 * Created by Souverain73 on 22.11.2016.
 */
public class MovePhaseState extends StepByStepState {

    public MovePhaseState(){
        super.setNextState(PowerPhaseState.class);
    }

    @Override
    public String getName() {
        return "MovePhaseState";
    }

    @Override
    public int getID() {
        return StateID.MOVE_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
    }

    @Override
    public void recieve(Connection c, Object pkg) {
        super.recieve(c, pkg);
        GameServer.PlayerConnection connection = (GameServer.PlayerConnection) c;
        Player player = connection.player;

        if (pkg instanceof Packages.Move){
            Packages.Move msg = (Packages.Move) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerMove(player.id, msg.from, msg.to, msg.units));
        }

        if (pkg instanceof Packages.Attack) {
            Packages.Attack attack = (Packages.Attack) pkg;
            GameServer.shared.attackerRegionID = attack.from;
            GameServer.shared.defenderRegionID = attack.to;
            GameServer.shared.attackerID = attack.attackerId;
            GameServer.shared.defenderID = attack.defenderId;
            stm.setState(new ChangeState(new HelpPhaseState(), true));
        }
    }
}
