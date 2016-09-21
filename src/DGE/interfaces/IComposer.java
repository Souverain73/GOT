package DGE.interfaces;

public interface IComposer<T> {
	public void addChild(T object);
	public T getChild(int i);
	public void setParent(T object);
	public T getParent();
}
