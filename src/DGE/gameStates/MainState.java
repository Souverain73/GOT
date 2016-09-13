package DGE.gameStates;

import java.util.Vector;

import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;
import DGE.utils.LoaderParams;

public class MainState implements GameState {
	private Vector<GameObject> gameObjects;
	private static final String name = "MainState";
	private StateMachine stm;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter() {
		gameObjects = new Vector<GameObject>();
		stm = new StateMachine();
		System.out.println("Entering "+name);
		GameObject map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", "data/map.xml"}));
		gameObjects.addElement(map);
		
		stm.setState(new PlanningPhase());
	}

	@Override
	public void exit() {
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

}
