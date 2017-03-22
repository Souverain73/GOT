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

import static got.translation.Translator.tt;

public class MainState extends AbstractGameState {
	private static final String name = "MainState";
	private static final String MAP_FILE = "data/map.xml";
	private StateMachine stm;
	private GameMapObject map;
	private ImageObject background;
	private TextObject tooltipText;
	private GameLogObject glo;
	private TrackObject tt;

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
		//addObject(map);
		addObject(new PlayerPanelObject(PlayerManager.getSelf()));
		addObject(tooltipText = new TextObject("Tooltip").setPos(new Vector2f(300,10)).setSpace(DrawSpace.SCREEN));

		addObject(glo = new GameLogObject(300, 100, 32).setPos(new Vector2f(0, Constants.SCREEN_HEIGHT-100)).setSpace(DrawSpace.SCREEN));

		addObject(tt = new TrackObject(Game.instance().getTrack(Game.THRONE_TRACK))
				.setPos(new Vector2f(300, Constants.SCREEN_HEIGHT-100))
				.setSpace(DrawSpace.SCREEN));

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
		tooltipText.draw(this);

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
		tooltipText.update(this);

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.update(stm.getCurrentState());
		}

		stm.update();
		super.update();
	}
	
	public void tick(){
		background.tick();
		map.tick();
		tooltipText.tick();

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

	public void setTooltipText(String text){
		tooltipText.setText(text);
	}

	public void logMessage(String message){
		glo.addMessage(message);
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}
	
	public GameState getCurrentState(){
		return stm.getCurrentState();
	}
}
