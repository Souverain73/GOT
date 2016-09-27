package DGE;

import DGE.graphics.Texture;
import DGE.graphics.TextureManager;

/**
 * Test runner
 * @author Souverain73
 *
 */
public class test {

	public static void main(String[] args) {
		Game.instance().init();
		Texture test = TextureManager.instance().loadTexture("Winterfell.png");
		for (float i = 0; i<1000; i+=5){
			for (float j=0; j<1000; j+=5){
				if (test.getAlfa(j/1000, i/1000)==0){
					System.out.print(" ");
				}else {
					System.out.print("*");
				}
			}
			System.out.println("");
		}

	}	

}
