package got.gameStates.test;

import got.gameObjects.FPSCounterObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;

public class TestState extends AbstractGameState {
	private static final String name = "TestState";
	private Font test;
	private Text hello;
	
	public TestState() {
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
		addObject(new FPSCounterObject());
	}

	@Override
	public void exit() {
		super.exit();
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		GraphicModule.instance().setDrawSpace(DrawSpace.WORLD);
		hello.draw(0, 0, 1, 1);
		super.draw();
	}
}
