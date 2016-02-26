package manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileManager {
	
	public static String openFile(File file) {
		String str = "";
		try {
			Scanner in = new Scanner(new FileReader(file));
			while(in.hasNextLine())
				str += in.nextLine() + "\n";
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static void saveFile(File file, String text) {
		if(!file.getName().endsWith(".txt"))
			file = new File(file.getPath() + ".txt");
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			out.write(text);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
