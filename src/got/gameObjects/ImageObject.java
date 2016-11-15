package got.gameObjects;

import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.GraphicModule;
import got.graphics.Texture;

/**
 * Implements simple image.
 * @author  изиловћё
 *
 */
public class ImageObject extends AbstractGameObject<ImageObject>{
	private Texture tex;
	
	@Override
	protected ImageObject getThis() {
		return this;
	}
	
	public ImageObject(Texture tex, Vector2f pos, int w, int h) {
		super();
		this.pos = pos;
		this.w = w;
		this.h = h;
		this.tex = tex;
	}
	
	public ImageObject(String textureName, Vector2f pos, int w, int h){
		
	}
		
	@Override
	public void draw(GameState state) {
		if (!isVisible()) return;
		GraphicModule.instance().setDrawSpace(this.space);
		Vector2f cp = getPos();
		tex.draw(cp.x, cp.y, w, h, 0);
		super.draw(state);
	}
	
	public void setTexture(Texture texture){
		this.tex = texture;
	}
}
