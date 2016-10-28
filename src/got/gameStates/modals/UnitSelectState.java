package got.gameStates.modals;

import java.util.Vector;

import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.UnitObject;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.graphics.TextureManager;
import got.server.GameServer.PlayerConnection;

public class UnitSelectState implements GameState{
	private final String name = "UnitSelect";
	public Object result;
	Vector<GameObject> objects;
	
	public UnitSelectState(Vector<UnitObject> units, Vector2f pos){
		objects = new Vector<GameObject>();
		float x = pos.x;
		float y = pos.y;
			ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
				new Vector2f(x,y), 220, 60);
			objects.add(bg);
		x = pos.x + 6;
		y = pos.y + 5;
		
		int unitsCount = units.size();
		for (int i=0; i<unitsCount; i++){
			ImageButton btn = new ImageButton(units.get(i).getTexture(), 
					(int)x, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					units.get(i));
			btn.setCallback(this::clickCallback);
			objects.add(btn);
			x+=Constants.UNIT_SIZE + Constants.UNIT_STEP;
		}
	}
	
	private void clickCallback(GameObject sender, Object param){
		result = param;
		close();
	}
	
	private void close(){
		GameClient.instance().closeModal();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		
	}

	@Override
	public void exit() {
		objects.forEach(obj->obj.finish());
		objects.clear();
	}

	@Override
	public void draw() {
		objects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		objects.forEach(obj->obj.update(this));
	}

	public Object getResult(){
		return result;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		// TODO Auto-generated method stub
		
	}
}
