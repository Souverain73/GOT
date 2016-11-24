package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.HireMenuState;
import got.graphics.DrawSpace;
import got.model.Action;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.gameObjects.MapPartObject.RegionType;
import got.network.Packages;
import got.server.PlayerManager;
import got.utils.UI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class PowerPhase extends ActionPhase {
	private enum SubState {
		SELECT_SOURCE, SELECT_TARGET
	}

	private SubState state;


	private boolean firstTurn;
	private Map<MapPartObject, Integer> hirePointsCache;
	private MapPartObject sourceRegion = null;

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		firstTurn = true;
		hirePointsCache = new HashMap<>();
		state = SubState.SELECT_SOURCE;
		enableRegionsWithCrown();
	}


	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject) sender;
			if (state == SubState.SELECT_SOURCE) {
				if (region.getAction() == Action.MONEYPLUS &&
						region.getBuildingLevel() > 0) {
					//Надо дать игроку выбор действия.
					int action = -1;
					//Если игрок уже начал нанимать юнитов в регионе, то у него нет выбора.
					//Пусть набирает дальше
					if (hirePointsCache.containsKey(region)
							&& hirePointsCache.get(region) != region.getHirePoints()) {
						action = 1;
					}else {
						action = showSelectActionDialog();
					}
					Stream.of("a", "b").findFirst();
					if (action == -1) {
						return;
					} else if (action == 0) {
						sendCollectMoney(region);
						region.setEnabled(false);
						if (GameMapObject.instance().getEnabledRegions().isEmpty())
							GameClient.instance().send(new Packages.Ready(false));
					} else if (action == 1) {
						//Если можем нанимать юнитов создаем меню для найма
						//надо проверить, можем ли мы нанять в море, если да, то предоставить выбор.
						{
							List<MapPartObject> regionsForHire = region.getRegionsForHire();
							if (regionsForHire.size() == 1) {
								hireUnits(region, region);
							} else {
								GameMapObject.instance().disableAllRegions();
								regionsForHire.forEach(obj -> obj.setEnabled(true));
								state = SubState.SELECT_TARGET;
								sourceRegion = region;
							}
						}
					}

				}
			}else if(state == SubState.SELECT_TARGET){
				hireUnits(sourceRegion, region);
			}
		}
	}

	private void hireUnits(MapPartObject source, MapPartObject target) {
		target.hideUnits();
		HireMenuState hms = new HireMenuState(
                target.getUnitObjects(), InputManager.instance().getMousePosWin(),
                getHirePoints(source), target.getType() == RegionType.SEA);
		(new ModalState(hms)).run();
		target.showUnits();

		if (hms.isHired()) {

			Unit[] newUnits = target.getUnits();
			GameClient.instance().send(new Packages.ChangeUnits(
                target.getID(), newUnits));

			if (hms.getHirePoints() == 0) {
				//Если потратил все очки найма, надо отправить пакет об использовании действия.
				GameClient.instance().send(new Packages.Act(source.getID(), 0));
				source.setEnabled(false);
				if (GameMapObject.instance().getEnabledRegions().isEmpty())
					GameClient.instance().send(new Packages.Ready(false));
			} else {
				hirePointsCache.put(source, hms.getHirePoints());
			}
		}

		state = SubState.SELECT_SOURCE;
		sourceRegion = null;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.PlayerTurn) {
			Packages.PlayerTurn msg = ((Packages.PlayerTurn) pkg);
			if (PlayerManager.getSelf().id == msg.playerID) {
				UI.systemMessage("MyTurn");
				if (!enableRegionsWithCrown()) {
					UI.systemMessage("I can't turn");
					//Если не был активирован ни один регион, значит текущий игрок не может совершить ход.
					//В таком случае необходлимо сообщить об этом серверу пакетом Ready.
					GameClient.instance().send(new Packages.Ready(false));
				} else {
					if (firstTurn) {
						firstTurn = false;
						firstTurn();
					}
				}
			} else {
				UI.systemMessage("Not my turn :( current player id:" + msg.playerID);
				//Если чужой ход, отключаем все регионы, так как в чужой ход мы не можем совершать действий.
				GameMapObject.instance().disableAllRegions();
			}
		}
		if (pkg instanceof Packages.CollectInfluence){
			//Собираем влияние(деньги)
			Packages.CollectInfluence msg = ((Packages.CollectInfluence)pkg);
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.region);

			if (region.getFraction() == PlayerManager.getSelf().getFraction()){
				PlayerManager.getSelf().addMoney(region.getInfluencePoints()+1);
			}
			GameClient.instance().registerTask(()->{region.setAction(null);});
		}

		if (pkg instanceof Packages.PlayerChangeUnits){
			//изменяем состав войск в регионе
			Packages.PlayerChangeUnits msg = ((Packages.PlayerChangeUnits)pkg);

			MapPartObject region = GameMapObject.instance().getRegionByID(msg.region);
			Player player = PlayerManager.instance().getPlayer(msg.player);
			region.setUnits(msg.units);
			if (region.getFraction() == Fraction.NEUTRAL){
				region.setFraction(player.getFraction());
			}
		}
		if (pkg instanceof Packages.PlayerAct){
			Packages.PlayerAct msg = ((Packages.PlayerAct)pkg);
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.from);

			GameClient.instance().registerTask(()->{region.setAction(null);});
		}
	}


	private boolean enableRegionsWithCrown(){
		Fraction selfFraction = PlayerManager.getSelf().getFraction();

		//обход ограничения на изменение переменных внутри лямбд
		boolean []result = new boolean[1];
		result[0] = false;

		GameMapObject.instance().setEnabledByCondition(region->{
			boolean enable = region.getFraction() == selfFraction
					&& region.getAction() != null
					&& (region.getAction() == Action.MONEY ||
						region.getAction() == Action.MONEYPLUS);
			result[0] |= enable;
			return enable;
		});

		return result[0];
	}

	/**
	 * В первый ход автоматически играются все приказы для которых не требуется решение игрока.
	 */
	private void firstTurn(){
		for (MapPartObject region: GameMapObject.instance().getEnabledRegions()){
			if (region.getAction() == Action.MONEY){
				sendCollectMoney(region);
				region.setEnabled(false);
			}else if(region.getAction() == Action.MONEYPLUS){
				if (region.getBuildingLevel() == 0){
					sendCollectMoney(region);
				}
			}
		}
	}

	private int getHirePoints(MapPartObject region){
		if (hirePointsCache.get(region) == null){
			return region.getHirePoints();
		}else{
			return hirePointsCache.get(region);
		}
	}

	private void sendCollectMoney(MapPartObject region){
		GameClient.instance().send(new Packages.CollectInfluence(region.getID()));
	}

	private int showSelectActionDialog(){

		CustomModalState<Integer> cms = new CustomModalState<>(-1);

		ImageObject background = new ImageObject("selectActionBackground.png",
				InputManager.instance().getMousePosWorld(), 430, 120).setSpace(DrawSpace.WORLD);
		background.addChild(new ImageButton("Warriors.png", 10, 10, 200, 100, null).setCallback((sender, param)->{
			cms.setResult(1);
			GameClient.instance().closeModal();
		}).setSpace(DrawSpace.WORLD));
		background.addChild(new ImageButton("Power.png", 220, 10, 200, 100, null).setCallback((sender, param)->{
			cms.setResult(0);
			GameClient.instance().closeModal();
		}).setSpace(DrawSpace.WORLD));
		cms.addObject(background);
		new ModalState(cms).run();

		return cms.getResult();
	}
}
