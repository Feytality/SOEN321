package question1;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import question1.CsvUtility;

/**
 * Application which determines the security level of websites. For the purposes
 * of this specific implementation, the program reads from a CSV file containing
 * the stop one million website. In this case, this file was downloaded from the 
 * following site: http://s3.amazonaws.com/alexa-static/top-1m.csv.zip on October
 * 13th, 2015.
 * The application will load this CSV file, and evaluate the sites within given 
 * ranges.  It will evaluate the websites based on security attributes. Depending
 * on the security attributes available to a certain website, this website will be
 * given a security level ranking.  The higher the security level, the more secure
 * the website is based on the possible security attributes.
 * 
 * For SOEN 321.
 * 
 * 
 * @authors Felicia Santoro-Petti - 26619657
 * 			Daniel Caterson - 29746277
 * 
 * Date Created: October 13th, 2015
 * Date Last Modified: October 31st, 2015
 * 
 * Assignment Number: 1
 * Question Number: 2
 *
 */
public class SecurityStatistics {
	// Student IDs used to get range starts.
	public static final String STUDENT_ID_1 = "26619657";
	public static final String STUDENT_ID_2 = "29746277";
	
	/**
	 * This is the driver class that contains the Main method required to run the
	 * application. 
	 * It makes calls to the CSV Utility class which will load the websites into
	 * memory. It will then evaluate each website based on the required ranges.
	 * (1-1000, 56300-66299, ) 
	 *
	 * @param	args	Command line arguments needed for the JVM, but not used in
	 * 					this application.
	 */
	public static void main(String[] args) {
		// Re-enabled RC4
		java.security.Security.setProperty("jdk.tls.disabledAlgorithms", "");

		// First range is the range the whole class must analyze: 1 to 1000
		int startRange1 = 1; 
		int endRange1 = 1000;
		
		// Second range based on first student ID: range2 to range2+9999
		int startRange2 = getVideoRank(STUDENT_ID_1); 
		int endRange2 = startRange2 + 9999;
		
		// Third range based on second student ID: range3 to range3+9999
		int startRange3 = getVideoRank(STUDENT_ID_2);
		int endRange3 = startRange3 + 9999;
		
		System.out.println("This program will be working with websites with ranges: " + 
				startRange1 + "-" + endRange1 + ", " + 
				startRange2 + "-" + endRange2 + ", " +
				startRange3 + "-" + endRange3);
		System.out.println();
		
		// Create CVS Utility object to load the CSV file into memory.
		CsvUtility cvs = new CsvUtility();
		cvs.loadCsvDAO(startRange1, startRange2, startRange3);
		
		// Evaluate the websites based on the 3 different ranges.
		
		// Uses range 1-1000 which every student in the class must evaluate.
		/*for(int i = 431; i < 1000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}*/
		
		// Uses ranges starting from the rank for STUDENT_ID_1 to STUDENT_ID_1 + 10,000
		//for(int i = 58337; i < getVideoRank(STUDENT_ID_1) + (10000-2037); i++) {
		for(int i = 62244; i < 62299; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}

		// Uses ranges starting from the rank for STUDENT_ID_1 to STUDENT_ID_1 + 10,000
		/*for(int i = getVideoRank(STUDENT_ID_2); i < getVideoRank(STUDENT_ID_2) + 10000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}*/
	}
	
	/**
	 * Determines which websites the application is to verify.
	 * Gives an integer will will be the beginning of the range.
	 * 
	 * @param	studentId	Student ID of the students writing the program.
	 * 
	 * @return	A value relating to the rank the websites.
	 */
	public static int getVideoRank(String studentId) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(new Integer(studentId).toString().getBytes());
			BigInteger bi = new BigInteger(1,md.digest());
			return bi.mod(new BigInteger("9890")).multiply(new BigInteger("100")).intValue()+1000;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
