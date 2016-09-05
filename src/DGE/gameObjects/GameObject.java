package DGE.gameObjects;

import DGE.utils.LoaderParams;

public interface GameObject {
	public boolean init(LoaderParams params);
	public void draw();
	public void update();
}
