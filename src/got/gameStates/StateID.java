package got.gameStates;

import got.server.serverStates.base.ServerState;
import got.vesterosCards.States.CollectInfluence;
import got.vesterosCards.States.CollectSuply;
import got.vesterosCards.States.CollectUnits;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *	All states that can be changed through network must have an ID 
 *
 */
public class StateID {
	public static final int MAIN_STATE = 16;
	public static final int GAME_CONFIG_STATE = 17;
	public static final int PLANNING_PHASE = 1;
	public static final int FIRE_PHASE = 3;
	public static final int MOVE_PHASE = 4;
	public static final int POWER_PHASE = 5;
	public static final int VESTEROS_PHASE = 6;
	public static final int BATTLE_PHASE = 7;
	public static final int HELP_PHASE = 8;
	public static final int BATTLE_RESULT_PHASE = 9;
	public static final int SELECT_HOUSE_CARD_PHASE = 10;
	public static final int PLAY_VESTEROS_STATE = 18;
	public static final int COLLECT_UNITS = 19;
	public static final int COLLECT_INFLUENCE = 20;
	public static final int COLLECT_SUPLY = 21;

    public static GameState getGameStateByID(int id){
		switch(id){
			case MAIN_STATE: return new MainState();
			case PLANNING_PHASE: return new PlanningPhase();
			case FIRE_PHASE: return new FirePhase();
			case MOVE_PHASE: return new MovePhase();
			case POWER_PHASE: return new PowerPhase();
			case VESTEROS_PHASE: return new VesterosPhase();
			case BATTLE_PHASE: return null;
			case HELP_PHASE: return new HelpPhase();
			case BATTLE_RESULT_PHASE: return new BattleResultState();
			case SELECT_HOUSE_CARD_PHASE: return new SelectHouseCardPhase();
			case GAME_CONFIG_STATE: return new GameConfigState();
			case PLAY_VESTEROS_STATE: return new PlayVesterosCard();
			case COLLECT_UNITS: return new CollectUnits.ClientState();
			case COLLECT_INFLUENCE: return new CollectInfluence.ClientState();
			case COLLECT_SUPLY: return new CollectSuply.ClientState();
		}
		return null;
	}
	
	public static ServerState getServerStateByID(int id){
		throw new NotImplementedException();
	}
}
