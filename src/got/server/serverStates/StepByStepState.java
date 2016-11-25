package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public abstract class StepByStepState implements ServerState{

    protected StateMachine stm;
    protected Player currentPlayer;
    private Class<? extends ServerState> nextStateClass;

    @Override
    public String getName() {
        return "StepByStepState;";
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        //Если у игрока стоит признак готовности, значит он не может больше совершить ход
        //Когда все игроки будут готовы, необходимо осуществить переход к следующей фазе.
        for (Player pl: PlayerManager.instance().getPlayersList()){
            pl.setReady(false);
        }

        //get first player on throne track
        currentPlayer = PlayerManager.instance().getPlayerByFraction(
                Game.instance().getThroneTrack().getFirst());
        //Правильно было бы проверить, может ли игрок совершить ход. Если игрок не имеет приказов набега,
        //нет смысла передавать ему ход. Но в текущей архитектуе сервер самостоятельно не может этого сделать,
        //поэтому отдадим это на сторону клиента.
        GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection c, Object pkg) {
        GameServer.PlayerConnection connection = ((GameServer.PlayerConnection)c);
        Player player = connection.player;
        if (pkg instanceof Packages.Ready){
            //Если о готовности сообщает не текущий игрок, игнорируем сообщение.
            if (player.id != currentPlayer.id) return;
            Packages.Ready msg = ((Packages.Ready)pkg);
            //если свойство ready = true, значит игрок совершил ход
            //если false, значит возможных ходов для него больше нет
            if (!msg.ready){
                player.setReady(true);
            }
            //проверяем, если все игроки готовы, значит никто больше не может совершить ход, значит можно переходить к следующей фазе.
            if (PlayerManager.instance().isAllPlayersReady()){
                try {
                    stm.setState(new ChangeState(nextStateClass.newInstance(), true));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Can't instantiate next State from CLASS");
                }
            }
            //передаем управление следующему игроку.
            nextTurn();
        }
    }

    private void nextTurn(){
        currentPlayer = PlayerManager.instance().getPlayerByFraction(
                Game.instance().getThroneTrack().getNext(currentPlayer.getFraction()));
        GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
    };
    
    protected void setNextState(Class<? extends ServerState> nextStateClass){
        this.nextStateClass = nextStateClass;
    }
}