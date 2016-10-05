package DGE.gameStates;

import java.util.HashMap;
import java.util.Vector;

import DGE.InputManager;
import DGE.ModalState;
import DGE.gameObjects.GameMapObject;
import DGE.gameObjects.GameObject;
import DGE.gameObjects.MapPartObject;
import DGE.gameObjects.MapPartObject.RegionType;
import DGE.gameStates.modals.HireMenuState;
import DGE.gameStates.modals.MessageBoxState;

public class CollectUnitsPhase extends ActionPhase {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private SubState state;
	private HashMap<String, Integer> hirePointsCache;

	private MapPartObject source; // Hire point source

	CollectUnitsPhase() {
		state = SubState.SELECT_SOURCE;
		hirePointsCache = new HashMap<String, Integer>();
	}

	@Override
	public void click(GameObject sender) {
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;

			if (state == SubState.SELECT_SOURCE) {
				int hirePoints = getHirePoints(region);
				System.out.println("Click region. Hire points:" + hirePoints);
				if (hirePoints > 0) {
					// check if units can be placed in neighboring regions
					Vector<MapPartObject> neighbors = region.getNeighbors();
					GameMapObject.instance().disableAllRegions();
					neighbors.forEach(obj -> {
						if (obj.getType() == RegionType.SEA || obj.getType() == RegionType.PORT)
							obj.setEnabled(true);
					});
					region.setEnabled(true);
					source = region;
					state = SubState.SELECT_TARGET;
				}
			} else if (state == SubState.SELECT_TARGET) {
				HireMenuState hms = new HireMenuState(region.getUnits(), InputManager.instance().getMousePosWorld(),
						getHirePoints(source), region.getType() == RegionType.SEA);
				region.hideUnits();
				(new ModalState(hms)).run();
				region.updateUnits();
				hirePointsCache.put(source.getName(), hms.getHirePoints());
				region.showUnits();
				state = SubState.SELECT_SOURCE;
				GameMapObject.instance().EnableAllRegions();
			}
		}
	}

	public int getHirePoints(MapPartObject region) {
		String regName = region.getName();
		Integer hirePoints = hirePointsCache.get(regName);
		if (hirePoints == null) {
			hirePoints = region.getHirePoints();
		}
		return hirePoints;
	}

	@Override
	public void exit() {
		GameMapObject.instance().EnableAllRegions();
	}
	
	@Override
	public void update() {
		if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_MIDDLE)==1){
			(new ModalState(
					new MessageBoxState("Hello, you just clicked MIDDLE mouse button", 64, 64)
			)).run();
		}	super.update();
	}
}
