package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.Fraction;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.HireMenuState;
import got.graphics.DrawSpace;
import got.gameObjects.ActionObject.Action;
import got.gameObjects.MapPartObject.RegionType;
import got.network.Packages;
import got.server.PlayerManager;

public class PowerPhase extends ActionPhase {
	
	private boolean firstTurn;
	
	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		firstTurn = true;
		enableRegionsWithCrown();
	}
	
	@Override
	public void click(GameObject sender) {
		//TODO: Handle clicks
		if (sender instanceof MapPartObject){
			MapPartObject region = (MapPartObject)sender;
			if (region.getAction().getType() == Action.MONEYPLUS &&
					region.getBuildingLevel()>0){
				int action = showSelectActionDialog();
				if (action==-1) return;
				
				
				if (action==1){
					//���� ���� ������ ����� ��������.
					//���� ����� �������� ������ ������� ���� ��� �����
					//���� ���������, ����� �� �� ������ � ����, ���� ��, �� ������������ �����.
					HireMenuState hms = new HireMenuState(
							region.getUnits(), InputManager.instance().getMousePosWin(),
							region.getBuildingLevel(), region.getType() == RegionType.SEA);
					(new ModalState(hms)).run();
				
					//�������� ���������� �� ������.
				}
				
			}
		}
	}
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		//TODO: handle networking
		if (pkg instanceof Packages.PlayerTurn){
			Packages.PlayerTurn msg = ((Packages.PlayerTurn)pkg);
			if (PlayerManager.getSelf().id == msg.playerID){
				if (!enableRegionsWithCrown()){
					//���� �� ��� ����������� �� ���� ������, ������ ������� ����� �� ����� ��������� ���.
					//� ����� ������ ����������� �������� �� ���� ������� ������� Ready.
					GameClient.instance().send(new Packages.Ready(false));
				}else{
					if (firstTurn){
						firstTurn = false;
						firstTurn();
					}
					GameClient.instance().send(new Packages.Ready(true));
				}
			}else{
				//���� ����� ���, ��������� ��� �������, ��� ��� � ����� ��� �� �� ����� ��������� ��������.
				GameMapObject.instance().disableAllRegions();
			}
		}
		if (pkg instanceof Packages.CollectInfluence){
			//�������� �������(������)
			Packages.CollectInfluence msg = ((Packages.CollectInfluence)pkg);
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.region);
			
			if (region.getFraction() == PlayerManager.getSelf().getFraction()){
				PlayerManager.getSelf().addMoney(region.getInfluencePoints()+1);
			}
			region.setAction(null);
		}
	}
	
	
	private boolean enableRegionsWithCrown(){
		Fraction selfFraction = PlayerManager.getSelf().getFraction();
		
		//����� ����������� �� ��������� ���������� ������ �����
		boolean []result = new boolean[1];
		result[0] = false;
		
		GameMapObject.instance().setEnabledByCondition(region->{
			boolean enable = region.getFraction() == selfFraction
					&& region.getAction() != null 
					&& (region.getAction().getType() == Action.FIRE ||
						region.getAction().getType() == Action.FIREPLUS);
			result[0] |= enable;
			return enable;
		});
		
		return result[0];
	}
	
	/**
	 * � ������ ��� ������������� �������� ��� ������� ��� ������� �� ��������� ������� ������.
	 */
	private void firstTurn(){
		for (MapPartObject region: GameMapObject.instance().getEnabledRegions()){
			if (region.getAction().getType() == Action.MONEY){
				sendCollectMoney(region);
			}else if(region.getAction().getType() == Action.MONEYPLUS){
				if (region.getBuildingLevel() == 0){
					sendCollectMoney(region);
				}
			}
		}
	}
	
	private void sendCollectMoney(MapPartObject region){
		GameClient.instance().send(new Packages.CollectInfluence(region.getID()));
	}
	
	private int showSelectActionDialog(){
		int result[] = new int[1];
		result[0]=-1;
		ImageObject background = new ImageObject("selectActionBackground.png", InputManager.instance().getMousePosWin(), 230, 120).setSpace(DrawSpace.WORLD);
		background.addChild(new ImageButton("Warriors.png", 10, 10, 200, 100, null).setCallback((sender, param)->{
			result[0] = 1;
			GameClient.instance().closeModal();
		}));
		background.addChild(new ImageButton("Power.png", 10, 120, 200, 100, null).setCallback((sender, param)->{
			result[0] = 0;
			GameClient.instance().closeModal();
		}));
		CustomModalState cms = new CustomModalState();
		cms.addObject(background);
		new ModalState(cms).run();
		
		return result[0];
	}
}
