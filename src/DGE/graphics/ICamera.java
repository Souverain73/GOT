package DGE.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ICamera {
	public Matrix4f getProjection();
	public void 	moveCamera(Vector3f diff);
	public void 	setPosition(Vector3f pos);
	public Vector3f getCameraPosition();
	public void 	rotateCamera(float degrees, Vector3f axis);
	public void 	setRotation(float degrees, Vector3f axis);
	public void		lookAt(Vector3f target);
	public void 	setViewport(float left, float right, float bottom, float top);
	public void 	windowResizeCallback(int newW, int newH);
	public void 	scale(float diff);
	public void 	setScale(float scale);
	public void 	updateCamera();
}
