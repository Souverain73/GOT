package got.model;

import org.joml.Vector3f;

public enum Fraction {
	STARK	(new Vector3f(3, 3, 3)), 
	LANISTER (new Vector3f(5, 1, 1)),
	BARATEON(new Vector3f(5,5,0)),
	GREYJOY	(new Vector3f(0.6f, 0.6f, 0.6f)),
	TIREL	(new Vector3f(1, 4, 1)), 
	MARTEL	(new Vector3f(5, 2.5f, 1));
	
	
	private final Vector3f multiplyColor;
	
	Fraction(Vector3f multiply){
		this.multiplyColor = multiply;
	}
	
	public Vector3f getMultiplyColor(){
		return multiplyColor;
	}
}	
