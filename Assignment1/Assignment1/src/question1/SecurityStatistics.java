package question1;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityStatistics {
	public static final String STUDENT_ID = "21234567";
	
	public static void main(String[] args) {
		System.out.println(getVideoRank(STUDENT_ID));
		
	}
	
	public static int getVideoRank(String studentId) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(new Integer(studentId).toString().getBytes());
			BigInteger bi = new BigInteger(1,md.digest());
			return bi.mod(new BigInteger("9890"))
			.multiply(new BigInteger("100"))
			.intValue()+1000;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

}
