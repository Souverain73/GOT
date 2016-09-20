package DGE.gameStates;

import java.util.function.Predicate;

public class WaitingPhase implements GameState {
	private final String name = "WaitingPhase";
	private StateMachine stm;
	private Predicate<Object> cond;
	private GameState next;
	
	@Override
	public String getName() {
		return name;
	}
	
	public WaitingPhase(Predicate<Object> cond, GameState next) {
		this.cond = cond;
	}
	
	@Override
	public void enter(StateMachine stm) {
		this.stm = stm;
	}

	@Override
	public void exit() {
		
	}

	@Override
	public void draw() {
		
	}

	@Override
	public void update() {
		if (cond.test(null)){
			stm.setState(next);
		}
	}

}
