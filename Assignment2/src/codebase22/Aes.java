package codebase22;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Aes {
	
	public Aes() {
		
	}
	
	/**
	 * Encrypt any text using AES 
	 * currently has hard coded iv, if it were in the scope of project we would provide a more secure IV
	 * key is 128 bits, can use a string of length16
	 * @param text
	 * @param key128bit
	 * @return
	 */
		public static String encrpyt(String text, String key128bit) {
			try {
				
				String ivec = "1234567890123456";
				IvParameterSpec iv = new IvParameterSpec(ivec.getBytes("UTF-8"));
				// Create key and cipher
				Key aesKey = new SecretKeySpec(key128bit.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				// encrypt the text
				cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
				byte[] encrypted = cipher.doFinal(text.getBytes());
				Base64.getEncoder().encode(encrypted);
				return DatatypeConverter.printBase64Binary(encrypted);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		/**
		 * Decrypt any text using AES 
		 * currently has hard coded iv, if it were in the scope of project we would provide a more secure IV
		 * key is 128 bits, can use a string of length16
		 * @param text
		 * @param key128bit
		 * @return
		 */
		public static String decrypt(String text, String key128bit) {
			try {
				String ivec = "1234567890123456";
				IvParameterSpec iv = new IvParameterSpec(ivec.getBytes("UTF-8"));
				Key aesKey = new SecretKeySpec(key128bit.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
				byte[] original = DatatypeConverter.parseBase64Binary(text);
				// decrypt the text
				String decrypted = new String(cipher.doFinal(original));
				return decrypted;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
}
