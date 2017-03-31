package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ServerState;
import got.vesterosCards.VesterosCard;

/**
 * Created by Souverain73 on 31.03.2017.
 */
public class PlayVesterosCardState implements ServerState {
    private StateMachine stm;
    private VesterosCard currentCard;
    private VesterosPhaseState.VesterosPhaseData data;

    @Override
    public String getName() {
        return "PlayVesterosCard state";
    }

    @Override
    public int getID() {
        return StateID.PLAY_VESTEROS_STATE;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        data = (VesterosPhaseState.VesterosPhaseData) stm.getParam(stm.VESTEROS_PHASE_DATA);
        currentCard = data.cards[data.currentCard];
        GameServer.getServer().sendToAllTCP(new Packages.OpenCard(data.currentCard, currentCard.getID()));
        currentCard.onOpenServer();
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }
}
