package DGE.input;

public class MouseManager {
	private static MouseManager _instance = null;
	private MouseManager(){}
	
	public static MouseManager instance(){
		if (_instance == null){
			_instance = new MouseManager();
		}
		return _instance;
	}
	
	public enum MouseButton{
		LEFT, RIGHT, MIDDLE
	}
	
	public boolean isButtonDown(MouseButton mb){
		return false;
	}
}
