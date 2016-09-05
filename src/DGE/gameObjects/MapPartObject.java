package DGE.gameObjects;

import java.util.Vector;

import DGE.graphics.Texture;
import DGE.utils.LoaderParams;

public class MapPartObject implements GameObject {
	private String name;
	private int resourcesCount;
	private int influencePoints;
	private int buildingLevel;
	private Texture texture;
	private Vector<MapPartObject>  neighbors;
	private int x, y, w, h;
	private ActionObject action;
	
	@Override
	public boolean init(LoaderParams params) {
		name = (String)params.get("name");
		resourcesCount = (Integer)params.get("resources");
		influencePoints = (Integer)params.get("influence");
		buildingLevel = (Integer)params.get("building");
		texture = (Texture)params.get("texture");
		x = (Integer)params.get("x");
		y = (Integer)params.get("y");
		w = (Integer)params.get("w");
		h = (Integer)params.get("h");
		
		neighbors = new Vector<MapPartObject>();
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw() {
		texture.draw(x, y, w, h);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
	
	
	public Vector<MapPartObject> getNeighbors(){
		return neighbors;
	}
	
	public String getName(){
		return name;
	}
	
	//Метод добавляющий соседа.
	public void addNeighbor(MapPartObject neighbor){
		if (neighbors.indexOf(neighbor)!=-1){
			//если сосед уже есть то делать ничего не надо
			return;
		}else{
			//Если соседа нет, дбавляем его к соседям
			neighbors.add(neighbor);
			//Добавляем обратную связь
			neighbor.addNeighbor(this);
		}
	}
}
