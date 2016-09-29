package DGE.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Font{
	
	private static final String fontsBase = "data/fonts/";
	
	private int mapWidth, mapHeight;
	private int cellWidth, cellHeight;
	private int startChar;
	private int[] charWidths;
	private int rowPitch;
	private float rowFactor;
	private float colFactor;
	private Texture map;
	
	/**
	 * Load font data, like size, characters width, etc<br>
	 * For each font must exist <b>fontName.bmp</b> file with image data
	 * and <b>fontName.dat</b> with font data.<br>
	 * Font data format:<br>
	 * <p>
	 * Offset Size(Bytes) Description<br> 
	 * 0 	4 - Map Width<br>
	 * 4 	4 - Map Height<br>
	 * 8 	4 - Cell Width<br> 
	 * 12	4 - Cell Height<br> 
	 * 16 	1 - Start Character<br> 
	 * 17 	256 - Character Widths<br>
	 * </p>
	 * @param fontName - name of font to load
	 */
	public Font(String fontName){
		Path dataPath = Paths.get(fontsBase+fontName+".dat");
		Path texturePath = Paths.get(fontsBase+fontName+".bmp");

		if (Files.notExists(dataPath)){
			System.out.println("Can't find data for font "+fontName);
		}
		if (Files.notExists(texturePath)){
			System.out.println("Can't find texture for font "+fontName);
		}
		try {
			ByteBuffer data = ByteBuffer.wrap(Files.readAllBytes(dataPath));
			data.order(ByteOrder.LITTLE_ENDIAN);
			//read font parameters from file
			mapWidth = data.getInt(0);
			mapHeight = data.getInt(4);
			cellWidth = data.getInt(8);
			cellHeight = data.getInt(12);
			startChar = data.get(16);
			charWidths = new int[256];
			for (int i=0; i<256; i++){
				charWidths[i] = data.get(i+17);
			}
			//calculate parameters for drawing
			rowPitch = mapWidth / cellWidth;
			rowFactor = cellWidth / mapWidth;
			colFactor = cellHeight / mapHeight;
			map = new Texture(texturePath.toString());
			if (map.getWidth() != mapWidth || map.getHeight()!=mapHeight){
				System.out.println("WARNING: Image size not equals data size for font:"+fontName);
			}
		} catch (IOException e) {
			System.out.println("Can't read data for font "+fontName);
			e.printStackTrace();
		}
	}
	
	protected Text newTextObject(String text){
		throw new NotImplementedException();
	}
	
	protected void changeText(Text text, String newText){
		throw new NotImplementedException();
	}
	
	public void freeResources(){
		//TODO delete image data from gpu memory;
		throw new NotImplementedException();
	}

	public int getTextureID(){
		return map.getID();
	}
	
	@Override
	public String toString() {
		return "TextRenderer [mapWidth=" + mapWidth + ", mapHeight=" + mapHeight + ", cellWidth=" + cellWidth
				+ ", cellHeight=" + cellHeight + ", startChar=" + startChar + ", rowPitch=" + rowPitch + ", rowFactor="
				+ rowFactor + ", colFactor=" + colFactor + "]";
	}
}
