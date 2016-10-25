package got.gameObjects;

import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.Text;

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
		tFPS.draw(10, 0, 1, 1, DrawSpace.SCREEN);
	}
}
