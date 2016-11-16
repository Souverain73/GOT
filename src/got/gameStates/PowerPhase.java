package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

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
import got.model.Fraction;
import got.model.Unit;
import got.gameObjects.ActionObject.Action;
import got.gameObjects.MapPartObject.RegionType;
import got.network.Packages;
import got.network.Packages.ChangeUnits;
import got.server.PlayerManager;
import got.utils.UI;

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
				//���� ���� ������ ����� ��������.
				int action = showSelectActionDialog();
				if (action==-1){
					return;
				}else if(action==0){
					sendCollectMoney(region);
					region.setEnabled(false);
					if (GameMapObject.instance().getEnabledRegions().isEmpty())
						GameClient.instance().send(new Packages.Ready(false));
				}else if (action==1){
					//���� ����� �������� ������ ������� ���� ��� �����
					//���� ���������, ����� �� �� ������ � ����, ���� ��, �� ������������ �����.
					region.hideUnits();
					HireMenuState hms = new HireMenuState(
							region.getUnitObjects(), InputManager.instance().getMousePosWin(),
							region.getBuildingLevel(), region.getType() == RegionType.SEA);
					(new ModalState(hms)).run();
					region.showUnits();
					//TODO: ���� ��������, ����� ����� ����� ������� �� ������ ������� � ��������� ������
					//� ����� �������� ���� ���� ��� ����������� ������� ���� ����� � ������ ������� ��� ��� ������� �������
					Unit[] newUnits = region.getUnits();
					GameClient.instance().send(new Packages.ChangeUnits(
							region.getID(), newUnits));
					
					//���� �������� ��� ���� �����, ���� ��������� ����� �� ������������� ��������.
					GameClient.instance().send(new Packages.Act(region.getID(), 0));
					region.setEnabled(false);
					if (GameMapObject.instance().getEnabledRegions().isEmpty())
						GameClient.instance().send(new Packages.Ready(false));
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
				UI.systemMessage("MyTurn");
				if (!enableRegionsWithCrown()){
					UI.systemMessage("I can't turn");
					//���� �� ��� ����������� �� ���� ������, ������ ������� ����� �� ����� ��������� ���.
					//� ����� ������ ����������� �������� �� ���� ������� ������� Ready.
					GameClient.instance().send(new Packages.Ready(false));
				}else{
					if (firstTurn){
						firstTurn = false;
						firstTurn();
					}
				}
			}else{
				UI.systemMessage("Not my turn :( cpid:"+msg.playerID);
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
			GameClient.instance().registerTask(()->{region.setAction(null);});
		}
		
		if (pkg instanceof Packages.ChangeUnits){
			//�������� ������ ����� � �������
			Packages.ChangeUnits msg = ((Packages.ChangeUnits)pkg);
			
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.region);
			region.setUnits(msg.units);
		}
		if (pkg instanceof Packages.PlayerAct){
			Packages.PlayerAct msg = ((Packages.PlayerAct)pkg);
			MapPartObject region = GameMapObject.instance().getRegionByID(msg.from);
			
			GameClient.instance().registerTask(()->{region.setAction(null);});
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
					&& (region.getAction().getType() == Action.MONEY ||
						region.getAction().getType() == Action.MONEYPLUS);
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
		ImageObject background = new ImageObject("selectActionBackground.png",
				InputManager.instance().getMousePosWorld(), 430, 120).setSpace(DrawSpace.WORLD);
		background.addChild(new ImageButton("Warriors.png", 10, 10, 200, 100, null).setCallback((sender, param)->{
			result[0] = 1;
			GameClient.instance().closeModal();
		}).setSpace(DrawSpace.WORLD));
		background.addChild(new ImageButton("Power.png", 220, 10, 200, 100, null).setCallback((sender, param)->{
			result[0] = 0;
			GameClient.instance().closeModal();
		}).setSpace(DrawSpace.WORLD));
		CustomModalState cms = new CustomModalState();
		cms.addObject(background);
		new ModalState(cms).run();
		
		return result[0];
	}
}
