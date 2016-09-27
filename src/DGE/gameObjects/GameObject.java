package DGE.gameObjects;


import DGE.gameStates.GameState;
import DGE.utils.LoaderParams;

/**
 * Interface with all GameObject methods.
 * @author Souverain73
 *
 */
public interface GameObject{
	public boolean init(LoaderParams params);
	public void draw(GameState state);
	public void update(GameState state);
	public void finish();
	public boolean isVisible();
	public void setVisible(boolean visible);
}
