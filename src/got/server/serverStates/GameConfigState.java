package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;

/**
 * Created by Souverain73 on 09.03.2017.
 */
public class GameConfigState implements ServerState {
    private StateMachine stm;

    @Override
    public String getName() {
        return "GameConfig State";
    }

    @Override
    public int getID() {
        return StateID.GAME_CONFIG_STATE;
    }

    @Override
    public void enter(StateMachine stm) {
            this.stm = stm;
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }

    public void finish(){
        stm.changeState(new PlanningPhaseState(), StateMachine.ChangeAction.REMOVE);
    }
}
