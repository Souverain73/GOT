package got.gameObjects.interfaceControls;

import org.joml.Vector2f;

import got.utils.Utils;

public class BlankButtonObject extends AbstractButtonObject<BlankButtonObject> {
	@Override	protected BlankButtonObject getThis() {	return this; }
	
	@Override
	public boolean ifMouseIn(Vector2f mousePos) {
		return Utils.pointInRect(mousePos , getPos(), getDim());
	}

}
