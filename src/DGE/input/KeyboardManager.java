package DGE.input;

public class KeyboardManager {
	private static KeyboardManager _instance = null;
	private KeyboardManager(){}
	
	public static KeyboardManager instance(){
		if (_instance == null){
			_instance = new KeyboardManager();
		}
		return _instance;
	}
	
	public boolean isKeyDown(int key){
		return false;
	}
}
