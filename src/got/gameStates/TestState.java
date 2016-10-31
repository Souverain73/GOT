package got.gameStates;

import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;

import got.gameObjects.FPSCounterObject;
import got.gameObjects.GameObject;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;
import got.server.GameServer.PlayerConnection;

public class TestState extends AbstractGameState{
	private static final String name = "TestState";
	private Vector<GameObject> objects;
	private Font test;
	private Text hello;
	
	public TestState() {
		objects = new Vector<GameObject>();
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		System.out.println("Enter "+name);
		test = new Font("test");
		hello = Text.newInstance("Hello World!!!!", test);
		objects.add(new FPSCounterObject());
	}

	@Override
	public void exit() {
		objects.forEach(obj->obj.finish());	
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		GraphicModule.instance().setDrawSpace(DrawSpace.WORLD);
		hello.draw(0, 0, 1, 1);
		objects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		objects.forEach(obj->obj.update(this));
	}


	@Override
	public void recieve(Connection connection, Object pkg) {

	}

}
