package DGE.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import DGE.Game;
import DGE.InputManager;
import DGE.gameObjects.AbstractButtonObject.State;
import DGE.gameStates.GameState;
import DGE.gameStates.PlanningPhase;
import DGE.graphics.Effect;
import DGE.graphics.GraphicModule;
import DGE.graphics.Texture;
import DGE.utils.LoaderParams;
import DGE.utils.Utils;

public class MapPartObject extends AbstractButtonObject {
	private String name;
	private int resourcesCount;
	private int influencePoints;
	private int buildingLevel;
	private Texture texture;
	private Vector<MapPartObject>  neighbors;
	private int x, y, w, h;
	private ActionObject action;
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
	public void draw(GameState st) {
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			setOverlay(new Vector3f(0.0f, 0.5f, 0.0f));
		}else if (state == State.DOWN){
			setOverlay(new Vector3f(0.5f, 0.0f, 0.0f));
		}
		
		texture.draw(x, y, w, h);
		GraphicModule.instance().resetEffect();
		if (action != null){
			action.draw(x, y, 1);
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

	public void setAction(ActionObject act){
		action = act;
	}
	
	@Override
	protected boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(InputManager.instance().getMousePosWorld(), new Vector2f(x,y), new Vector2f(w,h))){
			Vector2f worldPos = InputManager.instance().getMousePosWorld();
			Vector2f modPos = new Vector2f(worldPos.x-x, worldPos.y-y);
			if (texture.getAlfa((int)modPos.x, (int)modPos.y) != 0){
				return true;
			}
		}
		return false;
	}
	
	private void setOverlay(Vector3f overlay){
		Effect eff = new Effect();
		eff.overlay = overlay;
		GraphicModule.instance().setEffect(eff);
	}

	@Override
	protected void click(GameState st) {
		System.out.println("Click region:"+name+" GameState:"+st.getName());
		if (st instanceof PlanningPhase){
			if (action == null){
				((PlanningPhase)st).placeAction(this);
			}else{
				((PlanningPhase)st).removeAction(this);
			}
		}
		super.click(st);
	}
	
}
