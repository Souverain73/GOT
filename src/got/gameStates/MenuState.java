package got.gameStates;

import java.io.IOException;
import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.network.Network;
import got.network.Packages;
import got.network.Packages.InitPlayer;
import got.utils.UI;

public class MenuState implements GameState {
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
		gameObjects.add(new ImageButton("play.jpg", 100,100,200,100, null).setCallback((sender, params)->{
			String host = UI.getString("Enter host", "host", "localhost");
			try{
				GameClient.instance().connect(host);
			}catch(IOException e){
				System.out.println("Can't connect to host:"+host);
				return;
			}
			GameClient.instance().send(new Packages.LogIn());
			GameClient.instance().getStateMachine().setState(new NetworkRoomState());
			System.out.println("Connection sccessfull");
		}));
		//button exit
		gameObjects.add(new ImageButton("exit.jpg", 100,220,200,100, null).setCallback((sender, params)->{
			GameClient.instance().exit();
		}));
		
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
		if (pkg instanceof InitPlayer){
			GameClient.instance().getStateMachine().setState(new NetworkRoomState());
		}
	}

	
}
