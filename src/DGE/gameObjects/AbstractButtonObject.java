package DGE.gameObjects;

import java.util.function.BiConsumer;

import org.joml.Vector2f;

import DGE.InputManager;
import DGE.gameStates.GameState;
import DGE.interfaces.IClickListener;
import DGE.interfaces.IClickable;
import DGE.utils.LoaderParams;

/**
 * 
 * Base class for all buttons
 * <h1>Usage:</h1>
 * <p>
 * 	All buttons have callback function.<br>
 * 	Callback function called when button is pressed.
 * <h2>Default callback:</h2>
 * 	Default callback tries to use iClickListener interface of current state.
 * 	If current state is click listener then click() method will be called.
 * </p>
 * @author Souverain73
 *
 */
public abstract class AbstractButtonObject extends AbstractGameObject implements IClickable{
	protected enum State {DOWN, FREE, HOVER, DISABLED};
	protected BiConsumer<GameObject, Object> callback;
	protected State state;
	protected boolean wasClick;
	protected boolean mouseIn;
	
	public AbstractButtonObject() {
		super();
		wasClick = false;
		state = State.FREE;
		InputManager.instance().registerClickable(this);
		callback = (sender, param)->{
			if (param instanceof IClickListener){
				((IClickListener)param).click(sender);
			}
		};
	}
	
	@Override
	public void finish() {
		InputManager.instance().removeClickable(this);
	}

	@SuppressWarnings("unchecked")
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
	public void update(GameState st) {
		if (state == State.DISABLED) return;
		
		if (state == State.FREE){
			if (mouseIn){
				mouseEnter();
				if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1){
					wasClick = true;
				}
			}
		}
		
		if (state == State.DOWN){
			if (!mouseIn){
				mouseOut();
				wasClick = false;
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==0){
				if (!wasClick){
					wasClick = true;
					click(st);
				}
				if (state!=State.DISABLED)
					state = State.HOVER;
			}
		}
		
		if (state == State.HOVER){
			if (!mouseIn){
				mouseOut();
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1){
				state = State.DOWN;
			}else{
				wasClick = false;
			}
		}
		super.update(st);
	}
	
	protected void mouseEnter(){
		state = State.HOVER;
	}
	
	protected void mouseOut(){
		state = State.FREE;
	}
	
	protected void click(GameState st){
		if (callback != null){
			callback.accept(this, st);
		}
	}
	
	public void setEnabled(boolean enabled){
		if (enabled){
			state = State.FREE;
		}else{
			state = State.DISABLED;
		}
	}
	
	//IClickable implementation
	public abstract boolean ifMouseIn(Vector2f mousePos);

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void setMouseIn(boolean mouseIn) {
		this.mouseIn = mouseIn;
	}

	@Override
	public boolean isActive() {
		return (state!=State.DISABLED && isVisible());
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (this.visible == visible) return;
		this.visible = visible;
		if (visible == true){
			InputManager.instance().registerClickable(this);
		}else{
			InputManager.instance().removeClickable(this);
		}
	}
}
