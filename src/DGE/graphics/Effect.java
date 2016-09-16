package DGE.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Effect {
	public  Vector3f overlay;
	public  Vector3f colorMultiply;
	public  Matrix4f transform;
	
	public Effect() {
	}
	
	public Effect(Vector3f overlay, Vector3f colorMultiply, Matrix4f transform) {
		this.overlay = overlay;
		this.colorMultiply = colorMultiply;
		this.transform = transform;
	}
}
