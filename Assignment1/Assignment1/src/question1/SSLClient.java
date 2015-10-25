package question1;

import java.io.BufferedReader;
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

	private int PORT = 443; // this is the port for SSL.
	private String host = null;
	private ResultLine resultLine; // line to be writen back to the csv file

	public SSLClient(int rank, String host) {
		this.host = host;
		resultLine = new ResultLine(rank, host);
		sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
	}

	/**
	 * Gets all the site information needed for the output file.
	 */
	public void getSiteInfo() {
		try {
			int attempt = 1;
			boolean connected = connectToSite(host, PORT, attempt);

			if(connected) {
				sendHeaderRequest();
	
				// Extract information from response.
				parseHeader();
				
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
			} else {
				System.out.println("Could not connect to domain: " + host);
				System.out.println();
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
	 * port
	 * 
	 */
	public boolean connectToSite(String host, int port, int attempt) {
		// Add a way to retry the connection. Due to connection refused or
		// connection time out.
		boolean retVal = false;
		try {
			socket = (SSLSocket) sf.createSocket(host, port);
			retVal = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			if (attempt < 4) {
				System.out.println("RETRYING CONNECTION TO '" + host + "' (ATTEMPT " + attempt + ")");
				new java.util.Timer().schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				            	connectToSite(host, port, attempt + 1);				  
				            }
				        }, 
				        3000 
				);
			} else {
				System.out.println("CONNECTION TO '"+host+"' IMPOSSIBLE");
			}
		} catch (IOException e) {
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
				int age=0;
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
		// close reader/writer after we header is acquired
		if (reader != null)
			reader.close();
		if (writer != null)
			writer.close();
	}

	/**
	 * Prints Security details about a given host and files them into the result line obj
	 */
	public void parseCertificate() {
		System.out.println("******* Parse Certificate *******");
		// don't know how to deal with the multiple lines
		for (int i = 0; i < x509certificates.length; i++) {
			System.out.println("Subject DN: " + x509certificates[i].getSubjectDN());
			System.out.println("Issuer DN: " + x509certificates[i].getIssuerDN());
			System.out.println("Signature Algorithm: " + x509certificates[i].getSigAlgName());
			System.out.println("Public key: " + x509certificates[i].getPublicKey());
			
			resultLine.setSignatureAlgorithm(x509certificates[i].getSigAlgName());
			String publicKey = x509certificates[i].getPublicKey().toString();
			
			resultLine.setKeyType(publicKey.substring(0, publicKey.indexOf(',')));
			String keySize = publicKey.substring(publicKey.indexOf(',')+1, publicKey.lastIndexOf("bits")).trim();
			try{
				resultLine.setKeySize(Integer.parseInt(keySize));
			} catch (Exception e) {
				System.out.println("Could not parse the key size: " + keySize);
			}
			System.out.println("Key type: " + resultLine.getKeyType());
			System.out.println("Key size: " + resultLine.getKeySize());
		}
		
		System.out.println("******* End Certificate *******");
		System.out.println();

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
