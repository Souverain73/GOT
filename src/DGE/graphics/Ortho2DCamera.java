package DGE.graphics;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Ortho2DCamera implements ICamera{
	private Matrix4f projection;
	private Vector3f position;
	private AxisAngle4f rotation;
	private float scale;
	private float x;
	private float y;
	private float w;
	private float h;
	private boolean forceUpdate;
	private boolean debug;
	
	public Ortho2DCamera(){
		this(true);
	}
	
	public Ortho2DCamera(boolean forceUpdate){
		this(0, 0, forceUpdate, false);
	}
	
	public Ortho2DCamera(float x, float y, boolean forceUpdate, boolean debug) {
		projection = new Matrix4f();
		position = new Vector3f(0, 0, 0);
		rotation = new AxisAngle4f(0, 0, 0, 1);
		this.x = x;
		this.y = y;
		w = 0;
		h = 0;
		scale = 0.5f;
		this.forceUpdate = forceUpdate;
		this.debug = debug;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public Matrix4f getProjection() {
		return projection;
	}

	@Override
	public void moveCamera(Vector3f diff) {
		position.x += diff.x;
		position.y += diff.y;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setPosition(Vector3f pos) {
		position.x = pos.x;
		position.y = pos.y;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public Vector3f getCameraPosition() {
		return position;
	}

	@Override
	public void rotateCamera(float degrees, Vector3f axis) {
		rotation.angle += degrees;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void setRotation(float degrees, Vector3f axis) {
		rotation.angle = degrees;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void lookAt(Vector3f target) {
		position.x = target.x;
		position.y = target.y;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setViewport(float left, float right, float bottom, float top) {
		
	}

	@Override
	public void windowResizeCallback(int newW, int newH) {
		w = newW;
		h = newH;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void scale(float diff) {
		scale+=diff;
		if (forceUpdate) updateCamera();
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		if (forceUpdate) updateCamera();
	}
	
	@Override
	public void updateCamera(){
		if (debug) System.out.println(this.toString());
		projection.setOrtho(x-w/2, x+w/2, y+h/2, y-h/2, 1, -1);
		projection.scale(scale);
		projection.translate(position);
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Ortho2DCamera\n");
		sb.append("Position: "+position.x+" "+position.y+"\n");
		sb.append("Rotation: "+rotation.angle+"\n");
		sb.append("Scale: "+scale+"\n");
		return sb.toString();
	}
}
