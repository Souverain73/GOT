package got.gameObjects;

import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;

public class TextObject extends AbstractGameObject{
	Font font = new Font("test");
	Text text;
	
	public TextObject(String message) {
		text = Text.newInstance(message, font);
	}
	
	@Override
	public void update(GameState state) {
		// TODO Auto-generated method stub
		super.update(state);
	}
	
	@Override
	public void draw(GameState state) {
		super.draw(state);
		Vector2f cp = getPos();
		GraphicModule.instance().setDrawSpace(getSpace());
		text.draw(cp.x, cp.y, 1, 1);
	}
}
