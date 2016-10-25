package got.utils;

import javax.swing.JOptionPane;

public class GUI {
	public static void main(String[] args){
		System.out.println(getString("Input Host", "Host:", "localhost"));
	}
	
	public static String getString(String title, String label, String def){
		String input = (String)JOptionPane.showInputDialog(null, label, title, 
				JOptionPane.QUESTION_MESSAGE, null, null ,def);
		return input.trim();
	}

}
