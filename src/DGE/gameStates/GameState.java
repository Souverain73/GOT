package DGE.gameStates;

public interface GameState {
	public String getName();
	public void enter();
	public void exit();
	public void draw();
	public void update();
}
