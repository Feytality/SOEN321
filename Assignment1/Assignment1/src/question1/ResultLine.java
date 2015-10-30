package question1;

/**
 * The purpose of this class is to hold the information relating 
 * to the security of a website.
 * 
 * Input: the domain of the website and the ranking of the website as
 * found in the CSV file of the top websites.
 * 
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class ResultLine {
	// Data Members - Attributes
	private int rank;
	private String domain;
	private boolean isHttps;
	private String sslVersion; // need
	private String keyType;
	private int keySize;
	private String signatureAlgorithm;
	private boolean isHSTS;
	private boolean isHSTSLong; // need
	private int HSTSAge;
	// This is to determine what is the site's security level.
	// The higher the rank, the more secure the site is.
	private int securityLevel; 

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

	/**
	 * 
	 * @return
	 */
	public int getHSTSAge() {
		return HSTSAge;
	}
	
	/**
	 * 
	 * @param hstsAge
	 */
	public void setHSTSAge(int hstsAge) {
		this.HSTSAge = hstsAge;
	}

	/**
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
	 */
	public boolean isHttps() {
		return isHttps;
	}

	/**
	 * 
	 * @param isHttps
	 */
	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	/**
	 * 
	 * @return
	 */
	public String getSslVersion() {
		return sslVersion;
	}

	/**
	 * 
	 * @param sslVersion
	 */
	public void setSslVersion(String sslVersion) {
		this.sslVersion = sslVersion;
	}

	/**
	 * 
	 * @return
	 */
	public String getKeyType() {
		return keyType;
	}

	/**
	 * 
	 * @param keyType
	 */
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	/**
	 * 
	 * @return
	 */
	public int getKeySize() {
		return keySize;
	}

	/**
	 * 
	 * @param keySize
	 */
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	/**
	 * 
	 * @return
	 */
	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	/**
	 * 
	 * @param signatureAlgorithm
	 */
	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isHSTS() {
		return isHSTS;
	}

	/**
	 * 
	 * @param isHSTS
	 */
	public void setHSTS(boolean isHSTS) {
		this.isHSTS = isHSTS;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isHSTSLong() {
		if (HSTSAge >= 15768000) // about 6 months in seconds
			isHSTSLong = true;
		else
			isHSTSLong = false;
		return isHSTSLong;
	}

	/**
	 * 
	 * @param isHSTSLong
	 */
	public void setHSTSLong(boolean isHSTSLong) {
		this.isHSTSLong = isHSTSLong;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * 
	 * @param securityLevel
	 */
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	/**
	 * Overrides the toString method to make a ResultLine look like the expected
	 * result line.
	 */
	@Override
	public String toString() {
		return rank + "," + domain + "," + isHttps + "," + sslVersion + "," + keyType + "," + keySize + ","
				+ signatureAlgorithm + "," + isHSTS + "," + isHSTSLong() + "," + securityLevel;
	}

}
