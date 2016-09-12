package DGE.gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import DGE.graphics.Texture;
import DGE.graphics.TextureManager;
import DGE.utils.Utils;
import DGE.graphics.Effect;
import DGE.graphics.GraphicModule;

public class ImageButton extends AbstractButtonObject {
	private Texture texture;
	private int x;
	private int y;
	private int w;
	private int h;
	
	public ImageButton(String textName, int x, int y, int w, int h) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		texture = TextureManager.instance().loadTexture(textName);
	}
	
	
	@Override
	public void draw() {
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			setOverlay(new Vector3f(0.0f, 0.5f, 0.0f));
		}else if (state == State.DOWN){
			setOverlay(new Vector3f(0.5f, 0.0f, 0.0f));
		}
		
		texture.draw(x, y, w, h);
		super.draw();
		
		GraphicModule.instance().resetEffect();
	}
	
	@Override
	protected boolean ifMouseIn(Vector2f mousePos) {
		return Utils.pointInRect(mousePos,
				new Vector2f(x, y),
				new Vector2f(w, h));
	}
	
	private void setOverlay(Vector3f overlay){
		Effect eff = new Effect();
		eff.overlay = overlay;
		GraphicModule.instance().setEffect(eff);
	}


	@Override
	protected void mouseEnter() {
		super.mouseEnter();
	}


	@Override
	protected void mouseOut() {
		super.mouseOut();
	}


	@Override
	protected void click() {
		System.out.println("Click button");
		super.click();
	}
	
	
	
	
}