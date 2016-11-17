package got.gameStates;

import got.server.*;
import got.server.serverStates.PowerPhaseState;

/**
 *	All states that can be changed through network must have an ID 
 *
 */
public class StateID {
	public static final int MAIN_STATE = 16;
	public static final int PLANNING_PHASE = 1;
	public static final int ACTION_PHASE = 2;
	public static final int FIRE_PHASE = 3;
	public static final int MOVE_PHASE = 4;
	public static final int POWER_PHASE = 5;
	public static final int VESTEROS_PHASE = 6;
	public static final int BATTLE_PHASE = 7;
	public static final int HELP_PHASE = 8;
	
	public static GameState getGameStateByID(int id){
		switch(id){
		case MAIN_STATE: return new MainState();
		case PLANNING_PHASE: return new PlanningPhase();
		case ACTION_PHASE: return new ActionPhase();
		case FIRE_PHASE: return new FirePhase();
		case MOVE_PHASE: return new MovePhase();
		case POWER_PHASE: return new PowerPhase();
		case VESTEROS_PHASE: return new VesterosPhase();
		case BATTLE_PHASE: return null;
		case HELP_PHASE: return null;
		}
		return null;
	}
	
	public static got.server.serverStates.ServerState getServerStateByID(int id){
		switch(id){
		case MAIN_STATE: return new got.server.serverStates.MainState();
		case PLANNING_PHASE: return new got.server.serverStates.PlanningPhaseState();
		case ACTION_PHASE: return null;
		case FIRE_PHASE: return new got.server.serverStates.FirePhaseState();
		case MOVE_PHASE: return null;
		case POWER_PHASE: return new got.server.serverStates.PowerPhaseState();
		case VESTEROS_PHASE: return null;
		case BATTLE_PHASE: return null;
		case HELP_PHASE: return null;
		}
		return null;
	}
}