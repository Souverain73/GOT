package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import got.Fraction;
import got.Player;
import got.gameObjects.ActionObject.Action;

public class Packages {
	private Packages() {
	}

	/**
	 * Method register all network packages. Must be used for server and client/
	 * 
	 * @param endpoint
	 */
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		kryo.register(ServerMessage.class);
		kryo.register(LogIn.class);
		kryo.register(PlayersList.class);
		kryo.register(InitPlayer.class);
		kryo.register(ConnectionError.class);
		kryo.register(PlayerConnected.class);
		kryo.register(PlayerDisconnected.class);
		kryo.register(SetFractions.class);
		kryo.register(SetUnits.class);
		kryo.register(SetTrack.class);
		kryo.register(PlayerSetAction.class);
		kryo.register(PlayerReady.class);
		kryo.register(PlayerTurn.class);
		kryo.register(PlayerAct.class);
		kryo.register(PlayerSelectRegion.class);
		kryo.register(PlayerMove.class);
		kryo.register(OpenCard.class);
		kryo.register(PlayerSelectItem.class);
		kryo.register(PlayerBets.class);
		kryo.register(ChangeState.class);
		kryo.register(SetAction.class);
		kryo.register(Ready.class);
		kryo.register(Act.class);
		kryo.register(SelectRegion.class);
		kryo.register(Move.class);
		kryo.register(ChangeUnits.class);
		kryo.register(CollectInfluence.class);
		kryo.register(SelectItem.class);
		kryo.register(Bet.class);
		kryo.register(GetGlobalState.class);
		kryo.register(ForceReadyAt.class);
		kryo.register(AddInfluence.class);
		kryo.register(SetGlobalState.class);
	}

	public static class NetPackage {
		private NetPackage() {

		}
	}

	public static class BroadcastPackage extends NetPackage {
		private BroadcastPackage() {

		}
	}

	public static class ClientServerPackage extends NetPackage {
		private ClientServerPackage() {

		}
	}

	public static class ServerClientPackage extends NetPackage {
		private ServerClientPackage() {

		}
	}

	
	/**
	 * ������ �� ������� �� �����������.
	 */
	public static class LogIn extends ClientServerPackage {
		public String nickname;

		public LogIn Nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}
	}
	
	
	
	/**
	 *	��������� � ������ ����������� � �������� ����.
	 *	������������ ��� ��������� ������� � ��������� ����� ��� ���� ��������, 
	 *	�� ������� ����������� �� ��������.
	 */
	public static class ConnectionError extends ServerClientPackage {
		public final static int LobbyIsFull = 1; 
		public int errorCode;
		public ConnectionError() {
		}
		public ConnectionError(int code){
			this.errorCode = code;
		}
	}
	
	/**
	 *	�������� ������� ������ �������.
	 *	������������ ��� ��������� ���������� � ������� �������� ���� ��� ����� ���������������� �������.
	 */
	public static class PlayersList extends ServerClientPackage {
		public Player [] players;
		public PlayersList() {
			players = new Player[6];
		}
		public PlayersList(Player[] list){
			players = list;
		}
	}

	
	/**
	 *	�����, �������������� ����� ��������� �� ������� �������.
	 *	������������ ��� �������� ��������� ��������� ���������
	 */
	public static class ServerMessage extends ServerClientPackage {
		public String message;
		public ServerMessage(){};
		public ServerMessage(String message){
			this.message = message;
		}
	}

	/**
	 * �������������� ������ ������ �� �������
	 */
	public static class InitPlayer extends ServerClientPackage {
		public Player player;
	}

	/**
	 * �������� ���� ������� � ����������� ������
	 */
	public static class PlayerConnected extends BroadcastPackage {
		public Player player;
	}

	/**
	 * �������� ���� ������� � ���������� ������
	 */
	public static class PlayerDisconnected extends BroadcastPackage {
		public Player player;
		
		public PlayerDisconnected(){
			
		}
		
		public PlayerDisconnected(Player player) {
			this.player = player;
		}
	}

	/**
	 *	����� ��� �������� ������ �������. <br>
	 *	������� ������� � ������� ���������� �������������� ������� � ��������.<br>
	 *	players[id].fraction = fractions[id];<br>
	 *	������������ ��� ������������� ������������� ������� �� �������� � ������ ����.
	 */
	public static class SetFractions extends BroadcastPackage {
		public Fraction[] fractions;
		public SetFractions() {}
		public SetFractions(Fraction[] fractions) {
			this.fractions = fractions;
		}
	}
	
	/**
	 * ������������� ����� ������ �� ������������ ���������.
	 */
	public static class SetUnits extends BroadcastPackage {
		int region;
		int units[];

		public SetUnits() {
			units = new int[4];
		}
	}

	/**
	 * ������������� ��������� ������� �� ������.
	 */
	public static class SetTrack extends BroadcastPackage {
		int track;
		int data[];

		public SetTrack() {
			data = new int[7];
		}
	}

	/**
	 * ������������� �������� ��� �������.
	 */
	public static class PlayerSetAction extends BroadcastPackage {
		public int region;
		public Action action;
		public PlayerSetAction() {}
		public PlayerSetAction(int region, Action action) {
			this.region = region;
			this.action = action;
		}
		
	}

	/**
	 * �������� ���� ������� � ���������� ������ player.
	 */
	public static class PlayerReady extends BroadcastPackage {
		public int playerID;
		public boolean ready;
		public PlayerReady() {
		}
		public PlayerReady(int playerID, boolean ready) {
			this.playerID = playerID;
			this.ready = ready;
		}
		
	}

	/**
	 * �������� ���� �������, ��� ������ ��� ������ player
	 */
	public static class PlayerTurn extends BroadcastPackage {
		public int playerID;

		public PlayerTurn() {}

		public PlayerTurn(int playerID) {
			this.playerID = playerID;
		}
		
	}

	/**
	 * �������� ���� �������, ��� ������� ����� ������ ������ � ���������� from
	 * �� ���������� to
	 */
	public static class PlayerAct extends BroadcastPackage {
		public int from;
		public int to;

		public PlayerAct() {}

		public PlayerAct(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
	}

	/**
	 * �������� ���� �������, ��� ������� ����� ������ ������ region
	 */
	public static class PlayerSelectRegion extends BroadcastPackage {
		int region;

		public PlayerSelectRegion() {
		}
	}

	/**
	 * �������� ���� �������, ��� ������� ����� ���������� � ���������� from ��
	 * ���������� to ������ units
	 */
	public static class PlayerMove extends BroadcastPackage {
		int from;
		int to;
		int units[];

		public PlayerMove() {
			units = new int[4];
		}
	}

	/**
	 * �������� �������, ��� ������� ����� card �� ������ number.
	 */
	public static class OpenCard extends BroadcastPackage {
		int number;
		int card;

		public OpenCard() {
		}
	}

	/**
	 * �������� �������, ��� ���� ������� ������� select.
	 */
	public static class PlayerSelectItem extends BroadcastPackage {
		int select;

		public PlayerSelectItem() {
		}
	}

	/**
	 * �������� �������, ����� ������ ���� �������.
	 */
	public static class PlayerBets extends BroadcastPackage {
		int bets[];

		public PlayerBets() {
			bets = new int[7];
		}
	}

	/**
	 * �������� ���������� � ����� ���� ����.
	 */
	public static class ChangeState extends BroadcastPackage {
		public int state;
		public ChangeState() {}
		public ChangeState(int state) {
			this.state = state;
		}
	}

	/**
	 * ������������� �������� ��� �������.
	 */
	public static class SetAction extends ClientServerPackage {
		public int region;
		public Action action;
		public SetAction() {}
		public SetAction(int region, Action action) {
			this.region = region;
			this.action = action;
		}
		
	}

	/**
	 * ������������� ���������� ������� � ���������� ���� ��� ����.
	 */
	public static class Ready extends ClientServerPackage {
		public boolean ready;
		
		public Ready() {
		}
		public Ready(boolean ready){
			this.ready = ready;
		}
	}

	/**
	 * �������� �������, ��� ����� ������ ������ � ���������� from �� ����������
	 * to
	 */
	public static class Act extends ClientServerPackage {
		public int from;
		public int to;

		public Act() {}

		public Act(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
	}

	/**
	 * �������� �������, ��� ����� ������ ������ region
	 */
	public static class SelectRegion extends ClientServerPackage {
		int region;

		public SelectRegion() {
		}
	}

	/**
	 * �������� �������, ��� ����� ���������� � ���������� from �� ���������� to
	 * ������ units
	 */
	public static class Move extends ClientServerPackage {
		int from;
		int to;
		int units[];

		public Move() {
			units = new int[4];
		}
	}

	/**
	 * �������� �������, ��� ����� �������� ������ � ������� � �������� �����
	 * ����� ������.
	 */
	public static class ChangeUnits extends ClientServerPackage {
		public int region;
		public int units[];

		public ChangeUnits() {
			units = new int[4];
		}

		public ChangeUnits(int region, int[] units) {
			this.region = region;
			this.units = units;
		}
	}

	/**
	 * �������� �������, ��� ����� �������� ���� ������� � �������.
	 */
	public static class CollectInfluence extends ClientServerPackage {
		public int region;

		public CollectInfluence() {
		}

		public CollectInfluence(int region) {
			this.region = region;
		}
	}

	/**
	* 
	*/
	public static class SelectItem extends ClientServerPackage {
		int select;

		public SelectItem() {
		}
	}

	/**
	 * �������� �������, ��� ����� ������ value ����� �������.
	 */
	public static class Bet extends ClientServerPackage {
		int value;

		public Bet() {
		}
	}

	/**
	 * ����������� � ������� ���������� � ������� ���������. ������������ ���
	 * �������������� ��� �����.
	 */
	public static class GetGlobalState extends ClientServerPackage {
		public GetGlobalState() {
		}
	}

	/**
	 * �������� ������� ��� ����� seconds ������, ���������� ������ �����
	 * ����������� �������������.
	 */
	public static class ForceReadyAt extends ServerClientPackage {
		int seconds;

		public ForceReadyAt() {
		}
	}

	/**
	 * ��������� ������ ���� ������� � ���������� value. Value ����� ����
	 * �������������.
	 */
	public static class AddInfluence extends ServerClientPackage {
		int value;

		public AddInfluence() {
		}
	}

	/**
	 * �������� ��� ���������� � ������� ��������� ����. ������������ ���
	 * �������������� ����������� ������� ������ � ������������� ����� ����.
	 */
	public static class SetGlobalState extends ServerClientPackage {
		int stateData;

		public SetGlobalState() {
		}
	}
}