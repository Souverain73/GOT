package DGE.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import DGE.Constants;
import DGE.utils.Utils;


public class GraphicModule {
	private static GraphicModule _instance = null;

	private int vao;
	private static ICamera camera;
	private static Effect effect;

	private GraphicModule() {
		camera = new Ortho2DCamera();
		camera.windowResizeCallback(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
	}

	public void clear(){
	    glClear(GL_COLOR_BUFFER_BIT);
	}
	
	public static GraphicModule instance() {
		if (_instance == null) {
			_instance = new GraphicModule();
		}
		return _instance;
	}
	
	public static void resizeCallback(long window, int w, int h){
		glViewport(0, 0, w, h);
		camera.windowResizeCallback(w, h);
	}
	
	public void initOpenGl(){
		Texture.init();
	    vao = glGenVertexArrays();
	    glBindVertexArray(vao);
	    glClearColor(0.5f, 0.5f, 0f, 1.0f);
	}
	
	
	public static int createBuffer(float[] data){
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(data.length);
		dataBuffer.put(data);
		dataBuffer.flip();
		
		int resultBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, resultBuffer);
			glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, resultBuffer);
		return resultBuffer;
	}
	
	public static int createShader(int eShaderType, String fnameShaderFile) {
			String strShaderText = null;
			try{
				strShaderText = Utils.readFile(fnameShaderFile);
			}catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
	        int shader = glCreateShader(eShaderType);
	        glShaderSource(shader, strShaderText);

	        glCompileShader(shader);

	        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
	        if (status == 0) {
	          int infoLogLength = glGetShaderi(shader, GL_INFO_LOG_LENGTH);

	          String strInfoLog = glGetShaderInfoLog(shader, infoLogLength);

	          String strShaderType = null;
	         switch (eShaderType) {
	         case GL_VERTEX_SHADER:
	            strShaderType = "vertex";
	            break;
	         case GL_GEOMETRY_SHADER:
	            strShaderType = "geometry";
	            break;
	         case GL_FRAGMENT_SHADER:
	            strShaderType = "fragment";
	            break;
	         }

	          System.err.printf("Compile failure in %s shader:\n%s\n", strShaderType, strInfoLog);
	        }
	        
	      return shader;
	   }
	   	   
	public static int createProgram(ArrayList<Integer> shaderList) {
	      int program = glCreateProgram();

	      for (Integer shader : shaderList) {
	         glAttachShader(program, shader);
	      }

	      glLinkProgram(program);
	      
	      int status = glGetProgrami(program, GL_LINK_STATUS);
	      if (status == 0) {
	         int infoLogLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);

	         String strInfoLog = null;
	         strInfoLog = glGetProgramInfoLog(program, infoLogLength);
	         
	         System.err.printf("Linker failure: %s\n", strInfoLog);
	      }
	      
	      for (Integer shader : shaderList) {
	         glDetachShader(program, shader);
	      }

	      return program;
	   }
	
	public ICamera getCamera(){
		return camera;
	}
	
	public void setEffect(Effect eff){
		effect = eff;
	}
	
	public void resetEffect(){
		effect = null;
	}
	
	public Effect getEffect(){
		return effect;
	}
}
