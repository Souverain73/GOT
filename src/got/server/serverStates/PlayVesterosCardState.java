package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.interfaces.IPauseable;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ParallelState;
import got.server.serverStates.base.ServerState;
import got.utils.UI;
import got.vesterosCards.VesterosCard;

import javax.imageio.IIOParamController;

/**
 * Created by Souverain73 on 31.03.2017.
 */
public class PlayVesterosCardState extends ParallelState{
    private VesterosCard currentCard;
    private VesterosCard.openParams params;
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
        super.enter(stm);
        data = (VesterosPhaseState.VesterosPhaseData) stm.getParam(stm.VESTEROS_PHASE_DATA);
        currentCard = data.cards[data.currentCard];
        GameServer.getServer().sendToAllTCP(new Packages.OpenCard(data.currentCard, currentCard.getID()));
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.PlayerSelectItem) {
            Packages.PlayerSelectItem msg = (Packages.PlayerSelectItem) pkg;
            params.selection = msg.select;
        }
    }

    @Override
    protected void onReadyToChangeState() {
        currentCard.onOpenServer(stm, params);
    }

    @Override
    public void resume() {
        UI.systemMessage("Unpause Play vesteros");
        stm.changeState(null, StateMachine.ChangeAction.REMOVE);
    }
}
