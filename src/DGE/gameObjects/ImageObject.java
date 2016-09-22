package DGE.gameObjects;

import org.joml.Vector2f;

import DGE.gameStates.GameState;
import DGE.graphics.Texture;

public class ImageObject extends AbstractGameObject{
	private Texture tex;
	
	public ImageObject(Texture tex, Vector2f pos, int w, int h) {
		super();
		this.pos = pos;
		this.w = w;
		this.h = h;
		this.tex = tex;
	}
		
	@Override
	public void draw(GameState state) {
		if (!isVisible()) return;
		Vector2f cp = getPos();
		tex.draw(cp.x, cp.y, w, h, 0);
		super.draw(state);
	}
}
