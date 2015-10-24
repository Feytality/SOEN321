package question1;

/**
 * Holds the information for each website in the output file.
 * 
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class ResultLine {
	
	private int rank;
	private String domain;
	private boolean isHttps;
	private String sslVersion; //need
	private String keyType; //need
	private int keySize; //need
	private String signatureAlgorithm; //need
	private boolean isHSTS; //need
	private boolean isHSTSLong; //need
	private int hstsAge;//need
	

	public ResultLine() {	
	}

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
	}
	
	public int getHstsAge() {
		return hstsAge;
	}
	
	public void setHstsAge(int hstsAge) {
		this.hstsAge = hstsAge;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isHttps() {
		return isHttps;
	}

	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	public String getSslVersion() {
		return sslVersion;
	}

	public void setSslVersion(String sslVersion) {
		this.sslVersion = sslVersion;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public boolean isHSTS() {
		return isHSTS;
	}

	public void setHSTS(boolean isHSTS) {
		this.isHSTS = isHSTS;
	}

	public boolean isHSTSLong() {
		return isHSTSLong;
	}

	public void setHSTSLong(boolean isHSTSLong) {
		this.isHSTSLong = isHSTSLong;
	}
	
	
	/**
	 * Override the toString method to make a ResultLine look like the expected
	 * result line.
	 */
	@Override
	public String toString(){
		return rank + "," + domain + "," + isHttps + "," + sslVersion + "," + keyType + "," + 
				keySize + "," + signatureAlgorithm + "," + isHSTS + "," + isHSTSLong;
	}
	/**
	 * Will generate a security rating for the site based on the Https,algorithm, SSL/TLS ,HSTS, Certificate values 
	 * @return
	 */
	public String getSecurityRating(){
		
		return "The meaning of life";
	}
	
}
