package DGE.gameStates;

import java.util.Vector;

import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;
import DGE.utils.LoaderParams;

public class MainState implements GameState {
	private Vector<GameObject> gameObjects;
	private static final String name = "MainState";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter() {
		gameObjects = new Vector<GameObject>();
		
		System.out.println("Entering "+name);
		GameObject map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", "data/map.xml"}));
		gameObjects.addElement(map);
	}

	@Override
	public void exit() {
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		// TODO draw the map
		gameObjects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.update(this));
	}

}
