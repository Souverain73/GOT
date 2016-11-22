package got.gameStates.modals;

import got.GameClient;
import got.gameObjects.GameObject;
import got.gameStates.AbstractGameState;
import got.interfaces.IClickListener;

/**
 * CustomModalState is a class for creating modal game states in runtime;<br>
 * Used for custom dialogs;<br>
 * For use this state create an instance and add GameObjects to it;<br>
 * @param <T> - type of result object;
 */

public class CustomModalState<T> extends AbstractGameState implements IClickListener{
	private T result;

	public CustomModalState(T defaultResult){
		super();
		this.result = defaultResult;
	}

	@Override
	public void click(GameObject sender) {
		if (sender == null){
			GameClient.instance().closeModal();
		}	
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}