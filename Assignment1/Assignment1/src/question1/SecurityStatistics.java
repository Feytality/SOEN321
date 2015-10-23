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
		
		System.out.println(getVideoRank(STUDENT_ID_1));
		System.out.println(getVideoRank(STUDENT_ID_2));
		
		CsvUtility cvs = new CsvUtility();
		
		cvs.loadData();
		
		// Uses the ranges.
		for(int i = 1; i < 1000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}
		
		for(int i = getVideoRank(STUDENT_ID_1); i < getVideoRank(STUDENT_ID_1)+1000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}
		
		for(int i = getVideoRank(STUDENT_ID_2); i < getVideoRank(STUDENT_ID_2)+1000; i++) {
			SSLClient client = new SSLClient(i, cvs.getCsvDAO().get(i)); 
			client.getSiteInfo();
		}
	}
	
	/**
	 * Determines which websites the application is to verify.
	 * Gives an integer will will be the beginning of the range.
	 * 
	 * @param	studentId	Student ID of the students writing the program.
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
