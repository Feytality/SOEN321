package aesMisuse;

import java.io.UnsupportedEncodingException;

/**
 * Facilitates the string to byte[] operations
 * @author Cat
 *
 */
public  class StringUtil {

	
	/**
	 * turns strings into byte arrays. will truncate is a certain lenght requested
	 * @param str
	 * @param size
	 * @return
	 */
	public static byte[] StrToByte(String str,int size){
		byte [] convertedString=null;
		try {
			convertedString= str.substring(0, size).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			System.out.print(arr[i]);
		}
		
	}
}
