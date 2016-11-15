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
	 * Запрос от клиента на подключение.
	 */
	public static class LogIn extends ClientServerPackage {
		public String nickname;

		public LogIn Nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}
	}
	
	
	
	/**
	 *	Сообщение о ошибке подключения к сетевому лоби.
	 *	Используется для сообщения клиенту о занятости лобби или иных причинах, 
	 *	по которым подключение не возможно.
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
	 *	Передает клиенту список игроков.
	 *	Используется для получения информации о составе сетевого лоби для вновь присоединившихся игроков.
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
	 *	Пакет, представляющий собой сообщение от сервера клиенту.
	 *	Используется для передачи различных системных сообщений
	 */
	public static class ServerMessage extends ServerClientPackage {
		public String message;
		public ServerMessage(){};
		public ServerMessage(String message){
			this.message = message;
		}
	}

	/**
	 * Инициализирует модель игрока на клиенте
	 */
	public static class InitPlayer extends ServerClientPackage {
		public Player player;
	}

	/**
	 * Сообщает всем игрокам о подключении игрока
	 */
	public static class PlayerConnected extends BroadcastPackage {
		public Player player;
	}

	/**
	 * Сообзает всем игрокам о отключении игрока
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
	 *	Пакет для передачи списка фракций. <br>
	 *	Порядок фракций в массиве определяет принадлежность игроков к фракциям.<br>
	 *	players[id].fraction = fractions[id];<br>
	 *	Используется для инициализации распределения игроков по фракциям в начале игры.
	 */
	public static class SetFractions extends BroadcastPackage {
		public Fraction[] fractions;
		public SetFractions() {}
		public SetFractions(Fraction[] fractions) {
			this.fractions = fractions;
		}
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
		public int region;
		public Action action;
		public PlayerSetAction() {}
		public PlayerSetAction(int region, Action action) {
			this.region = region;
			this.action = action;
		}
		
	}

	/**
	 * сообщает всем игрокам о готовности игрока player.
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
	 * сообщает всем игрокам, что сейчас ход игрока player
	 */
	public static class PlayerTurn extends BroadcastPackage {
		public int playerID;

		public PlayerTurn() {}

		public PlayerTurn(int playerID) {
			this.playerID = playerID;
		}
		
	}

	/**
	 * сообщает всем игрокам, что текущий игрок играет приказ с терриротии from
	 * на территорию to
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
	public static class ChangeState extends BroadcastPackage {
		public int state;
		public ChangeState() {}
		public ChangeState(int state) {
			this.state = state;
		}
	}

	/**
	 * устанавливает действие для региона.
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
	 * Устанавливает готовность клиента к завершению фазы или хода.
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
	 * сообщает серверу, что игрок играет приказ с территории from на территорию
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
	 * сообщает серверу, что игрок собирает очки влияния с региона.
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