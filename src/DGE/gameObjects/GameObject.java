package DGE.gameObjects;

import DGE.gameStates.GameState;
import DGE.utils.LoaderParams;

public interface GameObject {
	public boolean init(LoaderParams params);
	public void draw(GameState state);
	public void update(GameState state);
	public void finish();
}
