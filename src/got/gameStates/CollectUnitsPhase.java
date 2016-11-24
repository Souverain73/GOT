package got.gameStates;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.MapPartObject.RegionType;
import got.gameStates.modals.HireMenuState;
import got.server.PlayerManager;

public class CollectUnitsPhase extends ActionPhase {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private SubState state;
	private HashMap<String, Integer> hirePointsCache;

	private MapPartObject source; // Hire point source

	CollectUnitsPhase() {
		state = SubState.SELECT_SOURCE;
		hirePointsCache = new HashMap<>();
	}

	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;

			if (state == SubState.SELECT_SOURCE) {
				int hirePoints = getHirePoints(region);
				System.out.println("Click region. Hire points:" + hirePoints);
				if (hirePoints > 0) {
					// check if units can be placed in neighboring regions
					List<MapPartObject> neighbors = region.getNeighbors();
					GameMapObject.instance().disableAllRegions();
					neighbors.forEach(obj -> {
						if ((obj.getType() == RegionType.SEA && obj.getFraction() == PlayerManager.getSelf().getFraction())
								|| obj.getType() == RegionType.PORT)
							obj.setEnabled(true);
					});
					region.setEnabled(true);
					source = region;
					state = SubState.SELECT_TARGET;
				}
			} else if (state == SubState.SELECT_TARGET) {
				HireMenuState hms = new HireMenuState(region.getUnitObjects(), InputManager.instance().getMousePosWorld(),
						getHirePoints(source), region.getType() == RegionType.SEA);
				region.hideUnits();
				(new ModalState(hms)).run();
				region.updateUnits();
				hirePointsCache.put(source.getName(), hms.getHirePoints());
				region.showUnits();
				state = SubState.SELECT_SOURCE;
				GameMapObject.instance().enableAllRegions();
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
		GameMapObject.instance().enableAllRegions();
		super.exit();
	}
	
	@Override
	public void update() {
		super.update();
	}
}
