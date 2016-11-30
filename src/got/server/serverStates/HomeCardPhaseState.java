package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

/**
 * Created by Souverain73 on 29.11.2016.
 */
public class HomeCardPhaseState implements ServerState{

    public HomeCardPhaseState(){
        throw new IllegalStateException("This class need realization");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void enter(StateMachine stm) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }
}
