package DGE.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.joml.Vector2f;;


public class Utils {
	public static Charset charset = Charset.forName("windows-1251");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();
	
	public static ByteBuffer strToBb(String str){
		try{
			return encoder.encode(CharBuffer.wrap(str)).put((byte)0).order(ByteOrder.nativeOrder());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String bbToStr(ByteBuffer bb){
		try{
			return (decoder.decode(bb)).toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static public String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded);
	}
	
	static public boolean pointInRect(Vector2f point, Vector2f rectPos, Vector2f rectDim){
		if (point.x < rectPos.x) return false;
		if (point.x >= rectPos.x+rectDim.x) return false;
		if (point.y < rectPos.y) return false;
		if (point.y >= rectPos.y+rectDim.y) return false;
		return true;
	}
}
