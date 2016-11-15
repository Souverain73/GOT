package got.gameStates;


import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.graphics.DrawSpace;

public class MenuState extends AbstractGameState {
	private final String name = "MenuState";
	private Vector<GameObject> gameObjects;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		gameObjects = new Vector<GameObject>();
		
		System.out.println("Entering "+name);
		//button play
		ImageButton btn = new ImageButton("buttons/play.png", 540, 260, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().getStateMachine().setState(new NetworkRoomState());
		});
		btn.setSpace(DrawSpace.SCREEN);
		gameObjects.add(btn);
		//button exit
		btn = new ImageButton("buttons/exit.png", 540, 380, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().exit();
		});
		btn.setSpace(DrawSpace.SCREEN);
		gameObjects.add(btn);
		
	}

	@Override
	public void exit() {
		gameObjects.forEach(o->o.finish());
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		gameObjects.forEach(obj->obj.update(this));
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
	}

	
}
