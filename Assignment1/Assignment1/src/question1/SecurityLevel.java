package question1;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the SecurityLevel class which contains all the objects which help determine the
 * rank of a given site using hash maps. The higher the rank, the more secure it is. It is also
 * responsible for taking a ResultLine and tallying up its' respective security level.
 * 
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
@SuppressWarnings("serial")
public class SecurityLevel {
	// Data Members - Attributes
	// Objects which determine the security level depending on the value.
	/**
	 * Determines levels for websites with or without HTTPS.
	 */
	private static Map<Boolean, Integer> https = new HashMap<Boolean, Integer>(){
		{
			put(true, 2);
			put(false, 1);
		}
	};

	/**
	 * Determines levels for websites with or without HSTS.
	 */
	private static Map<Boolean, Integer> hsts = new HashMap<Boolean, Integer>(){
		{
			put(true, 3);
			put(false, 1);
		}
	};
	
	/**
	 * Determines levels for the SSL version used in the website.
	 */
	private static Map<String, Integer> sslVersion = new HashMap<String, Integer>(){
		{
			put("TLSv1.2", 3);
			put("TLSv1.1", 2);
			put("TLSv1", 1);
			put("SSLv2", 0);
			put("SSLv3", 0);
		}
	};

	/**
	 * Determines levels for key type used in the website's certificate.
	 */
	private static Map<String, Integer> keyType = new HashMap<String, Integer>(){
		{
			put("Sun RSA public key", 2);
			put("Sun EC public key", 1);
		}
	};

	/**
	 * Determines levels for key size used in the website's certificate.
	 */
	private static Map<Integer, Integer> keySize = new HashMap<Integer, Integer>(){
		{
			put(4096, 5);
			put(2048, 4);
			put(1024, 3);
			put(384, 2);
			put(256, 1);
		}
	};

	/**
	 * Determines levels for signature algorithm used in the website's certificate.
	 */
	private static Map<String, Integer> signatureAlgorithm = new HashMap<String, Integer>(){
		{
			put("SHA384withECDSA", 6);
			put("SHA256withECDSA", 5);
			put("SHA384withRSA", 5);
			put("SHA256withRSA", 4);
			put("SHA1withECDSA", 3);
			put("SHA1withRSA", 2);
			put("MD2withRSA", 1);
		}
	};
	
	/**
	 * Calculates the security level of a given site using the site's
	 * known parameters. It takes into account all values seen in the 
	 * ResultLine class. 
	 * In this case, the key type is dependent on the key size, and depending
	 * on the combination of the two, you will get a better or worse rating.
	 * For example, the key type "Sun RSA public key" used in conjunction with
	 * key size 4096 will receive a better rating than if using a key size of
	 * 2048.
	 * 
	 * @param	rl	The result line of the website.
	 */
	public static void calculateSecurityRank(ResultLine rl) {
		int securityLevel = 0;
		securityLevel += https.get(rl.isHttps())*hsts.get(rl.isHSTS());
		if (rl.getSslVersion() != "NONE")
			securityLevel += sslVersion.get(rl.getSslVersion());
		if (rl.getKeySize() != -1 && !rl.getKeyType().equals(""))
			securityLevel += keyType.get(rl.getKeyType()) * keySize.get(rl.getKeySize());
		if (!rl.getSignatureAlgorithm().equals(""))
			securityLevel += signatureAlgorithm.get(rl.getSignatureAlgorithm());
		
		rl.setSecurityLevel(securityLevel);
	}

}
