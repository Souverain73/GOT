package got.graphics;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Text {
	
	//TODO implement word wrap and bounding box.
	private float[] vertexCoords;
	private float[] UVCoords;
	private int glyphs;
	private int vertexCoordsBuffer;
	private int vertexUVBuffer;
	private static int drawProgram;
	private static int textureSampler;
	private static int mvLocation;
	private static Matrix4f mv;
	private static int projectionLocation;
	
	private static final String vertexShaderFileName = "data/shaders/text.vertexshader";
	private static final String fragmentShaderFileName = "data/shaders/text.fragmentshader"; 
			
	
	private FloatBuffer mvFB;
	private FloatBuffer pm;
	
	private String text;
	private Font font;
	
	public static Text newInstance(String text, Font font){
		return font.newTextObject(text);
	}
	
	public void changeText(String newText){
		font.changeText(this, newText);
		text = newText;
	}
	
	protected Text(){
		mvFB = BufferUtils.createFloatBuffer(16);
		vertexCoordsBuffer = GraphicModule.createEmptyBuffer();
		vertexUVBuffer = GraphicModule.createEmptyBuffer();
		glyphs = 0;
	}
	
	protected Text(float[] vertexCoords, float[] UVCoords, int glyphs, Font font){
		this();
		this.vertexCoords = vertexCoords;
		this.UVCoords = UVCoords;
		this.glyphs = glyphs;
		this.font = font;
		vertexCoordsBuffer = GraphicModule.setBufferData(vertexCoordsBuffer, vertexCoords);
		vertexUVBuffer = GraphicModule.setBufferData(vertexUVBuffer, UVCoords);
	}
	
	protected static void init(){
		//init buffers
		  glEnable(GL_TEXTURE_2D);
		//init program
		  ArrayList<Integer> shaders = new ArrayList<Integer>();
		  shaders.add(GraphicModule.createShader(GL_VERTEX_SHADER, vertexShaderFileName));
		  shaders.add(GraphicModule.createShader(GL_FRAGMENT_SHADER, fragmentShaderFileName));
		  drawProgram = GraphicModule.createProgram(shaders);
		  textureSampler = glGetUniformLocation(drawProgram, "myTextureSampler");
		  mvLocation = glGetUniformLocation(drawProgram, "MV");
		  projectionLocation = glGetUniformLocation(drawProgram, "Proj");
		  mv = new Matrix4f().identity();
	}
	
	public Font getFont() {
		return font;
	}

	protected void setFont(Font font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public int getLength() {
		return glyphs;
	}

	protected void setGlyphs(int glyphs) {
		this.glyphs = glyphs;
	}

	protected void setVertexCoords(float[] vertexCoords) {
		this.vertexCoords = vertexCoords;
		vertexCoordsBuffer = GraphicModule.setBufferData(vertexCoordsBuffer, this.vertexCoords);
	}

	protected void setUVCoords(float[] UVCoords) {
		this.UVCoords = UVCoords;
		vertexUVBuffer = GraphicModule.setBufferData(vertexUVBuffer, this.UVCoords);
	}

	public void draw(float x, float y, float w, float h, DrawSpace space){
		this.draw(x, y, w, h, 0, space);
	}
	
	public void draw(float x, float y, float w, float h, float z, DrawSpace space){
		//TODO: draw text here;
		mv.identity().translate(x,y,-z).scale(w, h, 1).get(mvFB);
		if (space == DrawSpace.WORLD)
			pm = GraphicModule.instance().getCamera().getProjectionAsFloatBuffer();
		else
			pm = GraphicModule.instance().getScreenProjectionAsFloatBuffer();
		
	    glEnable(GL_BLEND);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
		glUseProgram(drawProgram);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, font.getTextureID());
		
		glUniform1i(textureSampler, 0);
		glUniformMatrix4fv(mvLocation, false, mvFB);
		glUniformMatrix4fv(projectionLocation, false, pm);
		
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexCoordsBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vertexUVBuffer); 
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		glDrawArrays(GL_TRIANGLES, 0, glyphs * 6);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glDisable(GL_BLEND);
		
	}
	
}
