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

	// The port used for SSL
	private int PORT = 443;

	// Variable which holds the host name of the website
	// the class is obtaining security information for
	private String host = null;

	// Object representing the line to write back to a CSV file.
	private ResultLine resultLine;

	int redirects=0;
	FileUtility fu;

	/**
	 * Two parameter constructor which takes the rank and host of a website to
	 * connect to.
	 * 
	 * @param rank
	 *            The website's rank in the CSV file.
	 * @param domain
	 *            The website's domain.
	 */
	public SSLClient(int rank, String host) {
		this.host = host;
		resultLine = new ResultLine(rank, host);
		sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

		// Get the output file to write to.
		fu = new FileUtility();
		fu.open();
	}

	/**
	 * Gets all the site information needed for a ResultLine.
	 */
	public void getSiteInfo() {
		PrintWriter outputWriter = fu.getWriter();
		try {
			int attempt = 1;
			connectToSite("www." + host, attempt);
			sendHeaderRequest();
			// Extract information from response.
			parseHeader();

			// Re-enable RC4 and get SSL Session
			reenableRC4();
			SSLSession session = socket.getSession();

			// Find version of SSL
			determineSSLVersion(session);

			// Determine certificate specific information.
			// gets signature algorithm SHA256 etc
			x509certificates = session.getPeerCertificateChain();
			parseCertificate();

			// Calculate the security level for the site.
			SecurityLevel.calculateSecurityRank(resultLine);

			System.out.println("#####################################################");
			outputWriter.println(resultLine.toString());
			System.out.println(resultLine.toString());
			System.out.println("#####################################################");
		} catch (SSLHandshakeException ssle) {
			System.out.println("Handshake exception for '" + host + "' No https");
		} catch (SSLPeerUnverifiedException sspue) {
			// This is a case where the site is unable to identify itself, for example
			// it might not support authentication. This gives the site the lowest possible
			// security rank.
			SecurityLevel.calculateSecurityRank(resultLine);
			System.out.println("#####################################################");
			System.out.println("SSL Peer Unverified Exception, host '" + host + "' has the lowest security score.");
			outputWriter.println(resultLine.toString());
			System.out.println(resultLine.toString());
			System.out.println("#####################################################");
		} catch (IOException ioe) {
			System.out.println("Could not send request for domain '" + host + "'");
			System.out.println();
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			System.out.println("Could not connect to domain '" + host + "'");
			System.out.println();
			outputWriter.println(resultLine.getRank() + "," + resultLine.getDomain() +" No Connection Available");
		}
		outputWriter.close();
	}

	/**
	 * Connects to a site by opening a new SSL socket using the host and the
	 * port. Attempts to reconnect on connection failure related to a refused
	 * connection or a connection time out.
	 * 
	 * @param host
	 *            The host name of the website to connect to.
	 * @param attempt
	 *            The current number of attempts to connect to the host.
	 * 
	 * @return True if you are able to connect to the site, else false.
	 */
	public boolean connectToSite(String host, int attempt) {
		// Variable which holds the return value of the method to avoid mutliple
		// return statements.
		boolean retVal = false;
		PrintWriter outputWriter = fu.getWriter();

		try {
			// First attempt to connect to the host using the SSL port 443.
			socket = (SSLSocket) sf.createSocket(host, PORT);
			retVal = true;
		} catch (UnknownHostException e) {
			// This means that the site gave a 404 error
			outputWriter.println(resultLine.getRank() + "," + resultLine.getDomain() + ", COULD NOT CONNECT INITIALLY");
		} catch (ConnectException e) {
			if (attempt < 4) {
				System.out.println("RETRYING CONNECTION TO '" + host + "' (ATTEMPT " + attempt + ")");
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						connectToSite(host, attempt + 1);
					}
				}, 3000);
			} else {
				System.out.println(resultLine.getRank() + "," + resultLine.getDomain() + "' CONNECTION IMPOSSIBLE DUE TO TIMEOUTS");}
		} catch (SSLHandshakeException ssle) {
			System.out.println("Handshake exception for '" + host + "' No https");
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
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

		writer.println("GET / HTTP/1.1");
		writer.println("Host: " + host);
		writer.println("Accept: */*");
		writer.println("User-Agent: Java");
		writer.println("");
		writer.flush();
	}

	/**
	 * Prints and parses the header into parts of the ResultLine that it can
	 * (determines HTTPS and if is HSTS)
	 * 
	 * @throws IOException
	 */
	public void parseHeader()  {
		System.out.println("******* Parse Response *******");
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		boolean hasMoved = false;
		// read the response
		for (String line; (line = reader.readLine()) != null;) {
			if (line.isEmpty())
				break; // stop when headers are completed, ignore html
			if (line.contains("Moved")) {
				hasMoved = true;
			}
			if (line.contains("Location")) {
				if (hasMoved&& redirects!=4) {
					redirects++;
					int startOfHost = line.indexOf("//");
					int endOfHost = line.length();
					System.out.println(line);
					System.out.println("www." + host + " has moved  ");
					host = line.substring(startOfHost + 2, endOfHost);
					System.out.println(" trying " + host + " instead");
					getSiteInfo();
					break;
				}
				if (line.contains("https")) {
					resultLine.setHttps(true);
				}

			}
			if (line.contains("Strict-Transport-Security") || line.contains("strict-transport-security")) {
				// String manipulation to get age of HSTS
				int startOfAge = line.indexOf("=");
				int age = 0;
				if (startOfAge != -1) {
					int endOfAge = line.indexOf(";", startOfAge);
					if (endOfAge != -1) {
					} else {
						endOfAge = line.length();
					}
					age = Integer.parseInt(line.substring(startOfAge + 1, endOfAge));
				} // end manipulation

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
		} catch (IOException e) {
			System.out.println("Could not read header for "+ host);
		}
	}

	/**
	 * Prints Security details about a given host and files them into the result
	 * line obj
	 */
	public void parseCertificate() {
		PrintWriter outputWriter = fu.getWriter();

//		outputWriter.println("******* Parse Certificate for " + host + " *******");

		// don't know how to deal with the multiple lines
//		outputWriter.println("Subject DN: " + x509certificates[0].getSubjectDN());
//		outputWriter.println("Issuer DN: " + x509certificates[0].getIssuerDN());
//		outputWriter.println("Signature Algorithm: " + x509certificates[0].getSigAlgName());
//		outputWriter.println("Public key: " + x509certificates[0].getPublicKey());

		resultLine.setSignatureAlgorithm(x509certificates[0].getSigAlgName());
		String publicKey = x509certificates[0].getPublicKey().toString();

		resultLine.setKeyType(publicKey.substring(0, publicKey.indexOf(',')));
		String keySize = publicKey.substring(publicKey.indexOf(',') + 1, publicKey.lastIndexOf("bits")).trim();
		try {
			resultLine.setKeySize(Integer.parseInt(keySize));
		} catch (Exception e) {
			outputWriter.println("Could not parse the key size: " + keySize);
		}
//		outputWriter.println("Key Type: " + resultLine.getKeyType());
//		outputWriter.println("Key Size: " + resultLine.getKeySize());

//		outputWriter.println("******* End Certificate *******");
//		outputWriter.println();
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
	private void determineSSLVersion(SSLSession session) {
		resultLine.setSslVersion(session.getProtocol());
	}
}
