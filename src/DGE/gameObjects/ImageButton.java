package DGE.gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import DGE.graphics.Texture;
import DGE.graphics.TextureManager;
import DGE.utils.Utils;
import DGE.gameStates.GameState;
import DGE.graphics.Effect;
import DGE.graphics.GraphicModule;

public class ImageButton extends AbstractButtonObject {
	private Texture texture;
	private int x;
	private int y;
	private int w;
	private int h;
	private float scale;
	private Object param;
	
	public ImageButton(Texture tex, int x, int y, float scale, Object param){
		texture = tex;
		this.x = x;
		this.y = y;
		w = (int)(texture.getWidth()*scale);
		h = (int)(texture.getHeight()*scale);
		this.param=param;
	}
	
	public ImageButton(String textName, int x, int y, int w, int h, Object param) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.param = param;
		
		texture = TextureManager.instance().loadTexture(textName);
	}
	
	
	@Override
	public void draw(GameState st) {
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			setOverlay(new Vector3f(0.0f, 0.5f, 0.0f));
		}else if (state == State.DOWN){
			setOverlay(new Vector3f(0.5f, 0.0f, 0.0f));
		}else if (state == State.DISABLED){
			setOverlay(new Vector3f(-0.5f, -0.5f, -0.5f));
		}
		
		texture.draw(x, y, w, h, 1);
		super.draw(st);
		
		GraphicModule.instance().resetEffect();
	}
	//IClickable
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(mousePos,
				new Vector2f(x, y),
				new Vector2f(w, h))
		){
			if (texture.getAlfa((mousePos.x - x)/w, (mousePos.y - y)/h)!=0) return true; 
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
	
	
	
	
}