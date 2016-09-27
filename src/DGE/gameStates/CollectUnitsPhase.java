package DGE.gameStates;

import java.util.HashMap;
import java.util.Vector;

import DGE.InputManager;
import DGE.ModalState;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.MapPartObject;
import DGE.gameObjects.MapPartObject.RegionType;
import DGE.gameStates.modals.HireMenuState;

public class CollectUnitsPhase extends ActionPhase{
	private Vector<GameObject> objects;
	private HashMap<String, Integer> hirePointsCache;
	
	CollectUnitsPhase(){
		hirePointsCache = new HashMap<String, Integer>();
	}
	
	@Override
	public void click(GameObject sender) {
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject)sender;
			String regName = region.getName();
			Integer hirePoints = hirePointsCache.get(regName);
			if (hirePoints == null){
				hirePoints = region.getHirePoints();
			}
			System.out.println("Click region. Hire points:"+hirePoints);
			if (hirePoints>0){
				//TODO create hire menu and check if units alredy hired
				HireMenuState hms = new HireMenuState(region.getUnits(), InputManager.instance().getMousePosWorld(), hirePoints); 
				region.hideUnits();
				(new ModalState(hms)).run();
				region.updateUnits();
				hirePointsCache.put(regName, hms.getHirePoints());
				region.showUnits();
			}
		}
	}
	
	
}
