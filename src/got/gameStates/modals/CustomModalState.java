package got.gameStates.modals;

import got.GameClient;
import got.gameObjects.GameObject;
import got.gameStates.AbstractGameState;
import got.interfaces.IClickListener;

public class CustomModalState extends AbstractGameState implements IClickListener{
	
	@Override
	public void click(GameObject sender) {
		if (sender == null){
			GameClient.instance().closeModal();
		}	
	}
}
