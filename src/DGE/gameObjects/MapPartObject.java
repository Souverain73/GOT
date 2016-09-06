package DGE.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import DGE.Game;
import DGE.graphics.Effect;
import DGE.graphics.GraphicModule;
import DGE.graphics.Texture;
import DGE.utils.LoaderParams;
import DGE.utils.Utils;

public class MapPartObject implements GameObject {
	private String name;
	private int resourcesCount;
	private int influencePoints;
	private int buildingLevel;
	private Texture texture;
	private Vector<MapPartObject>  neighbors;
	private int x, y, w, h;
	private ActionObject action;
	private Vector4f overlayVec;
	boolean overlay;
	
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
		if (overlay){
			Effect eff = new Effect();
			eff.overlay = new Vector3f(0, 0.5f, 0);
			GraphicModule.instance().setEffect(eff);
		}
		texture.draw(x, y, w, h);
		GraphicModule.instance().resetEffect();
	}

	@Override
	public void update() {
		overlay = false;
		if (Utils.pointInRect(Game.instance().getWorldCursorPos(), new Vector2f(x,y), new Vector2f(w,h))){
			Vector2f worldPos = Game.instance().getWorldCursorPos();
			Vector2f modPos = new Vector2f(worldPos.x-x, worldPos.y-y);
			if (texture.getAlfa((int)modPos.x, (int)modPos.y) != 0){
				overlay=true;
			}
		}
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
