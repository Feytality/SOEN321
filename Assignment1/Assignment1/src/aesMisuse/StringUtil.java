package aesMisuse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Facilitates the string to byte[] and vice versa operations
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public  class StringUtil {	
	/**
	 * turns strings into byte arrays with UTF* encoding. will truncate is a certain length requested
	 * @param str
	 * @param size
	 * @return
	 */
	public static byte[] StrToByte(String str,int size){
		byte [] convertedString=null;
		convertedString= str.substring(0, size).getBytes(StandardCharsets.UTF_8);
		return convertedString;
	}
	/**
	 * Simple per index xor
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static byte[] XOR(byte[]b1,byte[] b2){
		byte[] result=new byte[b1.length];
		
		for(int i= 0; i<b1.length;i++){
			result[i] = (byte) (b1[i]^b2[i]);			
		}
		return result;
	}
	/**
	 * debug method, used for printing out strings that are now in byte array form
	 * @param arr
	 */
	public static void printbytearr(byte[] arr){
		System.out.println("");
		for (int i = 0; i < arr.length; i++) {
			System.out.print((char)arr[i]);
		}
		
	}
	
	/**
	 * Returns the string representation of a byte array
	 * @param arr
	 * @return
	 */
	public static String toString(byte[] arr){
		String build="";
		for (int i = 0; i < arr.length; i++) {
			build+=(char)arr[i];
		}
		
	return build;
	}
	/**
	 * Decode  stream in base 64
	 * @param text
	 * @return
	 */
	public static byte[] decode(byte[] text){		
		  // Get bytes from string
		byte[] decoded=null;
		
		try {
			decoded = Base64.decode(text);			
			
		} catch (Base64DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decoded;
	}
		/**
		 *  encode  stream in base 64
		 * @param text
		 * @return
		 */
	public static String encode(byte[] text){		
		  // Get bytes from string
		String encoded=null;		
		encoded = Base64.encode(text);
		return encoded;
	}
		
	
}
