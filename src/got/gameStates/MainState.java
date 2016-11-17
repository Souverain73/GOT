package got.gameStates;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.gameObjects.GameMapObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.graphics.DrawSpace;
import got.graphics.TextureManager;
import got.network.Packages.ChangeState;
import got.server.PlayerManager;
import got.utils.LoaderParams;

public class MainState extends AbstractGameState {
	private static final String name = "MainState";
	private static final String MAP_FILE = "data/map.xml";
	private StateMachine stm;
	private GameMapObject map;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine extstm) {
		stm = new StateMachine();
		System.out.println("Entering "+name);
		
		ImageObject background = new ImageObject(TextureManager.instance().loadTexture("backgroundMain.png"), 
				new Vector2f(0,0), Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		background.setSpace(DrawSpace.SCREEN);
		gameObjects.add(background);
		
		map = new GameMapObject();
		LoaderParams params = new LoaderParams(new String[]{"filename", MAP_FILE});
		map.init(params);
		//addObject(map);
		TextObject fractionText = new TextObject(PlayerManager.getSelf().getFraction().toString()); 
		fractionText.setPos(new Vector2f(10,10));
		fractionText.setSpace(DrawSpace.SCREEN);
		addObject(fractionText);
		
		super.enter(extstm);
	}

	@Override
	public void exit() {
		super.exit();
		map.finish();
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		super.draw();
		map.draw(stm.getCurrentState());
		stm.draw();
	}

	@Override
	public void update() {
		super.update();
		map.update(stm.getCurrentState());
		stm.update();
	}
	
	public void tick(){
		super.tick();
		map.tick();
		stm.tick();
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof ChangeState){
			int stateID = ((ChangeState) pkg).state;
			GameClient.instance().registerTask(() -> stm.setState(StateID.getGameStateByID(stateID)));
			return;
		}
		stm.recieve(connection, pkg);
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}
	
	public GameState getCurrentState(){
		return stm.getCurrentState();
	}
}
