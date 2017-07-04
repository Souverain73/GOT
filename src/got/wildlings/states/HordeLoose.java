package got.wildlings.states;

import got.gameStates.ParallelGameState;
import got.model.ChangeAction;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;

/**
 * Created by КизиловМЮ on 04.07.2017.
 */
public class HordeLoose {
    public static class ClientState extends ParallelGameState{

    }

    public static class ServerState extends ParallelState{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);

        }
    }
}
