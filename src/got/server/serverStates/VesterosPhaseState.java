package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.interfaces.IPauseable;
import got.model.ChangeAction;
import got.model.Game;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ServerState;
import got.utils.Timers;
import got.utils.UI;
import got.vesterosCards.VesterosCard;

import java.util.stream.IntStream;

/**
 * Created by Souverain73 on 30.03.2017.
 */
public class VesterosPhaseState implements ServerState, IPauseable{
    private StateMachine stm;
    class VesterosPhaseData {
        public VesterosCard[] cards = new VesterosCard[3];
        public int currentCard = -1;
    }

    VesterosPhaseData data = new VesterosPhaseData();

    @Override
    public String getName() {
        return "Vesteros Phase";
    }

    @Override
    public int getID() {
        return StateID.VESTEROS_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        Game.instance().nextTurn();
        UI.systemMessage("Entering Vesteros Phase");
        IntStream.range(0, 3).forEach(i->data.cards[i] = Game.instance().getVesterosDeck(i).getTopCard());

        stm.saveParam(stm.VESTEROS_PHASE_DATA, data);

        for(int i=0; i<3; i++){
            GameServer.getServer().sendToAllTCP(new Packages.OpenCard(i, data.cards[i].getID()));
            Timers.wait(1000);
        }

        playNextCard();
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        UI.systemMessage("Resume vesteros phase");
        if (data.currentCard < 2) {
            playNextCard();
        }else{
            stm.changeState(new PlanningPhaseState(), ChangeAction.SET);
        }
    }

    private void playNextCard(){
        data.currentCard++;
        stm.changeState(new PlayVesterosCardState(), ChangeAction.PUSH);
    }
}
