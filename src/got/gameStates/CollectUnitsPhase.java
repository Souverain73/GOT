package got.gameStates;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.MapPartObject.RegionType;
import got.gameStates.modals.HireMenuState;
import got.interfaces.IClickListener;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.network.Packages;
import got.server.PlayerManager;
import got.utils.UI;

public class CollectUnitsPhase extends ParallelGameState implements IClickListener {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private SubState state;
	private HashMap<String, Integer> hirePointsCache;

	private MapPartObject source; // Hire point source

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		state = SubState.SELECT_SOURCE;
		hirePointsCache = new HashMap<>();
		enableRegionsToHire();
	}

	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender instanceof MapPartObject) {
			MapPartObject region = (MapPartObject) sender;

			if (state == SubState.SELECT_SOURCE) {
				int hirePoints = getHirePoints(region);
				if (hirePoints > 0) {
					List<MapPartObject> regionsForHire = region.getRegionsForHire();
					if (regionsForHire.size() == 1) {
						hireUnits(region, region);
					} else {
						GameClient.shared.gameMap.disableAllRegions();
						regionsForHire.forEach(obj -> obj.setEnabled(true));
						state = SubState.SELECT_TARGET;
						source = region;
					}
				}
			} else if (state == SubState.SELECT_TARGET) {
				hireUnits(source, region);
			}
		}
	}

	private void hireUnits(MapPartObject source, MapPartObject target) {
		//todo:проверить по снабжению, сколько юнитов сюда влезет
		HireMenuState hms = new HireMenuState(target.getUnitObjects(), InputManager.instance().getMousePosWorld(),
				getHirePoints(source), target.getType() == RegionType.SEA);
		target.hideUnits();
		(new ModalState(hms)).run();
		target.showUnits();
		if (hms.isHired()) {
			Unit[] newUnits = target.getUnits();
			GameClient.instance().send(new Packages.ChangeUnits(
					target.getID(), newUnits));
		}
		hirePointsCache.put(source.getName(), hms.getHirePoints());

		state = SubState.SELECT_SOURCE;
		enableRegionsToHire();

		if (GameClient.shared.gameMap.getEnabledRegions().isEmpty())
			setReady(true);
	}

	private void enableRegionsToHire(){
		GameClient.shared.gameMap.forEachRegion((r)->{
			if (r.getFraction() == PlayerManager.getSelf().getFraction() && getHirePoints(r) > 0)
				r.setEnabled(true);
			else
				r.setEnabled(false);
		});
	}

	public int getHirePoints(MapPartObject region) {
		return hirePointsCache.getOrDefault(region.getName(), region.getHirePoints());
	}

	@Override
	public void exit() {
		GameClient.shared.gameMap.enableAllRegions();
		super.exit();
	}
	
	@Override
	public void update() {
		super.update();
	}

	public void recieve(Connection connection, Object pkg) {
		super.recieve(connection, pkg);
		if (pkg instanceof Packages.PlayerChangeUnits) {
			//изменяем состав войск в регионе
			Packages.PlayerChangeUnits msg = ((Packages.PlayerChangeUnits) pkg);

			MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
			Player player = PlayerManager.instance().getPlayer(msg.player);
			region.setUnits(msg.units);
			if (region.getFraction() == Fraction.NONE) {
				region.setFraction(player.getFraction());
			}
		}
	}
}
