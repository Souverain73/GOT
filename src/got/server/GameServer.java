package got.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import got.gameObjects.GameMapObject;
import got.gameObjects.MapPartObject;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.PlayerDisconnected;
import got.network.Packages.ServerMessage;
import got.server.serverStates.GameConfigState;
import got.server.serverStates.NetworkRoomState;
import got.server.serverStates.PlanningPhaseState;
import got.server.serverStates.StateMachine;
import got.translation.Language;
import got.translation.Translator;
import got.utils.LoaderParams;
import org.console.Command;
import org.console.Console;
import org.console.ValueCommand;

import static got.network.Network.portTCP;

public class GameServer {
	public static class Shared{
		public int attackerRegionID;
		public int defenderRegionID;
		public int attackerID;
		public int defenderID;
	}

	private static final String MAP_FILE = "data/map.xml";

	public static Shared shared = new Shared();
	private static StateMachine stm = new StateMachine();
	private static Server server = null;
	private ConcurrentLinkedQueue<Runnable> taskPool = new ConcurrentLinkedQueue<>();
	private GameMapObject map;


	public static Server getServer(){
		Log.set(Log.LEVEL_NONE);
		return server;
	}
	
	public static void main(String[] args) throws IOException {
		int tcpPort;
		int udpPort;

		if (args.length < 2){
			new GameServer(portTCP, Network.portUDP, true);
		}else{
			tcpPort = Integer.valueOf(args[0]);
			udpPort = Integer.valueOf(args[1]);
			new GameServer(tcpPort, udpPort, true);
		}
	}
	
	public static class PlayerConnection extends Connection{
		public Player player;
	}

	public GameServer() throws  IOException{
		this(portTCP, Network.portUDP);
	}

	public GameServer(int tcpPort, int udpPort) throws IOException{
		this(tcpPort, udpPort, false);
	}
	
	public GameServer(int tcpPort, int udpPort, boolean console) throws IOException{
		if (server!=null) throw new IOException("Server already exist");

		Translator.init(Language.RUSSIAN);
		map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", MAP_FILE}));

		Server server = new Server(){
			@Override
			protected Connection newConnection() {
				return new PlayerConnection();
			}
			
		};
		
		Network.register(server);
		
		int retries = 0;
		int maxRetries = 3;
		
		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				super.connected(connection);
			}

			@Override
			public void disconnected(Connection c) {
				
				PlayerConnection connection = (PlayerConnection)c;
				Player player = connection.player;
				if (player != null){
					PlayerManager.instance().disconnect(player.id);
					Log.debug("Player:"+player.getNickname()+" disconnected");
					server.sendToAllExceptTCP(connection.getID(), new PlayerDisconnected(player));
				}
				
				connection.close();
				super.disconnected(connection);
				if (server.getConnections().length == 0){
					server.close();
					System.exit(0);
				}
			}

			@Override
			public void received(Connection c, Object pkg) {
				PlayerConnection connection = (PlayerConnection)c;
				Player player = connection.player;
				//Handle all common packages
				//password state specified package to game state
				if (pkg instanceof Packages.ResumeModal) {
					getServer().sendToAllTCP(new Packages.PlayerResumeModal(player.id));
					return;
				}
				if (pkg instanceof Packages.WaitForModal) {
					//todo: обработать паузу, проверить все ли игроки ждут. Если не все игрока ждут, нельзя посылать пакет Resume
				}
				if (pkg instanceof Packages.Confirm) {
					getServer().sendToAllTCP(new Packages.PlayerConfirm(player.id));
				}
				if (pkg instanceof Packages.Cancel) {
					getServer().sendToAllTCP(new Packages.PlayerCancel(player.id));
				}


				stm.recieve(connection, pkg);
			}

