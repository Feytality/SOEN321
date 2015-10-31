package question1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * The purpose of this class is to represent the file writer for
 * the output file which we write all our Result lines. 
 * The file created is appended to and is called 'certificate-info.text'.
 * 
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class FileUtility {
	// Data Members - Attributes
	
	/**
	 * The writer object which will write to the output file.
	 */
	private static PrintWriter writer;
	
	// Member methods
	
	/**
	 * Creates a new file named 'certificate-info.txt' if it is not
	 * already created. If it is created, will retrieve the file in
	 * append mode.
	 */
	public void open(){
		try {
			writer = new PrintWriter(new FileOutputStream(
				    new File("./src/question1/certificate-info-first.txt"), true));
		} catch (FileNotFoundException e) {
			System.out.println("Could not open the output file.");
		} 
	}
	
	/**
	 * Closes the writer object to release connection to the writer.
	 */
	public void close() {
		writer.close();
	}
	
	/**
	 * Gets the writer object so it can be used to write to the file.
	 * 
	 * @return	writer	The writer object to use to write to the file
	 */
	public PrintWriter getWriter() {
		return writer;
	}

}
