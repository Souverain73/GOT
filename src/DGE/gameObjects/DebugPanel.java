package DGE.gameObjects;

import DGE.Game;
import DGE.Player;
import DGE.gameStates.GameState;
import DGE.graphics.DrawSpace;
import DGE.graphics.Font;
import DGE.graphics.Text;

public class DebugPanel extends AbstractGameObject{
	private static DebugPanel _instance = null;
	private boolean drawn = false;
	private boolean updated = false;

	public static DebugPanel instance() {
		if (_instance == null) {
			_instance = new DebugPanel();
		}
		return _instance;
	}
	private Font dbgFont = new Font("test");
	private String statePrefix = "State:";
	private String fractionPrefix = "Fraction";
	private Text tCurState;
	private Text tCurFraction;
	
	public DebugPanel() {
		addChild(new FPSCounterObject());
		tCurState = Text.newInstance("", dbgFont);
		tCurFraction = Text.newInstance("", dbgFont);
	}
	
	@Override
	public void update(GameState state) {
		if (updated) return;
		super.update(state);
		if (state==null)
			tCurState.changeText(statePrefix + Game.instance().getStateMachine().getCurrentState().getName());
		else
			tCurState.changeText(statePrefix + state.getName());
		tCurFraction.changeText(Player.instance().getFraction().toString());
		updated = true;
	}
	
	@Override
	public void draw(GameState state) {
		if (drawn) return;
		super.draw(state);
		tCurState.draw(10, 32, 1, 1, DrawSpace.SCREEN);
		tCurFraction.draw(10, 64, 1, 1, DrawSpace.SCREEN);
		drawn = true;
	}
	
	public void resetFlags(){
		updated = drawn = false;
	}
}
