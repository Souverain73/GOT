package DGE.gameStates;

import java.util.Vector;

import DGE.Game;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageButton;

public class MenuState implements GameState {
	private final String name = "MenuState";
	private Vector<GameObject> gameObjects;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter() {
		gameObjects = new Vector<GameObject>();
		
		System.out.println("Entering "+name);
		//button play
		gameObjects.add(new ImageButton("paly.jpg", 100,100,200,100).setCallback((sender, params)->{
			StateMachine.instance().setState(new MainState());
		}));
		gameObjects.add(new ImageButton("exit.jpg", 100,220,200,100).setCallback((sender, params)->{
			Game.instance().exit();
		}));
		
	}

	@Override
	public void exit() {
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.draw());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.update());
	}

}