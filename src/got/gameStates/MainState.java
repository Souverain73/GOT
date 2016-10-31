package got.gameStates;

import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.GameClient;
import got.gameObjects.FPSCounterObject;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.network.Packages.ChangeState;
import got.server.GameServer.PlayerConnection;
import got.utils.LoaderParams;

import static got.network.Packages.Ready;

public class MainState extends AbstractGameState {
	private Vector<GameObject> gameObjects;
	private static final String name = "MainState";
	private StateMachine stm;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine extstm) {
		gameObjects = new Vector<GameObject>();
		stm = new StateMachine();
		System.out.println("Entering "+name);
		GameObject map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", "data/map.xml"}));
		gameObjects.addElement(map);
		super.enter(extstm);
	}

	@Override
	public void exit() {
		gameObjects.forEach(o->o.finish());
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		gameObjects.forEach(obj->obj.draw(stm.getCurrentState()));
		stm.draw();
	}

	@Override
	public void update() {
		gameObjects.forEach(obj->obj.update(stm.getCurrentState()));
		stm.update();
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof ChangeState){
			int stateID = ((ChangeState) pkg).state;
			GameClient.instance().registerTask(new Runnable() {
				@Override
				public void run() {
					stm.setState(StateID.getGameStateByID(stateID));
				}
			});
			return;
		}
		stm.recieve(connection, pkg);
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}
}
