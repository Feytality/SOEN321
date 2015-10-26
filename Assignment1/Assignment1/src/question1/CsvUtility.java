package question1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVWriter;

/**
 * Class which is responsible with manipulating the data from the
 * CSV file.
 *  
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class CsvUtility {
	
	// Instance variables
	
	/** 
	 * Will contain all the data in the CSV file. 
	 * Rank is the key, value is the site.
	 */
	private Map<Integer, String> csvDAO = null;
	
	// Constructor

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
	
	/**
	 * Loads the CSV file data into a Map object which
	 * can be used to look up websites according to their rank.
	 */
	@SuppressWarnings("resource")
	public void loadCsvDAO(int range1, int range2, int range3){
		final String fileName = "./src/question1/top-1m_13-10-15.csv";
		try {
			String line = "";
			FileInputStream fis = new FileInputStream(fileName);
	    
			BufferedReader myInput = new BufferedReader(new InputStreamReader(fis));
	    
			while ((line = myInput.readLine()) != null) {
			    String[] tokenVals = line.split(",");
			    int rank = Integer.parseInt(tokenVals[0]);
			    if ((rank >= range1 && rank < range1+999) || 
			    	(rank >= range2 && rank < range2+9999) ||
			    	(rank >= range3 && rank < range3+9999))
			    	csvDAO.put(rank, tokenVals[1]);
	        }
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not find the specified file! Please be sure to get the latest"
					+ " version of the top million websites.");
			fnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("Successfully loaded CVS file.");
	    writeCvs(1, 1000); // TODO remove this line, only here for now to test the function.
	}
	
	// TODO rewrite this method to not use the opencsv dependency
	/**
	 * Writes to a CVS file about the different information about websites.
	 * Each line in the CVS file will look like the following:
	 * 
	 * rank,domain,isHTTPS,SSLversion,key-type,key-size,signature-algorithm,isHSTS,isHSTSlong
	 * 
	 * @param	startRange	Starting rank of the websites to verify
	 * @param 	endRange	Ending rank of the websites to verify
	 */
	public void writeCvs(int startRange, int endRange) {
		// TODO Append to the end of the file for each range
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter("./src/question1/output.csv"), '\n');
			
			int counter = startRange;
			do {
				// TODO replace with toString for resultline object
				String[] line = new String[1];
				line[0] = counter + ","  + csvDAO.get(counter) + ","; //+ usesHttps(.get(counter));
				System.out.println(line[0]);
				
				writer.writeNext(line);
				counter++;
			} while (counter <= endRange);
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
