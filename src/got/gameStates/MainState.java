package got.gameStates;

import got.gameObjects.*;
import got.model.Game;
import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.graphics.DrawSpace;
import got.graphics.TextureManager;
import got.network.Packages.ChangeState;
import got.server.PlayerManager;
import got.utils.LoaderParams;

import static com.sun.javafx.logging.PulseLogger.addMessage;
import static got.translation.Translator.tt;

public class MainState extends AbstractGameState {
	private static final String name = "MainState";
	private static final String MAP_FILE = "data/map.xml";
	private StateMachine stm;
	private GameMapObject map;
	private ImageObject background;

	private GUIObject gui;


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine extstm) {
		stm = new StateMachine();
		System.out.println("Entering "+name);

		background = new ImageObject(TextureManager.instance().loadTexture("backgroundMain.png"),
				new Vector2f(0,0), Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		background.setSpace(DrawSpace.SCREEN);

		map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", MAP_FILE}));

		GameClient.shared.gameMap = map;

		addObject(gui = new GUIObject());

		GameClient.shared.gui = gui;

		super.enter(extstm);
	}

	@Override
	public void exit() {
		background.finish();
		map.finish();
		super.exit();
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		background.draw(this);
		map.draw(stm.getCurrentState());

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.draw(stm.getCurrentState());
		}

		stm.draw();
		super.draw();
	}

	@Override
	public void update() {
		background.update(this);
		map.update(stm.getCurrentState());

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.update(stm.getCurrentState());
		}

		stm.update();
		super.update();
	}
	
	public void tick(){
		background.tick();
		map.tick();

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.tick();
		}

		stm.tick();
		super.tick();
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
