package DGE.interfaces;

import org.joml.Vector2f;

public interface IClickable {
	public int getPriority();
	public boolean ifMouseIn(Vector2f mousePos);
	public void setMouseIn(boolean mouseIn);
	public boolean isActive();
}
