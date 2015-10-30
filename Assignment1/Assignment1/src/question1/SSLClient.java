package question1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

/**
 * Purpose of this class is to act as the SSL Client for a website and retrieve
 * the information needed.
 * 
 * @author Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class SSLClient {

	// Socket variables
	SSLSocket socket = null;
	SSLSocketFactory sf = null;

	// Reading and writing
	BufferedReader reader = null;
	PrintWriter writer = null;

	// Certificate Handle
	X509Certificate[] x509certificates;

	// The port used for SSL
	private int PORT = 443;
	
	// Variable which holds the host name of the website 
	// the class is obtaining security information for
	private String host = null;
	
	// Object representing the line to write back to a CSV file.
	private ResultLine resultLine;

	/**
	 * Two parameter constructor which takes the rank and host of
	 * a website to connect to.
	 * 
	 * @param	rank	The website's rank in the CSV file.
	 * @param 	domain	The website's domain.
	 */
	public SSLClient(int rank, String host) {
		this.host = host;
		resultLine = new ResultLine(rank, host);
		sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
	}

	/**
	 * Gets all the site information needed for a ResultLine.
	 */
	public void getSiteInfo() {
		try {
			int attempt = 1;
			boolean connected = connectToSite(host, attempt);

			if(connected) {
				sendHeaderRequest();
	
				// Extract information from response.
				parseHeader();
			} else {
				System.out.println("Could not connect to domain '" + host + 
									"'. This means the website is not using HTTPS.");
				System.out.println();
				
				resultLine.setHttps(false);
				resultLine.setHSTS(false);
				resultLine.setHSTSAge(0);
				resultLine.setHSTSLong(false);
				
				// Find version of SSL
				determineSSLVersion();
	
				// Re-enable RC4 and get SSL Session
				reenableRC4();
				SSLSession session = socket.getSession();
				
				// Determine certificate specific information.
				// gets signature algorithm SHA256 etc
				x509certificates = session.getPeerCertificateChain();
				parseCertificate();
				
				System.out.println("#####################################################");
				System.out.println(resultLine.toString());
				
				System.out.println("#####################################################");
			}
				
			
		} catch (IOException ioe) {
			System.out.println("Could not send request for domain '" + host + "'");
			System.out.println();
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("Could not connect to domain '" + host + "'");
			System.out.println();
		}
	}

	/**
	 * Connects to a site by opening a new SSL socket using the host and the
	 * port. Attempts to reconnect on connection failure related to a refused connection
	 * or a connection time out.
	 * 
	 * @param	host	The host name of the website to connect to.
	 * @param 	attempt	The current number of attempts to connect to the host.
	 * 
	 * @return	True if you are able to connect to the site, else false.
	 */
	public boolean connectToSite(String host, int attempt) {
		// Variable which holds the return value of the method to avoid mutliple
		// return statements.
		boolean retVal = false;
		
		try {
			// First attempt to connect to the host using the SSL port 443.
			socket = (SSLSocket) sf.createSocket(host, PORT);
			retVal = true;
		} catch (UnknownHostException e) {
			// This means that the site gave a 404 error
			e.printStackTrace();
		} catch (ConnectException e) {
			if (attempt < 4) {
				System.out.println("RETRYING CONNECTION TO '" + host + "' (ATTEMPT " + attempt + ")");
				new java.util.Timer().schedule( 
						new java.util.TimerTask() {
							@Override
							public void run() {
								connectToSite(host, attempt + 1);				  
							}
						}, 
						3000 
				);
			} else {
				System.out.println("CONNECTION TO '" + host + "' IMPOSSIBLE");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * Sends the header request to the open socket.
	 * 
	 * @throws IOException
	 */
	public void sendHeaderRequest() throws IOException {
		System.out.println("******* Sending Request *******");
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		writer.println("GET / HTTP/1.1");
		writer.println("Host: " + host);
		writer.println("Accept: */*");
		writer.println("User-Agent: Java");
		writer.println("");
		writer.flush();
		
		System.out.println("******* Response Received *******");
		System.out.println();
	}

	/**
	 * Prints and parses the header into parts of the ResultLine that it can
	 * (determines HTTPS and if is HSTS)
	 * 
	 * @throws IOException
	 */
	public void parseHeader() throws IOException {
		System.out.println("******* Parse Response *******");
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// read the response
		for (String line; (line = reader.readLine()) != null;) {
			if (line.isEmpty())
				break; // stop when headers are completed, ignore html
			if (line.contains("Location") && line.contains("https")) {
				resultLine.setHttps(true);
			}
			if (line.contains("Strict-Transport-Security") || line.contains("strict-transport-security")) {
				//String manipulation to get age of HSTS
				int startOfAge = line.indexOf("=");
				int age = 0;
				if (startOfAge != -1) {
					int endOfAge = line.indexOf(";", startOfAge);
					if (endOfAge != -1) {
					} else {
						endOfAge = line.length();
					}
					age = Integer.parseInt(line.substring(startOfAge + 1, endOfAge));
				}//end manipulation
				
				resultLine.setHSTS(true);
				resultLine.setHSTSAge(age);
			}
			System.out.println(line);
		}
		
		System.out.println("******* End Response *******");
		System.out.println();
		
		// Close reader and writer objects
		if (reader != null)
			reader.close();
		if (writer != null)
			writer.close();
	}

	/**
	 * Prints Security details about a given host and files them into the result line obj
	 */
	public void parseCertificate() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				    new File("./src/question1/certificate-info.txt"),true));
			writer.println("******* Parse Certificate for " + host + " *******");
						
			// don't know how to deal with the multiple lines
			writer.println("Subject DN: " + x509certificates[0].getSubjectDN());
			writer.println("Issuer DN: " + x509certificates[0].getIssuerDN());
			writer.println("Signature Algorithm: " + x509certificates[0].getSigAlgName());
			writer.println("Public key: " + x509certificates[0].getPublicKey());
			
			resultLine.setSignatureAlgorithm(x509certificates[0].getSigAlgName());
			String publicKey = x509certificates[0].getPublicKey().toString();
			
			resultLine.setKeyType(publicKey.substring(0, publicKey.indexOf(',')));
			String keySize = publicKey.substring(publicKey.indexOf(',')+1, publicKey.lastIndexOf("bits")).trim();
			try{
				resultLine.setKeySize(Integer.parseInt(keySize));
			} catch (Exception e) {
				writer.println("Could not parse the key size: " + keySize);
			}
			writer.println("Key Type: " + resultLine.getKeyType());
			writer.println("Key Size: " + resultLine.getKeySize());
			
			SecurityLevel.calculateSecurityRank(resultLine);
			writer.println(resultLine.toString());
			writer.println("******* End Certificate *******");
			writer.println();
			
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Must do this for each SSL socket. Code taken from Tutorial #4 slides.
	 */
	public void reenableRC4() {
		String[] suites = socket.getEnabledCipherSuites();
		ArrayList<String> newSuitesList = new ArrayList<String>(Arrays.asList(suites));
		newSuitesList.add("SSL_RSA_WITH_RC4_128_SHA");
		newSuitesList.add("SSL_RSA_WITH_RC4_128_MD5");
		String[] newSuitesArray = new String[newSuitesList.size()];
		newSuitesArray = newSuitesList.toArray(newSuitesArray);
		socket.setEnabledCipherSuites(newSuitesArray);
	}
	
	/**
	 * Determines the version of SSL used on the given site.
	 */
	private void determineSSLVersion() {
		String[] enabledProtocols = socket.getEnabledProtocols();
		for(String protocol : enabledProtocols) {
			if(protocol.toUpperCase().contains("SSL")) {
				resultLine.setSslVersion(protocol);
			}
		}
	}
}
