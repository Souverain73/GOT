package DGE.gameStates;

import java.util.Vector;

import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;
import DGE.utils.LoaderParams;

public class MainState implements GameState {
	private Vector<GameObject> gameObjects;
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enter() {
		System.out.println("Entering main state");
		GameObject map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"fileName", "data/map.xml"}));
		gameObjects.addElement(map);
	}

	@Override
	public void exit() {
		System.out.println("Exit main state");
	}

	@Override
	public void draw() {
		// TODO draw the map
		gameObjects.forEach(obj->obj.draw());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.update());
	}

}
