package got.gameObjects;

import java.util.List;
import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector3f;

import got.Constants;
import got.Fraction;
import got.InputManager;
import got.gameStates.GameState;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.utils.LoaderParams;
import got.utils.Utils;

/**
 * Handling all map region data, and region interactions.<br>
 * Also extends {@link AbstractButtonObject}.
 * @author КизиловМЮ
 *
 */
public class MapPartObject extends AbstractButtonObject<MapPartObject> {
	public enum RegionType{	GROUND, SEA, PORT}
	
	@Override
	protected MapPartObject getThis() {
		return this;
	}

	
	private Fraction fraction = Fraction.STARK;
	
	private int ID = 0;
	private RegionType type;
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
	
	public MapPartObject() {
		super();
		neighbors = new Vector<MapPartObject>();
		units = new Vector<UnitObject>();
	}
	
	@Override
	public boolean init(LoaderParams params) {
		name = (String)params.get("name");
		ID = (Integer)params.get("id");
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
		type = RegionType.valueOf((String)params.get("type"));
		try{
			fraction = Fraction.valueOf((String)params.get("fraction"));
		}catch(Exception e){
			
		}
		if (fraction == null){
			fraction = Fraction.STARK;
		}
		placeUnits();
		return false;
	}

	public int getID(){
		return ID;
	}
	
	@Override
	public void draw(GameState st) {
		if (!isVisible()) return;
		GraphicModule.instance().setDrawSpace(this.space);
		if (state == State.FREE){
			
		}else if (state == State.HOVER){
			GraphicModule.instance().setEffect(
					new Effect().Overlay(new Vector3f(0.0f, 0.2f, 0.0f))
			);
		}else if (state == State.DOWN){
			GraphicModule.instance().setEffect(
					 new Effect().Overlay(new Vector3f(0.2f, 0.0f, 0.0f))
			);
		}

		if (state == State.DISABLED){
			GraphicModule.instance().setEffect(
					new Effect().Overlay(new Vector3f(-0.2f, -0.2f, -0.2f))
			);
		}
		
		Vector2f cp = getPos();
		texture.draw(cp.x, cp.y, w, h, 0);
		GraphicModule.instance().resetEffect();
		if (action != null){
			action.draw(st);
		}
		
		GraphicModule.instance().setEffect(
				new Effect().Multiply(fraction.getMultiplyColor())
		);
		if (units != null){
			units.forEach(unit->unit.draw(st));
		}
		GraphicModule.instance().resetEffect();
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
	
	public List<UnitObject> getUnits() {
		return units;
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

	public RegionType getType() {
		return type;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public void setFraction(Fraction fraction) {
		this.fraction = fraction;
	}

	public int getBuildingLevel() {
		return buildingLevel;
	}
	
	public int getInfluencePoints() {
		return influencePoints;
	}

	@Override
	public String toString() {
		return "MapPartObject [type=" + type + ", name=" + name + ", resourcesCount=" + resourcesCount
				+ ", influencePoints=" + influencePoints + ", buildingLevel=" + buildingLevel + ", w=" + w + ", h=" + h
				+ "]";
	}
	
	
}
