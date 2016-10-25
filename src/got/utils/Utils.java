package got.utils;

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
	
	
	/**
	 * Reads file into String;
	 * @param path - path to file
	 * @return all file data as encoded string.
	 * @throws IOException
	 */
	static public String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded);
	}
	
	/**
	 * Check if point in rectangle
	 * @param point
	 * @param rectPos - top left corner of rectangle
	 * @param rectDim - width and height of rectangle
	 */
	static public boolean pointInRect(Vector2f point, Vector2f rectPos, Vector2f rectDim){
		if (point.x < rectPos.x) return false;
		if (point.x >= rectPos.x+rectDim.x) return false;
		if (point.y < rectPos.y) return false;
		if (point.y >= rectPos.y+rectDim.y) return false;
		return true;
	}
	
	
	/**
	 * Calculate distance between two points
	 * @param p1
	 * @param p2
	 * @return distance between p1 and p2
	 */
	static public float distance(Vector2f p1, Vector2f p2){
		return (float)Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
	}
}
