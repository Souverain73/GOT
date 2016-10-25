package got.utils;

import javax.swing.JOptionPane;

public class UI {
	
	public static String getString(String title, String label, String def){
		String input = (String)JOptionPane.showInputDialog(null, label, title, 
				JOptionPane.QUESTION_MESSAGE, null, null ,def);
		return input.trim();
	}
	
	public static void serverMessage(String message){
		System.out.println("[Server]:"+message);
	}
	
	public static void systemMessage(String message){
		System.out.println("[System]:"+message);
	}
}
