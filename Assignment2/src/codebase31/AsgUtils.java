package codebase31;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

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
	 * Simple code to get mac address of pc running application
	 * http://www.mkyong.com/java/how-to-get-mac-address-in-java/
	 * 
	 * @return
	 */
	public static String getMac() {
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
	 * Removes the first "-" from mac address to turn it into an appropriate key
	 * size for ENC/DEC
	 * 
	 * @param mac
	 * @return
	 */
	public static String shortenMac(String mac) {
		return mac.replaceFirst("-", "");

	}

	public static RSAPrivateKey getPrivateKey(String path) {
		InputStream ins = null;
		try {
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
			RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
			ins.close();
			return privateKey;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static RSAPublicKey getPublicKey(String path) {

		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(fis);

			RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
			return publicKey;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getUserFromCertificate(String path) {
		InputStream ins = null;
		try {
			ins = new FileInputStream(path);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = ins.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] keyArr = buffer.toByteArray();

			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(keyArr);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
			String name = cert.getSubjectX500Principal().getName();
			String username = "";
			LdapName ldapName = null;
			try {
				ldapName = new LdapName(name);
			} catch (InvalidNameException e) {
				throw new RuntimeException(e);
			}
			for (Rdn rdn : ldapName.getRdns()) {
				String type = rdn.getType();
				if ("CN".equals(type)) {
					username = (String) rdn.getValue();
				}
			}

			return username;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encrypt(String text, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA");
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	public static String decrypt(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance("RSA");

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);
			
			return new String(dectyptedText);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}		
	}

	public static byte[] getFileAsByteArray(String path) {
		InputStream ins = null;
		try {
			ins = new FileInputStream(path);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = ins.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			byte[] fileByteArray = buffer.toByteArray();
			return fileByteArray;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String parseCertficateToString(byte[] bytes) {

		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(bytes);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
			String name = cert.getSubjectX500Principal().getName();
			String username = "";
			LdapName ldapName = null;
			try {
				ldapName = new LdapName(name);
			} catch (InvalidNameException e) {
				throw new RuntimeException(e);
			}
			for (Rdn rdn : ldapName.getRdns()) {
				String type = rdn.getType();
				if ("CN".equals(type)) {
					username = (String) rdn.getValue();
				}
			}

			return username;
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
