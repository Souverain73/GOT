package got.gameStates;

import got.GameClient;
import got.gameObjects.interfaceControls.ImageButton;
import got.graphics.DrawSpace;

public class MenuState extends AbstractGameState {
	private final String name = "MenuState";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {		
		System.out.println("Entering "+name);
		//button play
		ImageButton btn = new ImageButton("buttons/play.png", 540, 260, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().getStateMachine().setState(new NetworkRoomState());
		});
		btn.setSpace(DrawSpace.SCREEN);
		addObject(btn);;
		//button exit
		btn = new ImageButton("buttons/exit.png", 540, 380, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().exit();
		});
		btn.setSpace(DrawSpace.SCREEN);
		addObject(btn);


		addObject(new ImageButton("map/winterfell.png", 100, 50, 200, 140, null)
		.setSpace(DrawSpace.SCREEN)
		.setCallback((sender, params)->{
			GameClient.instance().getStateMachine().setState(new MapViewState());
		}));
	}

	@Override
	public void exit() {
		super.exit();
		System.out.println("Exit "+name);
	}	
}
