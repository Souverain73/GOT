package DGE.gameObjects;

import java.util.function.BiConsumer;

import org.joml.Vector2f;

import DGE.InputManager;
import DGE.gameStates.GameState;
import DGE.utils.LoaderParams;

public abstract class AbstractButtonObject implements GameObject{
	protected enum State {DOWN, FREE, HOVER};
	protected BiConsumer<GameObject, Object> callback;
	protected State state;
	protected boolean wasClick;
	
	public AbstractButtonObject() {
		wasClick = false;
		state = State.FREE;
	}
	
	@Override
	public boolean init(LoaderParams params) {
		if (params.containsKey("callback")){
			callback = (BiConsumer<GameObject, Object>)params.get("callback");
		}
		return true;
	}

	public AbstractButtonObject setCallback(BiConsumer<GameObject, Object> cb){
		callback = cb;
		return this;
	}
	
	@Override
	public void draw(GameState st) {
		
	}

	@Override
	public void update(GameState st) {
		if (state == State.FREE){
			if (ifMouseIn(InputManager.instance().getMousePosWorld())){
				mouseEnter();
				if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1){
					wasClick = true;
				}
			}
		}
		
		if (state == State.DOWN){
			if (!ifMouseIn(InputManager.instance().getMousePosWorld())){
				mouseOut();
				wasClick = false;
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==0){
				wasClick = false;
				state = State.HOVER;
			}
		}
		
		if (state == State.HOVER){
			if (!ifMouseIn(InputManager.instance().getMousePosWorld())){
				mouseOut();
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1){
				state = State.DOWN;
				if (!wasClick){
					wasClick = true;
					click(st);
				}
			}else{
				wasClick = false;
			}
		}		
	}
	
	protected void mouseEnter(){
		state = State.HOVER;
	}
	
	protected void mouseOut(){
		state = State.FREE;
	}
	
	protected void click(GameState st){
		if (callback != null){
			callback.accept(this, null);
		}
	}
	
	protected abstract boolean ifMouseIn(Vector2f mousePos);
}
