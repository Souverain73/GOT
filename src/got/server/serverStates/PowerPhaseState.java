package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import got.gameStates.StateID;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;

public class PowerPhaseState implements ServerState{
	private static final String name = "PowerPhase";
	private Player currentPlayer;
	private StateMachine stm;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return StateID.POWER_PHASE;
	}

	
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
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
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = (PlayerConnection)c;
		Player player = connection.player;
		//Если клиент отправил пакет о готовности, значит он закончил ход или не может совершить ход.
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
				stm.setState(new ChangeState(new PlanningPhaseState(), true));
			}
			//передаем управление следующему игроку.
			nextTurn();
		}

		if (pkg instanceof Packages.CollectInfluence){
			Packages.CollectInfluence msg = ((Packages.CollectInfluence)pkg);
			GameServer.getServer().sendToAllTCP(msg);
		}
		
		if (pkg instanceof Packages.ChangeUnits){
			Packages.ChangeUnits msg = ((Packages.ChangeUnits)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(
					player.id, msg.region, msg.units
			));
		}
		
		if (pkg instanceof Packages.Act){
			Packages.Act msg = ((Packages.Act)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerAct(msg.from, 0));
		}
	}
	
	private void nextTurn(){
		currentPlayer = PlayerManager.instance().getPlayerByFraction(
				Game.instance().getThroneTrack().getNext(currentPlayer.getFraction()));
		GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
	};
}
