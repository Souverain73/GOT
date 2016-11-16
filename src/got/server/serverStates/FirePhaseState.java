package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import got.gameStates.StateID;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.GameServer.PlayerConnection;
import got.server.PlayerManager;

public class FirePhaseState implements ServerState {
	private static final String name = "FirePhase";
	private Player currentPlayer;
	private StateMachine stm;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return StateID.FIRE_PHASE;
	}

	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
		//���� � ������ ����� ������� ����������, ������ �� �� ����� ������ ��������� ���
		//����� ��� ������ ����� ������, ���������� ����������� ������� � ��������� ����.
		for (Player pl: PlayerManager.instance().getPlayersList()){
			pl.setReady(false);
		}
		
		//get first player on throne track
		currentPlayer = PlayerManager.instance().getPlayerByFraction(
				Game.instance().getThroneTrack().getFirst());
		//��������� ���� �� ���������, ����� �� ����� ��������� ���. ���� ����� �� ����� �������� ������,
		//��� ������ ���������� ��� ���. �� � ������� ���������� ������ �������������� �� ����� ����� �������,
		//������� ������� ��� �� ������� �������.
		GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
	}

	@Override
	public void exit() {

	}

	@Override
	public void recieve(Connection c, Object pkg) {
		PlayerConnection connection = (PlayerConnection)c;
		Player player = connection.player;
		//���� ������ �������� ����� � ����������, ������ �� �������� ��� ��� �� ����� ��������� ���.
		if (pkg instanceof Packages.Ready){
			//���� � ���������� �������� �� ������� �����, ���������� ���������.
			if (player.id != currentPlayer.id) return;
			Packages.Ready msg = ((Packages.Ready)pkg);
			//���� �������� ready = true, ������ ����� �������� ���
			//���� false, ������ ��������� ����� ��� ���� ������ ���
			if (!msg.ready){
				player.setReady(true);
			}
			//���������, ���� ��� ������ ������, ������ ����� ������ �� ����� ��������� ���, ������ ����� ���������� � ��������� ����.
			if (PlayerManager.instance().isAllPlayersReady()){
				stm.setState(new ChangeState(new PlanningPhaseState(), true));
			}
			//�������� ���������� ���������� ������.
			nextTurn();
		}
		
		if (pkg instanceof Packages.Act){
			Packages.Act msg = ((Packages.Act)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerAct(msg.from, msg.to));
		}
		
	}
	
	private void nextTurn(){
		currentPlayer = PlayerManager.instance().getPlayerByFraction(
				Game.instance().getThroneTrack().getNext(currentPlayer.getFraction()));
		GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
	};
}
