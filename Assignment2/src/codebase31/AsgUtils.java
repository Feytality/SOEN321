package codebase31;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
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

	
	/**
	 * Simple code to get mac address of pc running application
	 * http://www.mkyong.com/java/how-to-get-mac-address-in-java/
	 * @return 
	 */
	public static String getMac(){
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
		
		
		
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			
		byte[] mac = network.getHardwareAddress();
			
			
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Removes the first "-" from mac address to turn it into an appropriate key size for ENC/DEC
	 * @param mac
	 * @return
	 */
	public static String shortenMac(String mac){
		return mac.replaceFirst("-", "");		
		 
	}
	
	public static RSAPrivateKey loadPrivateKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		InputStream ins = null;		
		ins = new FileInputStream(path);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = ins.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] keyArr = buffer.toByteArray();
		KeyFactory kf = KeyFactory.getInstance("RSA");
		KeySpec keySpec = new PKCS8EncodedKeySpec(keyArr);
		RSAPrivateKey privateKey =(RSAPrivateKey) kf.generatePrivate(keySpec);
		ins.close();		
		return privateKey;
	}
	
}
