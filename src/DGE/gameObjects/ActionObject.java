package DGE.gameObjects;

import java.util.Vector;

import DGE.gameStates.GameState;
import DGE.graphics.Texture;
import DGE.graphics.TextureManager;
import DGE.utils.LoaderParams;

public class ActionObject implements GameObject{
	public enum Action{
		FIRE, FIREPLUS, MONEY, MONEYPLUS, MOVE, MOVEMINUS, MOVEPLUS, DEFEND, DEFENDPLUS, HELP, HELPPLUS 
	}

	private static final String TEXTURE_BASE = "actions/";
	
	protected int modifier;
	public Texture texture;
	protected Action type;

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
	}
	
	protected ActionObject(int modif) {
		modifier = modif;
	}
	
	@Override
	public boolean init(LoaderParams params) {
		return true;
	}

	@Override
	public void draw(GameState st) {
		
	}

	@Override
	public void update(GameState st) {
	}
	
	public void draw(float x, float y, float scale){
		texture.draw(x, y, scale);
	}

	public void doAction(){
		
	}
	
	public boolean isSpecial(){
		return false;
	}
	
	public Action getType(){
		return type;
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
}