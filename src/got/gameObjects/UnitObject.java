package got.gameObjects;

import java.util.Vector;
import java.util.function.Predicate;

import org.joml.Vector2f;

import got.Constants;
import got.gameStates.GameState;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.utils.LoaderParams;

/**
 * Implements battle Unit.
 * @author  ËÁËÎÓ‚Ãﬁ
 *
 */
public class UnitObject extends AbstractGameObject<UnitObject>{
	public enum UnitType{
		SOLDIER, KNIGHT, SIEGE, SHIP
	}
	
	private Vector2f pos;
	private float 	scale;
	private float 	size;
	protected Texture tex;
	protected static final String TEXTURE_BASE = "units/";
	
	public UnitType type;
	
	@Override
	protected UnitObject getThis() {
		return this;
	}

	
	public static UnitObject getUnitByType(UnitType type){
		UnitObject result = new UnitObject(null);
		switch(type){
		case SOLDIER: 	result = result.new Soldier(); break;
		case KNIGHT:	result = result.new Knight(); break;
		case SHIP:		result = result.new Ship(); break;
		case SIEGE:		result = result.new Siege(); break;
		}
		result.setVisible(false);
		return result;
	}
	
	public static Vector<UnitObject> getUnitsByCondition(Predicate<UnitObject> cond){
		Vector<UnitObject> result = new Vector<UnitObject>();
		UnitType[] types = UnitType.values();
		for (UnitType type : types) {
			UnitObject unit = UnitObject.getUnitByType(type);
			if (cond.test(unit)){
				result.add(unit);
			}
		}
		return result;
	}
	
	protected UnitObject(UnitType type){
		this.type = type;
		pos = new Vector2f(0,0);
		scale = Constants.UNIT_SCALE;
		size = Constants.UNIT_SIZE;
	}
	
	@Override
	public void finish() {
	
	}

	@Override
	public boolean init(LoaderParams params) {
		return false;
	}

	@Override
	public void draw(GameState st) {
		if (isVisible()){
			tex.draw(pos.x, pos.y, size, size, Constants.UNIT_Z);
		}
	}

	@Override
	public void update(GameState st) {
		
	}
	
	public Vector2f getPos() {
		return pos;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public int getDamage(){
		return 0;
	}
	
	public int getCost(){
		return 0;
	}
	
	public UnitType getType(){
		return type;
	}
	
	public boolean isUpgradeable(){
		return false;
	}
	
	public Vector<UnitObject> getPossibleUpgrades(){
		return null;
	}
	
	
	public Texture getTexture() {
		return tex;
	}

	public void setTexture(Texture tex) {
		this.tex = tex;
	}



	private class Soldier extends UnitObject{

		public Soldier() {
			super(UnitType.SOLDIER);
			tex = TextureManager.instance().loadTexture(TEXTURE_BASE+"soldier.png");
		}
		@Override
		public int getDamage() {
			return 1;
		}
		
		@Override
		public int getCost() {
			return 1;
		}
		
		@Override
		public boolean isUpgradeable() {
			return true;
		}
		
		@Override
		public Vector<UnitObject> getPossibleUpgrades() {
			Vector<UnitObject> result = new Vector<UnitObject>();
			result.add(UnitObject.getUnitByType(UnitType.KNIGHT));
			result.add(UnitObject.getUnitByType(UnitType.SIEGE));
			return result;
		}
	}
	
	private class Knight extends UnitObject{

		public Knight() {
			super(UnitType.KNIGHT);
			tex = TextureManager.instance().loadTexture(TEXTURE_BASE+"knight.png");
		}
		@Override
		public int getDamage() {
			return 2;
		}
		
		@Override
		public int getCost() {
			return 2;
		}
	}
	
	private class Ship extends UnitObject{

		public Ship() {
			super(UnitType.SHIP);
			tex = TextureManager.instance().loadTexture(TEXTURE_BASE+"ship.png");
		}
		@Override
		public int getDamage() {
			return 1;
		}
		
		@Override
		public int getCost() {
			return 1;
		}
	}
	
	private class Siege extends UnitObject{

		public Siege() {
			super(UnitType.SIEGE);
			tex = TextureManager.instance().loadTexture(TEXTURE_BASE+"siege.png");
		}
		@Override
		public int getDamage() {
			return 4;
		}
		
		@Override
		public int getCost() {
			return 2;
		}
	}

	@Override
	public String toString() {
		return "UnitObject [pos=" + pos + ", scale=" + scale + ", size=" + size + ", \ntex=" + tex + ",\ntype=" + type
				+ ", Upgradeable="+ isUpgradeable() +"]";
	}
}
