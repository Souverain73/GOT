package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

/**
 * Created by Souverain73 on 29.11.2016.
 */
public class SelectHomeCardPhaseState implements ServerState{

    public SelectHomeCardPhaseState(){

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
