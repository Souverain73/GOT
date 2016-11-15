package got.gameObjects;

import java.util.Vector;

import org.joml.Vector2f;

import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.interfaces.IComposer;
import got.utils.LoaderParams;

/**
 * Base class for all GameObject.
 * Implements basic GameObject functions and extends it with Position, Dimensions, and Container functions.
 * @author Souverain73
 *
 */
public abstract class AbstractGameObject<T extends AbstractGameObject<T>> implements GameObject, IComposer<AbstractGameObject>{
	protected Vector<AbstractGameObject> childs;
	protected AbstractGameObject parent;
	protected Vector2f pos;
	protected float w, h;
	protected boolean visible;
	protected DrawSpace space = DrawSpace.WORLD;
	
	protected abstract T getThis();
	
	public Vector2f getPos() {
		return (parent == null) ? pos : new Vector2f(parent.getPos()).add(pos);
	}
	
	protected AbstractGameObject() {
		visible = true;
		pos = new Vector2f();
		childs = new Vector<AbstractGameObject>();
	}
	
	public T setSpace(DrawSpace space){
		this.space = space;
		return getThis();
	}
	
	public DrawSpace getSpace(){
		return space;
	}
	
	@Override
	public void addChild(AbstractGameObject object) {
		if (object!=null){
			object.setParent(this);
			childs.add(object);
		}
	}

	@Override
	public AbstractGameObject getChild(int i) {
		if (i<childs.size()) return childs.get(i);
		return null;
	}

	public void removeChild(AbstractGameObject object){
		if (object!=null){
			object.setParent(null);
			childs.remove(object);
		}
	}
		
	@Override
	public void setParent(AbstractGameObject object) {
		parent = object;
	}
	
	@Override
	public AbstractGameObject getParent() {
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

	public float getW() {
		return w;
	}

	public T setW(float w) {
		this.w = w;
		return getThis();
	}

	public float getH() {
		return h;
	}

	public T setH(float h) {
		this.h = h;
		return getThis();
	}

	public T setPos(Vector2f pos) {
		this.pos = pos;
		return getThis();
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
	public boolean isVisible(){
		return visible;
	};
	
	@Override
	public void setVisible(boolean visible){
		this.visible = visible;
	}
}
