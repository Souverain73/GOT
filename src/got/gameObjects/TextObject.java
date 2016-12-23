package got.gameObjects;

import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;

public class TextObject extends AbstractGameObject<TextObject>{
	Font font = new Font("test");
	Text text;
	String currentText;
	
	@Override
	protected TextObject getThis() {
		return this;
	}

	
	public TextObject(String message) {
		text = Text.newInstance(message, font);
		currentText = message;
	}
	
	@Override
	public void update(GameState state) {
		super.update(state);
	}
	
	@Override
	public void draw(GameState state) {
		super.draw(state);
		Vector2f cp = getPos();
		GraphicModule.instance().setDrawSpace(getSpace());
		text.draw(cp.x, cp.y, 1, 1);
	}
	
	public void setText(String newText){
		if (!currentText.equals(newText))
			text.changeText(newText);
	}
}
