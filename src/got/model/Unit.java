package got.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import got.graphics.Texture;
import got.graphics.TextureManager;

public enum Unit {
	KNIGHT(2, 2, "knight.png"),
	SIEGE(2, 4, "siege.png"),
	SHIP(1, 1, "ship.png"),
	SOLDIER(1, 1, "soldier.png", KNIGHT, SIEGE);
	
	private static final String TEXTURE_BASE = "units/";
	private final int cost;
	private final int damage;
	private final String textureName;
	private Texture texture = null;
	private final Unit[] upgrades;
	
	private Unit(int cost, int damage, String textureName, Unit ...args){
		this.cost = cost;
		this.damage = damage;
		this.textureName = textureName;
		upgrades = args;
	}
	
	public int getCost(){
		return cost;
	}

	public int getDamage() {
		return damage;
	}

	public Texture getTexture() {
		if (texture == null){
			this.texture = TextureManager.instance().loadTexture(TEXTURE_BASE+textureName);
		}
		return texture;
	}
	
	public Unit[] getPosibleUpgrades(){
		return upgrades;
	}
	
	public static Unit[] getUnitsByCondition(Predicate<Unit> condition){
		List<Unit> result = new ArrayList<>();
		for(Unit unit: Unit.values()){
			if(condition.test(unit)){
				result.add(unit);
			}
		}
		
		return result.toArray(new Unit[0]);
	}

	@Override
	public String toString() {
		return "Unit{textureName='" + textureName + "\'} ";
	}
}