			@Override
			public void idle(Connection connection) {
				super.idle(connection);
			}
		});
		
		while (true){
			retries++;
			try {
				server.bind(tcpPort, udpPort);
				break;
			} catch (IOException e) {
				System.out.println("Can't bind server. Trying again");
				if (retries>=maxRetries){
					e.printStackTrace();
					System.exit(-10);
				}
			}
		}
		
		server.start();
		GameServer.server = server;
		stm.setState(new NetworkRoomState());
		
		System.out.println("Server running on port:"+tcpPort+"/"+udpPort);
		System.out.println("[Control]:ServerReady");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Console cns = new Console();
		cns.addCommand(new Command("main.stop",
				"Остановка сервера",
				(a)->{
			server.close();
			System.exit(0);
			return "";
		}));

		cns.addCommand(new Command("main.say",
				"Отправка сообщения игрокам",
				(input)->{
			if (input.length<2) return "Не достаточно аргументов для выполнения команды";
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < input.length; i++) {
				sb.append(input[i] + " ");
			}
			ServerMessage msg = new ServerMessage();
			msg.message = sb.toString();
			server.sendToAllTCP(msg);
			return "Message " + msg.message + " sended to clients";
		}));

		cns.addCommand(new Command("main.dump",
				"Вывод на экран текущего состояния сервера",
				(a)->{
			StringBuilder sb = new StringBuilder();
			sb.append(PlayerManager.instance().toString() + "\n");
			sb.append("Current State Dump: " + stm.getCurrentState());
			return	sb.toString();
		}));

		cns.addCommand(new Command("main.gc",
				"Перевод сервера в режим настройки игры.",
				"Перевод сервера в режим настройки игры. В режиме настройки можно изменять расстановку войск на карте, установленные приказы, положения на треке и т.д.",
				(a)->{
//					if (stm.getCurrentState() instanceof PlanningPhaseState) {
						GameConfigState gcs = new GameConfigState();
						stm.changeState(gcs, StateMachine.ChangeAction.SET);
						cns.pushState(new Console.State("gc", null, null, ()->stm.changeState(new PlanningPhaseState(), StateMachine.ChangeAction.SET)));
						return "Сервер переведен в режим настройки игры.";
//					}else{
//						return "Перейти к настройке игры можно только в стадии планирования.";
//					}
		}));

		cns.addCommand(new Command("main.gc.region",
				"настройка региона", "команда используется для настройки региона, формат команды region [regioin name] или region list для списка регионов",
				(input)->{
					if (input[1].equals("list")){
						StringBuilder sb = new StringBuilder();
						map.getRegions().stream().map(MapPartObject::getName).forEach(value->{sb.append(value + "\n");});
						return sb.toString();
					}else{
						MapPartObject mpo = map.getRegionByName(input[1]);
						if (mpo == null){
							return "Регион " + input[1] + " не найден.";
						}else{
							cns.pushState(new Console.State("region", mpo));
							return "Настрйока региона " + mpo.getName();
						}
					}
				}));

		cns.addCommand(new ValueCommand<Fraction>("main.gc.region.fraction") {
			@Override
			protected Fraction get() {
				MapPartObject mpo = (MapPartObject) cns.getStateParam("region");
				return mpo.getFraction();
			}

			@Override
			protected String set(Fraction data) {
				//todo: replace with network package to clients
				MapPartObject mpo = (MapPartObject) cns.getStateParam("region");
				mpo.setFraction(data);
				GameServer.getServer().sendToAllTCP(new Packages.ChangeRegionFraction(mpo.getID(), data));
				return "Region fraction set to " + format(data);
			}

			@Override
			protected Fraction parse(String... data) {
				try {
					return Fraction.valueOf(data[1]);
				}catch (IllegalArgumentException e){
					return null;
				}
			}

			@Override
			protected String format(Fraction data) {
				return data.toString();
			}
		});

		cns.addCommand(new ValueCommand<Unit[]>("main.gc.region.units") {
			@Override
			protected Unit[] get() {
				MapPartObject mpo = (MapPartObject) cns.getStateParam("region");
				return mpo.getUnits();
			}

			@Override
			protected String set(Unit[] data) {
				MapPartObject mpo = (MapPartObject) cns.getStateParam("region");
				mpo.setUnits(data);
				GameServer.getServer().sendToAllTCP(new Packages.ChangeUnits(mpo.getID(), data));
				return "Units set to " + format(data);
			}

			@Override
			protected Unit[] parse(String... data) {
				try{
					Unit[] result = new Unit[data.length - 1];
					for (int i = 1; i < data.length; i++) {
						result[i-1] = Unit.valueOf(data[i]);
					}
					return result;
				}catch(IllegalArgumentException e){
					return null;
				}
			}

			@Override
			protected String format(Unit[] data) {
				return Arrays.toString(data);
			}
		});

		cns.start();

		while (true){
			executeTasks();
		}

	}
	
	public void registerTask(Runnable task){
		taskPool.add(task);
	}
	
	public void executeTasks(){
		while(!taskPool.isEmpty()){
			Runnable task = taskPool.poll();
			if (task!=null){
				task.run();
			}
		}
	}
}
