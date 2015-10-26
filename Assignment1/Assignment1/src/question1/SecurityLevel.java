package question1;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the SecurityLevel class which contains all the object which help determine the
 * rank of a given site using hash maps. The higher the rank, the less secure it is. It is also
 * responsible for taking a ResultLine and tallying up its' respective security level.
 * 
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
@SuppressWarnings("serial")
public class SecurityLevel {
	// Objects which determine the security level depending on the value.
	/**
	 * Determines levels for websites with or without HTTPS.
	 */
	private static Map<String, Integer> https = new HashMap<String, Integer>(){
		{
			put("true", 1);
			put("false", 2);
		}
	};
	
	/**
	 * Determines levels for the SSL version used in the website.
	 */
	private static Map<String, Integer> sslVersion = new HashMap<String, Integer>(){
		{
			put("SSLv3", 1);
		}
	};
	
	/**
	 * Determines levels for key type used in the website's certificate.
	 */
	private static Map<String, Integer> keyType = new HashMap<String, Integer>(){
		{
			put("Sun RSA public key", 1);
			put("Sun EC public key", 2);
		}
	};
	
	/**
	 * Determines levels for key size used in the website's certificate.
	 */
	private static Map<Integer, Integer> keySize = new HashMap<Integer, Integer>(){
		{
			put(4096, 1);
			put(2048, 2);
			put(1024, 3);
			put(384, 4); // Sun EC public key
			put(256, 5); // same
		}
	};
	
	/**
	 * Determines levels for signature algorithm used in the website's certificate.
	 */
	private static Map<String, Integer> signatureAlgorithm = new HashMap<String, Integer>(){
		{
			put("SHA384withRSA", 1);
			put("SHA384withECDSA", 2);
			put("SHA256withRSA", 2);
			put("SHA256withECDSA", 3);
			put("SHA1withRSA", 4);
			put("SHA1withECDSA", 5);
			put("MD2withRSA", 6);
		}
	};
	
	/**
	 * Determines levels for websites with or without HSTS.
	 */
	private static Map<String, Integer> hsts = new HashMap<String, Integer>(){
		{
			put("true", 1);
			put("false", 2);
		}
	};
	 
	public static void calculateSecurityRank(ResultLine rl) {
		int securityLevel = 0;
		securityLevel += https.get(rl.isHttps());
		securityLevel += sslVersion.get(rl.getSslVersion());
		securityLevel += keyType.get(rl.getKeyType());
		securityLevel += keySize.get(rl.getKeySize());
		securityLevel += signatureAlgorithm.get(rl.getSignatureAlgorithm());
		securityLevel += hsts.get(rl.isHSTS());
		
		rl.setSecurityLevel(securityLevel);
	}

}
