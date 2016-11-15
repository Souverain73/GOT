package got.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;

import got.Constants;
import got.gameStates.GameState;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.utils.LoaderParams;
import got.utils.Utils;

/**
 * Game object represents game action.
 * @author Souverain73
 *
 */
public class ActionObject extends AbstractButtonObject<ActionObject>{
	public enum Action{
		FIRE, FIREPLUS, MONEY, MONEYPLUS, MOVE, MOVEMINUS, MOVEPLUS, DEFEND, DEFENDPLUS, HELP, HELPPLUS 
	}

	private float radius;
	private float scale;	
	private static final String TEXTURE_BASE = "actions/";
	
	protected int modifier;
	public Texture texture;
	protected Action type;
	private Object owner;

	public static ActionObject getActionObject(Action type){
		ActionObject result = new ActionObject();
		switch (type){
		case FIRE: result = result.new FireAction(0); break;
		case FIREPLUS: result = result.new FireAction(1); break;
		case MONEY: result = result.new MoneyAction(0); break;
		case MONEYPLUS: result = result.new MoneyAction(1); break;
		case MOVE: result = result.new MoveAction(0); break;
		case MOVEMINUS: result = result.new MoveAction(-1); break;
		case MOVEPLUS: result = result.new MoveAction(1); break;
		case DEFEND: result = result.new DefendAction(1); break;
		case DEFENDPLUS: result = result.new DefendAction(2); break;
		case HELP: result = result.new HelpAction(0); break;
		case HELPPLUS: result = result.new HelpAction(1); break;
		}
		
		return result;
	}
	
	public static Vector<ActionObject> getAllActionObjects(){
		Action[] actions= Action.values();
		Vector<ActionObject> result= new Vector<ActionObject>();
		
		for (int i = 0; i < actions.length; i++) {
			result.add(getActionObject(actions[i]));
		}
		
		return result;
	}

	
	private ActionObject() {
		scale = 1;
		radius = Constants.ACTION_IMAGE_SIZE/2;
		pos = new Vector2f(0,0);
	}
	
	protected ActionObject(int modif) {
		this();
		modifier = modif;
	}
	
	@Override
	public boolean init(LoaderParams params) {
		return true;
	}

	@Override
	public void draw(GameState st) {
		if (state == State.HOVER){
			GraphicModule.instance().setEffect(
					new Effect(new Vector3f(0, 0.5f, 0), null, null));
		}
		Vector2f cx = getPos();
		texture.draw(cx.x-radius*scale, cx.y-radius*scale, 
				radius*2*scale, 
				radius*2*scale, 0.5f);		
		GraphicModule.instance().resetEffect();
	}

	
	
	@Override
	public void update(GameState st) {
		super.update(st);
	}

	public void doAction(){
		
	}
	
	public boolean isSpecial(){
		return false;
	}
	
	public Action getType(){
		return type;
	}
	
	public void setPosition(Vector2f pos){
		this.pos = pos;
	}
	
	public Vector2f getPosition() {
		return pos;
	}
	
	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	//IClickable
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.distance(mousePos, new Vector2f(pos.x,
				pos.y))<radius*scale) return true;
		return false;
	}
			
	@Override
	public int getPriority() {
		return 1;
	}

	private class FireAction extends ActionObject{
		public FireAction(int modif) {
			super(modif);
			if (modif == 1){
				type = Action.FIREPLUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"fire_1.png");
			}else{
				type = Action.FIRE;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"fire_0.png");
			}
		}
		
		@Override
		public boolean isSpecial() {
			if (modifier == 1) return true;
			else return false;
		}
	}
	
	private class MoneyAction extends ActionObject{
		public MoneyAction(int modif) {
			super(modif);
			if (modif == 1){
				type = Action.MONEYPLUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"money_1.png");
			}else{
				type = Action.MONEY;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"money_0.png");
			}
		}
		
		@Override
		public boolean isSpecial() {
			if (modifier == 1) return true;
			else return false;
		}
	}
	
	private class MoveAction extends ActionObject{
		protected MoveAction(int modif) {
			super(modif);
			if (modif == 1){
				type = Action.MOVEPLUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"move_2.png");
			}else if(modif == 0){
				type = Action.MOVE;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"move_1.png");
			}else{
				type = Action.MOVEMINUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"move_0.png");
			}
		}
		
		@Override
		public boolean isSpecial() {
			if (modifier == 1) return true;
			else return false;
		}
	}
	
	private class DefendAction extends ActionObject{
		public DefendAction(int modif) {
			super(modif);
			if (modif == 2){
				type = Action.DEFENDPLUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"defence_1.png");
			}else{
				type = Action.DEFEND;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"defence_0.png");
			}
		}
		
		@Override
		public boolean isSpecial() {
			if (modifier == 2){ 
				return true;
			}else{ 
				return false;
			}
		}
	}
	
	private class HelpAction extends ActionObject{
		public HelpAction(int modif) {
			super(modif);
			if (modif == 1){
				type = Action.HELPPLUS;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"help_1.png");
			}else{
				type = Action.HELP;
				texture = TextureManager.instance().loadTexture(TEXTURE_BASE+"help_0.png");
			}
		}
		
		@Override
		public boolean isSpecial() {
			if (modifier == 1) return true;
			else return false;
		}
	}

	public Object getOwner() {
		return owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	@Override
	protected ActionObject getThis() {
		return this;
	}
}