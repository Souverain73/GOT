package DGE;

import java.text.DecimalFormat;

import DGE.graphics.Texture;
import DGE.graphics.TextureManager;

public class test {

	public static void main(String[] args) {
		Game.instance().init();
		Texture test = TextureManager.instance().loadTexture("Winterfell.png");
		for (int i = 0; i<678; i+=5){
			for (int j=0; j<837; j+=5){
				if (test.getAlfa(j, i)==0){
					System.out.print(" ");
				}else {
					System.out.print("*");
				}
			}
			System.out.println("");
		}

	}	

}
