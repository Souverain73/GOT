package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import got.Player;

public class Packages {
	private Packages() {
	}

	
	/**
	 * Method register all network packages.
	 * Must be used for server and client/
	 * @param endpoint
	 */
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		kryo.register(ServerMessage.class);
		kryo.register(LogIn.class);
		kryo.register(InitPlayer.class);
		kryo.register(PlayerConnected.class);
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
		kryo.register(ChangePhase.class);
		kryo.register(SetAction.class);
		kryo.register(Ready.class);
		kryo.register(Act.class);
		kryo.register(SelectRegion.class);
		kryo.register(Move.class);
		kryo.register(CollectUnits.class);
		kryo.register(CollectInfluence.class);
		kryo.register(SelectItem.class);
		kryo.register(Bet.class);
		kryo.register(GetGlobalState.class);
		kryo.register(ForceReadyAt.class);
		kryo.register(AddInfluence.class);
		kryo.register(SetGlobalState.class);
	}
	
	public static class NetPackage{
		private NetPackage(){
			
		}
	}
	
	public static class BroadcastPackage extends NetPackage{
		private BroadcastPackage() {

		}
	}
	
	public static class ClientServerPackage extends NetPackage{
		private ClientServerPackage() {

		}
	}

	public static class ServerClientPackage extends NetPackage{
		private ServerClientPackage() {

		}
	}
	
	public static class LogIn extends ClientServerPackage{
		
	}
	
	
	
	public static class ServerMessage extends ServerClientPackage{
		public String message;
	}
	
	/**
	 * �������������� ������ ������ �� �������
	 */
	public static class InitPlayer extends ServerClientPackage{
		public Player player;
	}
	
	public static class PlayerConnected extends BroadcastPackage{
		public String nickname;
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
		int region;
		int action;

		public PlayerSetAction() {
		}
	}

	/**
	 * �������� ���� ������� � ���������� ������ player
	 */
	public static class PlayerReady extends BroadcastPackage {
		int player;
		int ready;

		public PlayerReady() {
		}
	}

	/**
	 * �������� ���� �������, ��� ������ ��� ������ player
	 */
	public static class PlayerTurn extends BroadcastPackage {
		int player;

		public PlayerTurn() {
		}
	}

	/**
	 * �������� ���� �������, ��� ������� ����� ������ ������ � ���������� from
	 * �� ���������� to
	 */
	public static class PlayerAct extends BroadcastPackage {
		int from;
		int to;

		public PlayerAct() {
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
	public static class ChangePhase extends BroadcastPackage {
		int phase;

		public ChangePhase() {
		}
	}

	/**
	 * ������������� �������� ��� �������.
	 */
	public static class SetAction extends ClientServerPackage {
		int region;
		int action;

		public SetAction() {
		}
	}

	/**
	 * ������������� ���������� ������� � ���������� ����.
	 */
	public static class Ready extends ClientServerPackage {
		int ready;

		public Ready() {
		}
	}

	/**
	 * �������� �������, ��� ����� ������ ������ � ���������� from �� ����������
	 * to
	 */
	public static class Act extends ClientServerPackage {
		int from;
		int to;

		public Act() {
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
	public static class CollectUnits extends ClientServerPackage {
		int region;
		int units[];

		public CollectUnits() {
			units = new int[4];
		}
	}

	/**
	 * �������� �������, ��� ����� �������� ���� ������� � �������.
	 */
	public static class CollectInfluence extends ClientServerPackage {
		int region;

		public CollectInfluence() {
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