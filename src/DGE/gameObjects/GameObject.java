package DGE.gameObjects;

import org.joml.Vector2f;

import DGE.gameStates.GameState;
import DGE.interfaces.IComposer;
import DGE.utils.LoaderParams;

public interface GameObject extends IComposer<GameObject>{
	public boolean init(LoaderParams params);
	public void draw(GameState state);
	public void update(GameState state);
	public void finish();
	public Vector2f getPos();
	public boolean isVisible();
	public void setVisible(boolean visible);
}
