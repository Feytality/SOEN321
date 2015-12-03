package codebase21;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AsgUtils {
	/**
	 * Hashes a password and a nonce using md5, fast and effective
	 * 
	 * @param passwordToHash
	 * @param nonce
	 * @return
	 */
	public static String simpleHash(String passwordToHash, int nonce) {

		String comboPass = nonce + passwordToHash;
		String generatedPassword = null;
		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(comboPass.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			// hex hash
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
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
			// System.err.println(DatatypeConverter.printBase64Binary(encrypted));
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
			//System.err.println(decrypted);
			return decrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	

}
