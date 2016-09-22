package DGE.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import DGE.Constants;
import DGE.Game;
import DGE.InputManager;
import DGE.gameObjects.AbstractButtonObject.State;
import DGE.gameStates.ActionPhase;
import DGE.gameStates.GameState;
import DGE.gameStates.PlanningPhase;
import DGE.graphics.Effect;
import DGE.graphics.GraphicModule;
import DGE.graphics.Texture;
import DGE.utils.LoaderParams;
import DGE.utils.Utils;
import sun.management.counter.Units;

public class MapPartObject extends AbstractButtonObject {
	private String name;
	private int resourcesCount;
	private int influencePoints;
	private int buildingLevel;
	private Texture texture;
	private Vector<MapPartObject>  neighbors;
	private int w, h;
	private int act_x, act_y;
	private int unit_x, unit_y;
	private ActionObject action;
	private Vector<UnitObject> units;
	boolean overlay;
	
	public MapPartObject() {
		super();
		neighbors = new Vector<MapPartObject>();
		units = new Vector<UnitObject>();
	}
	
	@Override
	public boolean init(LoaderParams params) {
		name = (String)params.get("name");
		resourcesCount = (Integer)params.get("resources");
		influencePoints = (Integer)params.get("influence");
		buildingLevel = (Integer)params.get("building");
		texture = (Texture)params.get("texture");
		pos.x = (Integer)params.get("x");
		pos.y = (Integer)params.get("y");
		w = (Integer)params.get("w");
		h = (Integer)params.get("h");
		act_x = (Integer)params.get("action_x");
		act_y = (Integer)params.get("action_y");
		unit_x = (Integer)params.get("unit_x");
		unit_y = (Integer)params.get("unit_y");
		placeUnits();
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
		
		Vector2f cp = getPos();
		texture.draw(cp.x, cp.y, w, h, 0);
		GraphicModule.instance().resetEffect();
		if (action != null){
			action.draw(st);
		}
		
		if (units != null){
			units.forEach(unit->unit.draw(st));
		}
		super.draw(st);
	}

	public Vector<MapPartObject> getNeighbors(){
		return neighbors;
	}
	
	public String getName(){
		return name;
	}
	
	
	
	@Override
	public void update(GameState st) {
		if (action != null){
			action.update(st);
		}
		super.update(st);
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

	public void addUnit(UnitObject unit){
		if (units.size()>=4){
			System.out.println("Can't add more than 4 units for one region");
			return;
		}
		units.add(unit);
		unit.setVisible(true);
		placeUnits();
	}
	
	public void updateUnits(){
		placeUnits();
	}
	
	private void placeUnits(){
		Vector2f cp = getPos();
		float x = cp.x+unit_x;
		float y = cp.y+unit_y;
		float angle = 0;
		float radius = Constants.UNIT_SIZE*Constants.UNIT_SCALE*0.7f;
		float step = 0;
		
		if (units.size() == 0) return;
		
		if (units.size() == 1){
			units.get(0).setPos(new Vector2f(x,y));
		}else{
			switch(units.size()){
			case 2: angle = 0; break;
			case 3: angle = -90; break;
			case 4: angle = 45; break;
			}
			angle = (float)Math.toRadians(angle);
			step = (float)(360.0f/units.size() * Math.PI/180.0f);
			for(int i=0; i<units.size(); i++){
				units.get(i).setPos(new Vector2f((float)(x+Math.cos(angle)*radius), (float)(y+Math.sin(angle)*radius)));
				angle+=step;
			}
		}
	}
	
	public void setAction(ActionObject act){
		if (act!=null){
			act.setPosition(new Vector2f(getPos().x+act_x, getPos().y+act_y));
		}
		action = act;
	}
	
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		if (Utils.pointInRect(InputManager.instance().getMousePosWorld(), getPos(), new Vector2f(w,h))){
			Vector2f worldPos = InputManager.instance().getMousePosWorld();
			Vector2f cp = getPos();
			Vector2f modPos = new Vector2f(worldPos.x-cp.x, worldPos.y-cp.y);
			if (texture.getAlfa(modPos.x/w, modPos.y/h) != 0){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	public int getHirePoints(){
		return buildingLevel;
	}
	
	public Vector<UnitObject> getUnits() {
		return units;
	}

	private void setOverlay(Vector3f overlay){
		Effect eff = new Effect();
		eff.overlay = overlay;
		GraphicModule.instance().setEffect(eff);
	}

	@Override
	protected void click(GameState st) {
		System.out.println("Click region:"+name+" GameState:"+st.getName());
		System.out.println("UnitsSize="+units.size());
		super.click(st);
	}

	public ActionObject getAction() {
		return action;
	}
	
	public void hideUnits(){
		units.forEach(unit->unit.setVisible(false));
	}
	
	public void showUnits(){
		units.forEach(unit->unit.setVisible(true));
	}
}
