package question1;

/**
 * The purpose of this class is to hold the information relating 
 * to the security of a website.
 * 
 * Input: the domain of the website and the ranking of the website as
 * found in the CSV file of the top websites.
 * 
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class ResultLine {
	// Data Members - Attributes
	// Security attributes relating to a website
	
	private int rank;
	private String domain;
	private boolean isHttps;
	private String sslVersion;
	private String keyType;
	private int keySize;
	private String signatureAlgorithm;
	private boolean isHSTS;
	private boolean isHSTSLong;
	private int HSTSAge;
	/**
	 * This is to determine what is the site's security level.
	 * The higher the rank, the more secure the site is.
	 */
	private int securityLevel; 
	
	// Constructor

	/**
	 * Two parameter constructor which takes a websites rank and domain.
	 * 
	 * @param	rank	The website's rank in the CSV file.
	 * @param 	domain	The website's domain.
	 */
	public ResultLine(int rank, String domain) {
		super();
		this.rank = rank;
		this.domain = domain;
		this.isHttps = false;
		this.sslVersion = "";
		this.keyType = "";
		this.keySize = -1;
		this.signatureAlgorithm = "";
		this.isHSTS = false;
		this.isHSTSLong = false;
		this.HSTSAge = -1;
		this.securityLevel = -1;
	}

	// Getters and Setters
	/**
	 * Gets the website's HSTS age.
	 * 
	 * @return	The website's HSTS age.
	 */
	public int getHSTSAge() {
		return HSTSAge;
	}
	
	/**
	 * Sets the website's HSTS age.
	 * 
	 * @param	hstsAge	The website's HSTS age.
	 */
	public void setHSTSAge(int hstsAge) {
		this.HSTSAge = hstsAge;
	}

	/**
	 * Gets the website's rank as seen in the
	 * CSV file.
	 * 
	 * @return	The website's rank.
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * 
	 * @param rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Gets the website's domain name as seen in the
	 * CSV file.
	 * 
	 * @return	The website's rank.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * 
	 * @param domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Gets whether the website supports HTTPS or not.
	 * 
	 * @return	True if the website supports HTTPS, else
	 * 			false.
	 */
	public boolean isHttps() {
		return isHttps;
	}

	/**
	 * Sets whether the website supports HTTPS or not.
	 * 
	 * @param	isHttps	True if the website supports HTTPS,
	 * 					else false.
	 */
	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	/**
	 * Gets the website's SSL version used.
	 * 
	 * @return	The SSL protocol enabled for the website.
	 */
	public String getSslVersion() {
		return sslVersion;
	}

	/**
	 *  Sets the website's SSL version used.
	 *  
	 * @param	sslVersion	The website's SSL version used.
	 */
	public void setSslVersion(String sslVersion) {
		this.sslVersion = sslVersion;
	}

	/**
	 * Gets the key type used on the website.
	 * 
	 * @return	The key type used on the website
	 */
	public String getKeyType() {
		return keyType;
	}

	/**
	 * Sets the key type used on the website.
	 * 
	 * @param	keyType	The key type used on the website
	 */
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	/**
	 * Gets the key size used on the website.
	 * 	
	 * @return	The key sized used on the website.
	 */
	public int getKeySize() {
		return keySize;
	}

	/**
	 * Sets the key size used on the website.
	 * 
	 * @param 	keySize	The key sized used on the website.
	 */
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	/**
	 * Gets the signature algorithm used on the website.
	 * 
	 * @return	the signature algorithm used on the website.
	 */
	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	/**
	 * Sets the signature algorithm used on the website.
	 * 
	 * @param 	signatureAlgorithm	The signature algorithm used 
	 * 								on the website.
	 */
	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	/**
	 * Gets whether the website supports HSTS or not.
	 * 
	 * @return	True if the website supports HSTS, else false.
	 */
	public boolean isHSTS() {
		return isHSTS;
	}

	/**
	 * Sets whether the website supports HSTS or not.
	 * 
	 * @param	isHSTS	True if the website supports HSTS, else false.
	 */
	public void setHSTS(boolean isHSTS) {
		this.isHSTS = isHSTS;
	}

	/**
	 * Determines if the HSTS age is long or not by checking to see
	 * if the age is greater than 6 months. If it is greater than 6 months,
	 * it is long.
	 * 
	 * @return	True if HSTS age is greater than 6 months, else false.
	 */
	public boolean isHSTSLong() {
		// Check if the age is greater than 6 months (about 15768000 seconds)
		if (HSTSAge >= 15768000) 
			isHSTSLong = true;
		else
			isHSTSLong = false;
		return isHSTSLong;
	}

	/**
	 * Sets whether HSTS on the website is long or not.
	 * 
	 * @param	isHSTSLong	True if HSTS is long, else false.
	 */
	public void setHSTSLong(boolean isHSTSLong) {
		this.isHSTSLong = isHSTSLong;
	}
	
	/**
	 * Gets the security level of the website. The higher the level,
	 * the more secure the website is.
	 * 
	 * @return	The security level of the website.
	 */
	public int getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * Gets the security level of the website. Ranges between levels 1-25.
	 * 
	 * @param	securityLevel	The security level of the site
	 */
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	// Member method
	/**
	 * Overrides the toString method to make a ResultLine look like the expected
	 * result line.
	 */
	@Override
	public String toString() {
		if (keyType == "") {
			keyType = "NONE";
		}
		if (signatureAlgorithm == "") {
			signatureAlgorithm = "NONE";
		}
		
		return rank + "," + domain + "," + isHttps + "," + sslVersion + "," + keyType + "," + keySize + ","
				+ signatureAlgorithm + "," + isHSTS + "," + isHSTSLong() + ",Score:" + securityLevel;
	}

}
