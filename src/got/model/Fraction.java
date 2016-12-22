package got.model;

import got.graphics.Texture;
import got.graphics.TextureManager;
import org.joml.Vector3f;

public enum Fraction {
	STARK	(new Vector3f(3, 3, 3), "back.png"),
	LANISTER (new Vector3f(5, 1, 1), "back.png"),
	BARATEON(new Vector3f(5,5,0), "back.png"),
	GREYJOY	(new Vector3f(0.6f, 0.6f, 0.6f), "back.png"),
	TIREL	(new Vector3f(1, 4, 1), "back.png"),
	MARTEL	(new Vector3f(5, 2.5f, 1), "back.png"),
	NONE	(new Vector3f(1, 1, 1), "back.png"),
	NEUTRAL_LORD(new Vector3f(1, 1, 1), "back.png");
	
	
	private final Vector3f multiplyColor;
	private final String backTextureName;
	private Texture backTexture = null;
	private static final String TEXTURE_BASE = "fractions/";

	Fraction(Vector3f multiply, String backTextureName){
		this.multiplyColor = multiply;
		this.backTextureName = backTextureName;
	}

	public Texture getBackTexture(){
		if (backTexture == null){
			backTexture = TextureManager.instance().loadTexture(TEXTURE_BASE + backTextureName);
		}
		return backTexture;
	}

	public Vector3f getMultiplyColor(){
		return multiplyColor;
	}
}	
