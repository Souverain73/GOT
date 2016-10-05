package DGE.gameStates;

import java.util.Vector;

import DGE.gameObjects.FPSCounterObject;
import DGE.gameObjects.GameObject;
import DGE.graphics.DrawSpace;
import DGE.graphics.Font;
import DGE.graphics.Text;

public class TestState implements GameState{
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
		hello.draw(0, 0, 1, 1, DrawSpace.WORLD);
		objects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		objects.forEach(obj->obj.update(this));
	}

}
