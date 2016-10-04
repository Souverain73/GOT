package DGE.gameObjects;

import DGE.gameStates.GameState;
import DGE.graphics.DrawSpace;
import DGE.graphics.Font;
import DGE.graphics.Text;

public class FPSCounterObject extends AbstractGameObject{
	private Text tFPS;
	
	private long counter;
	private long lastUpdate;
	
	public FPSCounterObject() {
		tFPS = Text.newInstance("0", new Font("test"));
		lastUpdate = System.currentTimeMillis();
	}
	
	@Override
	public void update(GameState state) {
		super.update(state);
		counter++;
		if (System.currentTimeMillis() - lastUpdate > 1000){
			tFPS.changeText("FPS:"+String.valueOf(counter));
			lastUpdate = System.currentTimeMillis();
			counter = 0;
		}
	}
	
	@Override
	public void draw(GameState state) {
		super.draw(state);
		tFPS.draw(32, 32, 1, 1, DrawSpace.SCREEN);
	}
}
