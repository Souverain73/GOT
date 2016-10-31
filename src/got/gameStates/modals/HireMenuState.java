package got.gameStates.modals;

import java.util.Vector;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.ModalState;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.UnitObject;
import got.gameObjects.UnitObject.UnitType;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.graphics.TextureManager;
import got.server.GameServer.PlayerConnection;

public class HireMenuState implements GameState{
	private static final String name = "HireMenu";
	private int hirePoints;
	private Vector<GameObject> objects;
	private Vector<UnitObject> units = null;
	private GameObject plusButton = null;
	private ImageButton [] buttons;
	private Vector2f pos;
	private boolean sea;
	
	public HireMenuState(Vector<UnitObject> units, Vector2f pos, int hirePoints, boolean sea) {
		this.pos = pos;
		this.sea = sea;
		this.hirePoints = hirePoints;
		this.units = units;
		this.buttons = new ImageButton[4];
		objects = new Vector<GameObject>();
		//TODO create BG
		float x = pos.x;
		float y = pos.y;
		
			ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
					new Vector2f(x,y), 220, 60);
			objects.add(bg);
			
		x = pos.x + 6;
		y = pos.y + 5;
		
		int step = (int)(Constants.UNIT_SIZE + Constants.UNIT_STEP);
		int unitsCount = units.size();
		for (int i=0; i<4; i++){
			int cx = (int)(x+step*i);
			ImageButton btn = new ImageButton(TextureManager.instance().loadTexture("blank.png"), 
					cx, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					i);
			btn.setCallback(this::unitClickCallback);
			btn.setVisible(false);
			objects.add(btn);
			buttons[i] = btn;
		}
		if (unitsCount<4){
			int cx = (int)(x+step*3);
			ImageButton btn = new ImageButton(TextureManager.instance().loadTexture("plus.png"), 
					cx, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					null);
			btn.setCallback(this::plusButtonCallback);
			plusButton = btn;
			objects.add(btn);
		}
		updateButtons();
	}
	
	private void updateButtons(){
		int unitsCount = units.size();
		for (int i=0; i<4; i++){
			if (i>=unitsCount){
				buttons[i].setVisible(false);
			}else{
				buttons[i].setTexture(units.get(i).getTexture());
				buttons[i].setVisible(true);
			}
		}
	}
	
	
	private void unitClickCallback(GameObject sender, Object param){
		int i = (Integer)param;
		UnitObject unit = units.get(i);
		if (unit.isUpgradeable() && hirePoints>0){
			hideObjects();
			Vector<UnitObject> upUnits = unit.getPossibleUpgrades();
			UnitSelectState ust = new UnitSelectState(upUnits, pos); 
			(new ModalState(ust)).run();
			if (ust.result!=null){
				UnitObject newUnit = (UnitObject)ust.result;
				units.set(i, newUnit);
				hirePoints--;
			}
			showObjects();
			updateButtons();
		}
	}
	
	private void plusButtonCallback(GameObject sender, Object param){
		hideObjects();
		UnitSelectState ust = new UnitSelectState(UnitObject.getUnitsByCondition(unit->{
			if (unit.getCost()<=hirePoints){
				if (sea && unit.getType()==UnitType.SHIP){
					return true;
				}else if(!sea && unit.getType()!=UnitType.SHIP){
					return true;
				}
			}
			return false;
		}),
				pos);
		(new ModalState(ust)).run();
		if (ust.result!=null){
			UnitObject unit = (UnitObject)ust.result;
			hirePoints-=unit.getCost();
			units.add(unit);
		}
		showObjects();
		updateButtons();
	}
	
	public void close(){
		GameClient.instance().closeModal();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		
	}

	@Override
	public void exit() {
		objects.forEach(obj->obj.finish());
	}

	@Override
	public void draw() {
//		System.out.println("Draw HireMenu");
		objects.forEach(obj->obj.draw(this));
	}
	
	private void hideObjects(){
		objects.forEach(obj->obj.setVisible(false));
	}
	
	private void showObjects(){
		objects.forEach(obj->obj.setVisible(true));
	}

	@Override
	public void update() {
		if (hirePoints == 0) close();
		if (plusButton != null && (hirePoints<=0 || units.size()>=4)){
			objects.remove(plusButton);
			plusButton.finish();
			plusButton = null;
		}
		objects.forEach(obj->obj.update(this));
	}

	public int getHirePoints() {
		return hirePoints;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return -1;
	}
	
}
