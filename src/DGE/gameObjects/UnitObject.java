package DGE.gameObjects;

import DGE.gameStates.GameState;
import DGE.utils.LoaderParams;

public class UnitObject implements GameObject{
	public enum UnitType{
		SOLDIER, KNIGHT, SIEGE, SHIP
	}
	
	public UnitType type;
	
	public UnitObject(UnitType type){
		this.type = type;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameState st) {
		// TODO Auto-generated method stub
		
	}

}
