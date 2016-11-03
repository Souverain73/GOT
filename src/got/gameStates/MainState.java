package got.gameStates;

import java.util.Vector;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.GameClient;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.FPSCounterObject;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.TextObject;
import got.graphics.DrawSpace;
import got.network.Packages.ChangeState;
import got.server.PlayerManager;
import got.server.GameServer.PlayerConnection;
import got.utils.LoaderParams;

import static got.network.Packages.Ready;

public class MainState extends AbstractGameState {
	private static final String name = "MainState";
	private StateMachine stm;
	private Vector<GameObject> gameObjects;
	
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
		AbstractGameObject fractionText = new TextObject(PlayerManager.getSelf().getFraction().toString()); 
		fractionText.setPos(new Vector2f(10,10));
		fractionText.setSpace(DrawSpace.SCREEN);
		addObject(fractionText);
		super.enter(extstm);
	}

	@Override
	public void exit() {
		super.exit();
		gameObjects.forEach(o->o.finish());
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		gameObjects.forEach(obj->obj.draw(stm.getCurrentState()));
		super.draw();
		stm.draw();
	}

	@Override
	public void update() {
		gameObjects.forEach(obj->obj.update(stm.getCurrentState()));
		super.update();
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
