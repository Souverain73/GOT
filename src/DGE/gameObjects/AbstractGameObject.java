package DGE.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;

import DGE.gameStates.GameState;
import DGE.utils.LoaderParams;

public class AbstractGameObject implements GameObject{
	protected Vector<GameObject> childs;
	protected GameObject parent;
	protected Vector2f pos;
	protected float w, h;
	protected boolean visible;
	
	@Override
	public Vector2f getPos() {
		return (parent == null) ? pos : new Vector2f(parent.getPos()).add(pos);
	}
	
	protected AbstractGameObject() {
		visible = true;
		pos = new Vector2f();
		childs = new Vector<GameObject>();
	}
	
	@Override
	public void addChild(GameObject object) {
		if (object!=null){
			object.setParent(this);
			childs.add(object);
		}
	}

	@Override
	public GameObject getChild(int i) {
		if (i<childs.size()) return childs.get(i);
		return null;
	}

	@Override
	public GameObject getParent() {
		return parent;
	}

	@Override
	public boolean init(LoaderParams params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(GameState state) {
		if (isVisible()){
			childs.forEach(obj->obj.draw(state));
		}
	}

	@Override
	public void update(GameState state) {
		childs.forEach(obj->obj.update(state));
	}

	@Override
	public void finish() {
		childs.forEach(obj->obj.finish());
	}

	@Override
	public void setParent(GameObject object) {
		parent = object;
	}
	
	@Override
	public boolean isVisible(){
		return visible;
	};
	
	@Override
	public void setVisible(boolean visible){
		this.visible = visible;
	}
}
