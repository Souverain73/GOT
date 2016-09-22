package DGE.gameStates;

import java.util.Vector;

import org.joml.Vector2f;

import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageObject;
import DGE.graphics.TextureManager;

public class TestState implements GameState{
	private static final String name = "TestState";
	private Vector<GameObject> objects;
	
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
		objects.add(new ImageObject(TextureManager.instance().loadTexture("plus.png"),
				new Vector2f(-50,-50), 100, 100));
	}

	@Override
	public void exit() {
		objects.forEach(obj->obj.finish());	
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		objects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		objects.forEach(obj->obj.update(this));
	}

}
