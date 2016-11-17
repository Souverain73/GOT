package got.gameStates.modals;



import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.ImageButton;
import got.gameObjects.ImageObject;
import got.gameStates.AbstractGameState;
import got.graphics.TextureManager;
import got.interfaces.IClickListener;
import got.model.Unit;

public class UnitSelectState extends AbstractGameState implements IClickListener{
	private final String name = "UnitSelect";
	public Object result;
	
	public UnitSelectState(Unit[] units, Vector2f pos){
		float x = pos.x;
		float y = pos.y;
			ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
				new Vector2f(x,y), 220, 60);
			addObject(bg);
		x = pos.x + 6;
		y = pos.y + 5;
		
		int unitsCount = units.length;
		for (int i=0; i<unitsCount; i++){
			ImageButton btn = new ImageButton(units[i].getTexture(), 
					(int)x, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					units[i]);
			btn.setCallback(this::clickCallback);
			addObject(btn);
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

	public Object getResult(){
		return result;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		
	}

	@Override
	public int getID() {
		return -1;
	}

	@Override
	public void click(GameObject sender) {
		if (sender == null){
			close();
		}
	}
}
