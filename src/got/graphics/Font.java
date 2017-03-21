package got.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Font{
	
	private static final String fontsBase = "data/fonts/";
	private String name;
	private int mapWidth, mapHeight;
	private int cellWidth, cellHeight;
	private int startChar;
	private int[] charWidths;
	private int rowPitch;
	private float rowFactor;
	private float colFactor;
	private Texture map;
	private int size = 32;
	private int graphSize;
	private final int spacing = 0;
	private final LocaleEncoder encoder;
	
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
	public Font(String fontName, LocaleEncoder encoder, int size){
		this.name = fontName;
		this.encoder = encoder;
		this.size = size;
		Path dataPath = Paths.get(fontsBase+fontName+".dat");
		Path texturePath = Paths.get(fontsBase+fontName+".png");

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
			rowFactor = cellWidth*1.0f / mapWidth;
			colFactor = cellHeight*1.0f / mapHeight;
			graphSize = cellWidth;

			//read charset map
			map = new Texture(texturePath.toString());
			if (map.getWidth() != mapWidth || map.getHeight()!=mapHeight){
				System.out.println("WARNING: Image size not equals data size for font:"+fontName);
			}
		} catch (IOException e) {
			System.out.println("Can't read data for font "+fontName);
			e.printStackTrace();
		}
	}

	public Font(String name) {
		this(name, new RussianEncoder(), 32);
	}

	public Font(String name, int size){
		this(name, new RussianEncoder(), size);
	}

	protected static void init(){
		
	}
	
	protected Text newTextObject(String text){
		float x = 0;
		float y = 0;
		int length = text.length();

		
		float [] UV = new float[length*12];
		float [] pos = new float[length*18];
		
		for (int i=0; i<length; i++){
			char code = encoder.encode(text.charAt(i));
			addGplyphUV(code, i, UV);
			addGlyphPos(x, y, i, pos);
			x+=charWidths[code] * (float)size/(float)graphSize + spacing;
		}

		
		return new Text(pos, UV, length, this);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	private void addGplyphUV(char c, int index, float[] UV){
		int pos = c - startChar;
		int row = pos / rowPitch;
		int col = pos - (row*rowPitch);
		float top = row * rowFactor;
		float left = col * colFactor;
		float right = left + colFactor;
		float bottom = top + rowFactor;
		
		int i = index * 12;  //One glyph needs two triangles. One triangle needs 3 points. One point need 2 floats. 2*3*2 = 12
		
		UV[i++] = right;	UV[i++] = bottom;
		UV[i++] = right; 	UV[i++] = top;
		UV[i++] = left;		UV[i++] = top;
		UV[i++] = left; 	UV[i++] = top;
		UV[i++] = left; 	UV[i++] = bottom;
		UV[i++] = right;	UV[i++] = bottom;
	}
	
	private void addGlyphPos(float x, float y, int index, float[] pos){
		float top = y;
		float left = x;
		float bottom = top + size;
		float right = left + size;
		
		int i = index * 18;	//One glyph needs two triangles. One triangle needs 3 points. One point need 3 floats. 2*3*3 = 18
		
		
		pos[i++] = right;	pos[i++] = bottom; 	pos[i++] = 0;
		pos[i++] = right; 	pos[i++] = top;		pos[i++] = 0;
		pos[i++] = left;	pos[i++] = top;		pos[i++] = 0;
		pos[i++] = left; 	pos[i++] = top;		pos[i++] = 0;
		pos[i++] = left; 	pos[i++] = bottom;	pos[i++] = 0;
		pos[i++] = right;	pos[i++] = bottom;	pos[i++] = 0;
	}
	
	protected void changeText(Text text, String newText){
		float x = 0;
		float y = 0;
		int length = newText.length();

		
		float [] UV = new float[length*12];
		float [] pos = new float[length*18];
		
		for (int i=0; i<length; i++){
			char code = encoder.encode(newText.charAt(i));
			addGplyphUV(code, i, UV);
			addGlyphPos(x, y, i, pos);
			x+=charWidths[code] + spacing;
		}
		
		text.setFont(this);
		text.setGlyphs(length);
		text.setUVCoords(UV);
		text.setVertexCoords(pos);
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
		return "Font [name=" + name + ", mapWidth=" + mapWidth + ", mapHeight=" + mapHeight + ", cellWidth=" + cellWidth
				+ ", cellHeight=" + cellHeight + ", startChar=" + startChar + ", size=" + size + ", spacing=" + spacing
				+ "]";
	}

	private interface LocaleEncoder{
		char encode(char charCode);
	}

	public static class RussianEncoder implements LocaleEncoder {
		@Override
		public char encode(char charCode) {
			if (charCode > 1039 && charCode < 1104){
				return (char) (charCode - 848);
			}
			return charCode;
		}
	}
}
