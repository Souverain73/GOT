package got.gameStates;

import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.gameObjects.GameObject;
import got.utils.UI;
import static got.network.Packages.Ready;

/**
 * @author Souverain73
 *	This class implements base for all GameStates
 */
public class AbstractGameState implements GameState {
	protected Vector<GameObject> gameObjects = new Vector<>();
	/* 
	 * If you want log all recieved packages use super.recieve(), otherwise skip it
	 */
	@Override
	public void recieve(Connection connection, Object pkg) {
		UI.systemMessage(pkg.toString());
	}

	@Override
	public String getName() {
		return "Abstract game state";
	}

	@Override
	public void enter(StateMachine stm) {
		GameClient.instance().sendReady(true);
	}

	@Override
	public void exit() {
		gameObjects.forEach(o->o.finish());
	}

	@Override
	public void draw() {
		gameObjects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		gameObjects.forEach(obj->obj.update(this));
	}
	
	protected void addObject(GameObject obj){
		gameObjects.addElement(obj);
	}

	protected void removeObject(GameObject obj){
		gameObjects.remove(obj);
		obj.finish();
	}

	@Override
	public int getID() {
		return 0;
	}
}
