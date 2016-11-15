package got.gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.utils.Utils;

/**
 * Extends {@link AbstractButtonObject} with image.<br>
 * For usage look {@link AbstractButtonObject}.
 * @author  ËÁËÎÓ‚Ãﬁ
 *
 */
public class ImageButton extends AbstractButtonObject<ImageButton> {
	private Texture texture;
	private Object param;
	
	@Override
	protected ImageButton getThis() {
		return this;
	}

	
	private ImageButton(int x, int y, int w, int h, Object param){
		super();
		pos.x = x;
		pos.y = y;
		this.w = w;
		this.h = h;
		this.param = param;
	}
	
	public ImageButton(Texture tex, int x, int y, float scale, Object param){
		this(x,y, (int)(tex.getWidth()*scale), (int)(tex.getHeight()*scale), param);
		texture = tex;
	}
	
	
	public ImageButton(String textName, int x, int y, int w, int h, Object param) {
		this(x,y,w,h,param);
		texture = TextureManager.instance().loadTexture(textName);
	}

	public ImageButton(Texture tex, int x, int y, int w, int h, Object param) {
		this(x,y,w,h,param);
		texture = tex;
	}
	
	@Override
	public void draw(GameState st) {
		GraphicModule.instance().setDrawSpace(space);
		if (!isVisible()) return;
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			setOverlay(new Vector3f(0.0f, 0.5f, 0.0f));
		}else if (state == State.DOWN){
			setOverlay(new Vector3f(0.5f, 0.0f, 0.0f));
		}else if (state == State.DISABLED){
			setOverlay(new Vector3f(-0.5f, -0.5f, -0.5f));
		}
		
		texture.draw(getPos().x, getPos().y, w, h, 1);
		super.draw(st);
		
		GraphicModule.instance().resetEffect();
	}
	//IClickable
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(mousePos,
				getPos(),
				new Vector2f(w, h))
		){
			if (texture.getAlfa((mousePos.x - getPos().x)/w, (mousePos.y - getPos().y)/h)!=0) return true; 
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 2;
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
	protected void click(GameState st) {
		System.out.println("Click button; CurrentState:"+st.getName());
		if (callback != null){
			callback.accept(this, param);
		}
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	
	
	
	
}