package question1;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import question1.CsvUtility;

/**
 * Main
 * 
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class SecurityStatistics {
	// TODO Empty the generated file every time the application starts
	public static final String STUDENT_ID_1 = "26619657";
	public static final String STUDENT_ID_2 = "29746277";
	
	public static void main(String[] args) {
		// Necessary for re-enabling RC4.
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
		CsvUtility cvs = new CsvUtility();
		
		cvs.loadCsvDAO(startRange1, startRange2, startRange3);
		
		// Uses range 1-1000 which every student in the class must evaluate.
		/*for(int i = 1; i < 1000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}*/
		
		// Uses ranges starting from the rank for STUDENT_ID_1 to STUDENT_ID_1 + 10,000
		for(int i = getVideoRank(STUDENT_ID_1); i < getVideoRank(STUDENT_ID_1) + 10000; i++) {
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
