package question1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which is responsible with manipulating the data from the
 * CSV file. It will load the CSV file into a Map containing all the
 * websites listed in the CSV file, so that these websites can be easily 
 * accessed in the application.
 *  
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class CsvUtility {
	
	// Data Memebers
	
	/** 
	 * Will contain all the data in the CSV file. 
	 * Rank is the key, value is the site.
	 */
	private Map<Integer, String> csvDAO = null;
	
	// Constructors

	/**
	 * No parameter constructor which creates a Map with
	 * an Integer key and a String value.
	 */
	public CsvUtility() {
		csvDAO = new HashMap<Integer, String>();
	}
	
	// Setters and Getters
	
	/**
	 * Gets the CsvDAO which contains all the information from
	 * the CSV file.
	 * 
	 * @return	The map containing the key-value object of the
	 * 			websites in the CSV file.
	 */
	public Map<Integer, String> getCsvDAO() {
		return csvDAO;
	}

	/**
	 * Sets the csvDAO instance variable.
	 * 
	 * @param	csvDAO	Key-value pair which represents a website's 
	 * 					rank and domain.
	 */
	public void setCsvDAO(Map<Integer, String> csvDAO) {
		this.csvDAO = csvDAO;
	}
	
	// Member Methods
	
	/**
	 * Loads the CSV file data into a Map object which can be used
	 *  to look up websites according to their rank.
	 * It takes into account the 3 necessary ranges that the program
	 * must evaluate the list of 1 million websites by; 1-1000,
	 * and the ranges obtained by our student number + 9999.
	 * 
	 * @param 	rangeStart1	This is the range which everyone in the class will
	 * 						have the same, 1-1000, so range1 will be 1.
	 * @param 	rangeStart2	The range associated with our student number
	 * @param 	rangeStart3	The range associated with our student number.
	 */
	@SuppressWarnings("resource")
	public void loadCsvDAO(int rangeStart1, int rangeStart2, int rangeStart3){
		final String fileName = "./src/question1/top-1m_13-10-15.csv";
		try {
			String line = "";
			// Open the CSV file and read from it.
			BufferedReader fileContents = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
	    
			// Iterate over every line in the CSV file.
			while ((line = fileContents.readLine()) != null) {
			    String[] tokenVals = line.split(",");
			    int rank = Integer.parseInt(tokenVals[0]);
			    // If it falls within the given ranges, add it to the website map.
			    if ((rank >= rangeStart1 && rank <= rangeStart1+999) || 
			    	(rank >= rangeStart2 && rank <= rangeStart2+9999) ||
			    	(rank >= rangeStart3 && rank <= rangeStart3+9999))
			    	csvDAO.put(rank, tokenVals[1]);
	        }
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not find the specified file! Please be sure to get the latest"
					+ " version of the top million websites.");
		} catch (IOException e) {
			System.out.println("There was a problem reading the CSV file containing the top million websites.");
		}
	    System.out.println("Successfully loaded CVS file.");
	}
	
}
