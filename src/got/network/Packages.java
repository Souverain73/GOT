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
	 * Инициализирует модель игрока на клиенте
	 */
	public static class InitPlayer extends ServerClientPackage{
		public Player player;
	}
	
	public static class PlayerConnected extends BroadcastPackage{
		public String nickname;
	}
	
	
	/**
	 * устанавливает набор юнитов на определенной територии.
	 */
	public static class SetUnits extends BroadcastPackage {
		int region;
		int units[];

		public SetUnits() {
			units = new int[4];
		}
	}

	/**
	 * устанавливает положение игроков на треках.
	 */
	public static class SetTrack extends BroadcastPackage {
		int track;
		int data[];

		public SetTrack() {
			data = new int[7];
		}
	}

	/**
	 * устанавливает действие для региона.
	 */
	public static class PlayerSetAction extends BroadcastPackage {
		int region;
		int action;

		public PlayerSetAction() {
		}
	}

	/**
	 * сообщает всем игрокам о готовности игрока player
	 */
	public static class PlayerReady extends BroadcastPackage {
		int player;
		int ready;

		public PlayerReady() {
		}
	}

	/**
	 * сообщает всем игрокам, что сейчас ход игрока player
	 */
	public static class PlayerTurn extends BroadcastPackage {
		int player;

		public PlayerTurn() {
		}
	}

	/**
	 * сообщает всем игрокам, что текущий игрок играет приказ с терриротии from
	 * на территорию to
	 */
	public static class PlayerAct extends BroadcastPackage {
		int from;
		int to;

		public PlayerAct() {
		}
	}

	/**
	 * сообщеат всем игрокам, что текущий игрок выбрал регион region
	 */
	public static class PlayerSelectRegion extends BroadcastPackage {
		int region;

		public PlayerSelectRegion() {
		}
	}

	/**
	 * сообщает всем игрокам, что текущий игрок перемещает с терриротии from на
	 * территорию to юнитов units
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
	 * сообщает игрокам, что открыта карта card из колоды number.
	 */
	public static class OpenCard extends BroadcastPackage {
		int number;
		int card;

		public OpenCard() {
		}
	}

	/**
	 * сообщает игрокам, что была выбрана позиция select.
	 */
	public static class PlayerSelectItem extends BroadcastPackage {
		int select;

		public PlayerSelectItem() {
		}
	}

	/**
	 * сообщает игрокам, какие ставки были сделаны.
	 */
	public static class PlayerBets extends BroadcastPackage {
		int bets[];

		public PlayerBets() {
			bets = new int[7];
		}
	}

	/**
	 * Передает информацию о смене фазы игры.
	 */
	public static class ChangePhase extends BroadcastPackage {
		int phase;

		public ChangePhase() {
		}
	}

	/**
	 * устанавливает действие для региона.
	 */
	public static class SetAction extends ClientServerPackage {
		int region;
		int action;

		public SetAction() {
		}
	}

	/**
	 * устанавливает готовность клиента к завершению фазы.
	 */
	public static class Ready extends ClientServerPackage {
		int ready;

		public Ready() {
		}
	}

	/**
	 * сообщает серверу, что игрок играет приказ с территории from на территорию
	 * to
	 */
	public static class Act extends ClientServerPackage {
		int from;
		int to;

		public Act() {
		}
	}

	/**
	 * сообщает серверу, что игрок выбрал регион region
	 */
	public static class SelectRegion extends ClientServerPackage {
		int region;

		public SelectRegion() {
		}
	}

	/**
	 * сообщает серверу, что игрок перемещает с территории from на территорию to
	 * юнитов units
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
	 * сообщает серверу, что игрок набирает войска в регионе и передает новый
	 * набор юнитов.
	 */
	public static class CollectUnits extends ClientServerPackage {
		int region;
		int units[];

		public CollectUnits() {
			units = new int[4];
		}
	}

	/**
	 * сообщает серверу, что игрок собирает очки влияния с региона.
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
	 * сообщает серверу, что игрок ставит value очков влияния.
	 */
	public static class Bet extends ClientServerPackage {
		int value;

		public Bet() {
		}
	}

	/**
	 * запрашивает у сервера информацию о текущем состоянии. Используется для
	 * восстановления при сбоях.
	 */
	public static class GetGlobalState extends ClientServerPackage {
		public GetGlobalState() {
		}
	}

	/**
	 * сообщает клиенту что через seconds секунд, готовность игрока будет
	 * проставлена автоматически.
	 */
	public static class ForceReadyAt extends ServerClientPackage {
		int seconds;

		public ForceReadyAt() {
		}
	}

	/**
	 * добавляет игроку очки влияния в количестве value. Value может быть
	 * отрицательным.
	 */
	public static class AddInfluence extends ServerClientPackage {
		int value;

		public AddInfluence() {
		}
	}

	/**
	 * передает всю информацию о текущем состоянии игры. Используется для
	 * восстановления разорванных игровых сессий и синхронизации после сбоя.
	 */
	public static class SetGlobalState extends ServerClientPackage {
		int stateData;

		public SetGlobalState() {
		}
	}
}