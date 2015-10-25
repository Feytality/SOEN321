package question1;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * Class which is responsible with manipulating the data from the
 * CSV file.
 *  
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class CsvUtility {
	
	// Will contain all the data in the CSV file. 
	// Rank is the key, value is the site.
	Map<Integer, String> csvDAO = null;
	
	// Object which reads CSV files
	CSVReader reader = null;
	
	/**
	 * No parameter constructor which creates a Map with
	 * an Integer key and a String value.
	 */
	public CsvUtility() {
		csvDAO = new HashMap<Integer, String>();
	}
	
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
	public void loadData() {
		System.out.println("Loading the CVS file...");
		
		// Object which reads CSV files
		CSVReader reader = null;
        try {
            // Get the CSVReader instance with specifying the delimiter used in the file
            reader = new CSVReader(new FileReader("./src/question1/top-1m_13-10-15.csv"), '\n');
            
            // Read CSV one line at a time and populate Map object
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for(String token : nextLine) {
                	// Get a String array with the rank value and site url.
                	String[] tokenVals = token.split(",");
                	csvDAO.put(new Integer(tokenVals[0]), tokenVals[1]);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Successfully loaded CVS file.");
        writeCvs(1, 10000);
	}
	
	/**
	 * Writes to a CVS file about the different information about websites.
	 * Each line in the CVS file will look like the following:
	 * 
	 * rank,domain,isHTTPS,SSLversion,key-type,key-size,signature-algorithm,isHSTS,isHSTSlong
	 * 
	 * @param	startRange	Starting rank of the websites to verify
	 * @param 	endRange	Ending rank of the websites to verify
	 */
	public void writeCvs(int startRange, int endRange) {// this is DUMB
		// TODO Append to the end of the file
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter("./src/question1/output.csv"), '\n');
			
			int counter = startRange;
			do {
				// TODO replace with toString for resultline object
				String[] line = new String[1];
				line[0] = counter + ","  + csvDAO.get(counter) + ","; //+ usesHttps(csvDAO.get(counter));
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
