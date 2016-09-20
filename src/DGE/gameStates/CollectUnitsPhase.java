package DGE.gameStates;

import java.util.Vector;

import org.joml.Vector2f;

import DGE.Constants;
import DGE.InputManager;
import DGE.ModalState;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.ImageButton;
import DGE.gameObjects.MapPartObject;
import DGE.gameObjects.UnitObject;

public class CollectUnitsPhase extends ActionPhase{
	private Vector<GameObject> objects;
	@Override
	public void click(GameObject sender) {
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject)sender;
			System.out.println("Click region. Hire points:"+region.getHirePoints());
			if (region.getHirePoints()>0){
				//TODO create hire menu and check if units alredy hired
				(new ModalState(new HireMenuState(region.getUnits(), InputManager.instance().getMousePosWorld()))).run();
			}
		}
	}
	
	private class HireMenuState implements GameState{
		private static final String name = "HireMenu";
		
		public HireMenuState(Vector<UnitObject> units, Vector2f pos) {
			objects = new Vector<GameObject>();
			//TODO create BG
			//TODO create plus button icon
			float x = pos.x;
			float y = pos.y;
			System.out.println("Create hire menu at x:"+x+" y:"+y);
			for (int i=0; i<units.size(); i++){
				System.out.println("unit");
				ImageButton btn = new ImageButton(units.get(i).getTexture(), 
						(int)x, (int)y, 
						(int)Constants.UNIT_SIZE,
						(int)Constants.UNIT_SIZE,
						units.get(i));
				btn.setCallback(this::hireMenuClickCallback);
				objects.add(btn);
				x+=Constants.UNIT_SIZE + Constants.UNIT_STEP;
			}
		}
		
		
		private void hireMenuClickCallback(GameObject sender, Object param){
			System.out.println("Clicked: "+param.toString());
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
//			System.out.println("Draw HireMenu");
			objects.forEach(obj->obj.draw(this));
		}

		@Override
		public void update() {
			objects.forEach(obj->obj.update(this));
		}
		
	}
}
