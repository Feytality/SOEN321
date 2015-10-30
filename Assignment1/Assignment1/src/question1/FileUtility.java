package question1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileUtility {
	private static PrintWriter writer;
	
	public void open(){
		try {
			writer = new PrintWriter(new FileOutputStream(
				    new File("./src/question1/certificate-info.txt"), true));
		} catch (FileNotFoundException e) {
			System.out.println("Could not open the output file.");
		}
	}
	
	public void close() {
		writer.close();
	}
	
	public PrintWriter getWriter() {
		return writer;
	}

}
