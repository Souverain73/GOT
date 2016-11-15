package got.gameObjects;


import got.gameStates.GameState;
import got.utils.LoaderParams;

/**
 * Interface with all GameObject methods.
 * @author Souverain73
 *
 */
public interface GameObject<T extends GameObject<T>>{
	public boolean init(LoaderParams params);
	public void draw(GameState state);
	public void update(GameState state);
	public void finish();
	public boolean isVisible();
	public void setVisible(boolean visible);
}
