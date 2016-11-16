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
public class UnitObjectOld extends AbstractGameObject<UnitObjectOld>{
	public enum UnitType{
		SOLDIER, KNIGHT, SIEGE, SHIP
	}
	
	private float 	scale;
	private float 	size;
	protected Texture tex;
	protected static final String TEXTURE_BASE = "units/";
	
	public UnitType type;
	
	@Override
	protected UnitObjectOld getThis() {
		return this;
	}

	
	public static UnitObjectOld getUnitByType(UnitType type){
		UnitObjectOld result = new UnitObjectOld(null);
		switch(type){
		case SOLDIER: 	result = result.new Soldier(); break;
		case KNIGHT:	result = result.new Knight(); break;
		case SHIP:		result = result.new Ship(); break;
		case SIEGE:		result = result.new Siege(); break;
		}
		result.setVisible(false);
		return result;
	}
	
	public static Vector<UnitObjectOld> getUnitsByCondition(Predicate<UnitObjectOld> cond){
		Vector<UnitObjectOld> result = new Vector<UnitObjectOld>();
		UnitType[] types = UnitType.values();
		for (UnitType type : types) {
			UnitObjectOld unit = UnitObjectOld.getUnitByType(type);
			if (cond.test(unit)){
				result.add(unit);
			}
		}
		return result;
	}
	
	protected UnitObjectOld(UnitType type){
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

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getSize() {
		return size;
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
	
	public Vector<UnitObjectOld> getPossibleUpgrades(){
		return null;
	}
	
	
	public Texture getTexture() {
		return tex;
	}

	public void setTexture(Texture tex) {
		this.tex = tex;
	}



	private class Soldier extends UnitObjectOld{

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
		public Vector<UnitObjectOld> getPossibleUpgrades() {
			Vector<UnitObjectOld> result = new Vector<UnitObjectOld>();
			result.add(UnitObjectOld.getUnitByType(UnitType.KNIGHT));
			result.add(UnitObjectOld.getUnitByType(UnitType.SIEGE));
			return result;
		}
	}
	
	private class Knight extends UnitObjectOld{

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
	
	private class Ship extends UnitObjectOld{

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
	
	private class Siege extends UnitObjectOld{

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
