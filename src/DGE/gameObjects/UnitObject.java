package DGE.gameObjects;

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
	public boolean init(LoaderParams params) {
		return false;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
