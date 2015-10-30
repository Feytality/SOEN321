package aesMisuse;

import java.util.Arrays;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class AesMisuse {
	public static void main(String[] args) {   
		String string = "40mO35Yj9cAMFaaOcshT10VwVw6WmbvAEyrI6TxElFY=";
	
		  // Get bytes from string
		byte[] byteArray;
		
		try {
			byteArray = Base64.decode(string.getBytes());
			// Print the decoded array
			System.out.println(Arrays.toString(byteArray));
			// Print the decoded string 
			String decodedString = new String(byteArray);
			System.out.println(string + " = " + decodedString);
		} catch (Base64DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
