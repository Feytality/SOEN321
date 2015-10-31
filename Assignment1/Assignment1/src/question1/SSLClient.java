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
 * the information needed.  It will store the information retrieved from 
 * connecting to the website in an object called ResultLine utility.
 * After filling out all of the information, it will then write this ResultLine
 * to an output file. 
 * 
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class SSLClient {
	// Data Members - Attributes
	
	/**
	 *  Socket variables
	 */
	SSLSocket socket = null;
	SSLSocketFactory sf = null;

	/**
	 * Reading and writing for the headers of the website
	 */
	BufferedReader reader = null;
	PrintWriter writer = null;

	/**
	 * Contains all the information relating to certificates.
	 */
	X509Certificate[] x509certificates;

	/**
	 * The port used for SSL
	 */
	private int PORT = 443;

	/**
	 * Variable which holds the host name of the website
	 * the class is obtaining security information for.
	 */
	private String host = null;

	/**
	 * Object representing the line to write back to a CSV file.
	 */
	private ResultLine resultLine;

	/**
	 * Holds the number of redirects needed to access the website.
	 */
	int redirects=0;
	
	/**
	 * The writer utility to write to the output file.
	 */
	FileUtility fu;
	
	// Constructor

	/**
	 * Two parameter constructor which takes the rank and host of a website to
	 * connect to. It initializes the data members which need to be used at
	 * this point.
	 * 
	 * @param	rank	The website's rank in the CSV file.
	 * @param	domain	The website's domain.
	 */
	public SSLClient(int rank, String host) {
		this.host = host;
		resultLine = new ResultLine(rank, host);
		sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

		System.out.println("host: " + host + " rank:  " + rank);
		
		// Gets the output file to write to and opens it
		fu = new FileUtility();
		fu.open();
	}
	
	// Member Methods

	/**
	 * Gets all the site information needed for a ResultLine and sets it
	 * accordingly. It is also responsible for writing the result line to a
	 * file once all the information has been gathered.
	 * 
	 * Limitation: does not write to the file if there is an IOException,
	 * and will skip the file entirely. This can occur due to an improper
	 * GET request, which has not happened as of yet.
	 */
	public void getSiteInfo() {
		PrintWriter outputWriter = fu.getWriter();
		try {
			int attempt = 1;
			// Attempt to connect to the website
			connectToSite("www." + host, attempt);
			sendHeaderRequest();
			
			// Extract information from response.
			parseHeader();

			// Re-enable RC4 and get the SSL session.
			reenableRC4();
			SSLSession session = socket.getSession();

			// Find version of SSL used by the website.
			determineSSLVersion(session);

			// Determine certificate specific information.
			// gets signature algorithm SHA256 etc
			x509certificates = session.getPeerCertificateChain();
			parseCertificate();

			// Calculate the security level for the site.
			SecurityLevel.calculateSecurityRank(resultLine);

			System.out.println("#####################################################");
			// Write the result line to the output file.
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
			// Handles any IOException.
			System.out.println("Could not send request for domain '" + host + "'");
		} catch (NullPointerException npe) {
			// Handles any null point exception.
			System.out.println("Could not connect to domain due to missing information '" + host + "'");
			outputWriter.println(resultLine.getRank() + "," + resultLine.getDomain() +" No Connection Available");
		}
		// Close the writer since we are done writing to the file now.
		outputWriter.close();
	}

	/**
	 * Connects to a site by opening a new SSL socket using the host and the
	 * port. Attempts to reconnect on connection failure related to a refused
	 * connection or a connection time out.
	 * 
	 * @param	host	The host name of the website to connect to
	 * @param	attempt The current number of attempts to connect to the host
	 * 
	 * @return True if you are able to connect to the site, else false
	 */
	public boolean connectToSite(String host, int attempt) {
		// Variable which holds the return value of the method to avoid multiple
		// return statements.
		boolean retVal = false;
		// Get the object which can write to the output file incase of any
		// exceptions when trying to connect to a site
		PrintWriter outputWriter = fu.getWriter();

		try {
			// First attempt to connect to the host using the SSL port 443.
			socket = (SSLSocket) sf.createSocket(host, PORT);
			retVal = true;
		} catch (UnknownHostException e) {
			// Write to the file that we could not initially connect to the host,
			// and that we are trying again.
			outputWriter.println(resultLine.getRank() + "," + resultLine.getDomain() 
									+ ", COULD NOT CONNECT INITIALLY");
			retVal = false;
		} catch (ConnectException e) {
			// If a connect exception occurs, try to connect again.
			// Will try to connect again for a maximum of 4 times.
			if (attempt < 4) {
				System.out.println("RETRYING CONNECTION TO '" + host + "' (ATTEMPT " + attempt + ")");
				// Create a timer for attempting to connnect to the site again.
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						connectToSite(host, attempt + 1);
					}
				}, 3000);
			} else {
				System.out.println(resultLine.getRank() + "," + resultLine.getDomain() 
										+ "' CONNECTION IMPOSSIBLE DUE TO TIMEOUTS");
			}
		} catch (SSLHandshakeException ssle) {
			// Handles SSLHandshakeException, which means the connection is
			// not usable due to insufficient security levels between the client and sever.
			System.out.println("Handshake exception for '" + host + "' No https");
		} catch (IOException ioe) {
			// Handles any IOException
			System.out.println("Something went with retrieve output file for while trying to connect to '"
								+ host + "'");
		} catch (Exception e) {
			// Handles any other exception appart from the exceptions handled above.
			System.out.println("An unforseen exception occured whiling connecting to '" + host + "'");
		}
		return retVal;
	}

	/**
	 * Sends the header request to the open socket.
	 * 
	 * @throws	IOException	If it could not get the output stream
	 * 						of  the socket.
	 */
	public void sendHeaderRequest() throws IOException {
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

		// Construct the get request
		writer.println("GET / HTTP/1.1");
		writer.println("Host: " + host);
		writer.println("Accept: */*");
		writer.println("User-Agent: Java");
		writer.println("");
		writer.flush(); // Required to complete the GET request.
	}

	/**
	 * Prints and parses the header into parts of the ResultLine that it can
	 * (determines HTTPS and if is HSTS)
	 */
	public void parseHeader()  {
		System.out.println("******* Parse Response *******");
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
			boolean hasMoved = false;
			// Read the response received from GET request.
			for (String line; (line = reader.readLine()) != null;) {
				if (line.isEmpty()) {
					// Stop when headers are completed and ignore HTML.
					break; 
				}
				// If a line contains "Moved", this means the site has been moved.
				// Set hasMoved flag to true.
				if (line.contains("Moved")) {
					hasMoved = true;
				}
				if (line.contains("Location")) {
					// If the hasMoved flag is set the true and there is
					// a "Location", try to connect to the site at that
					// location.
					if (hasMoved && redirects != 4) {
						// Increment the number of redirects so far.
						redirects++;
						
						// Parse the line for the new host to connect to.
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
				// If a line contains Strict-Transport-Security, this means that
				// the website supports HSTS.
				if (line.contains("Strict-Transport-Security") || line.contains("strict-transport-security")) {
					// Manipulate string to get age of HSTS.
					try {
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
		
						// Set the result line for HSTS support, and HSTS age.
						resultLine.setHSTS(true);
						resultLine.setHSTSAge(age);
					} catch (NumberFormatException nfe) {
						System.out.println("Could not compute HSTS age.");
					}
				}
				System.out.println(line);
			} // end for loop

			System.out.println("******* End Response *******");
			System.out.println();
	
			// Close reader and writer objects
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			System.out.println("Could not read header for '"+ host + "'");
		}
	}

	/**
	 * Handles the certificate object related to the SSL Session.
	 * This will load signature algorithm, public key type and public
	 * key size into the ResultLine object.
	 */
	public void parseCertificate() {
		// Get the output file writer.
		PrintWriter outputWriter = fu.getWriter();

		// Set signature algorithm in ResultLine for the website.
		resultLine.setSignatureAlgorithm(x509certificates[0].getSigAlgName());
		
		// Set public key type in ResultLine.
		String publicKey = x509certificates[0].getPublicKey().toString();
		resultLine.setKeyType(publicKey.substring(0, publicKey.indexOf(',')));
		
		// Set public key type in ResultLine.
		String keySize = publicKey.substring(publicKey.indexOf(',') + 1, publicKey.lastIndexOf("bits")).trim();
		try {
			resultLine.setKeySize(Integer.parseInt(keySize));
		} catch (Exception e) {
			outputWriter.println("Could not parse the key size: " + keySize);
		}
	}

	/**
	 * This method is responsible for re-enabling RC4 since it is disabled
	 * by default. Code taken from Tutorial #4 slides.
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
	 * Determines the SSL version for the given site.
	 * The possible protocols can be: TLSv1.2, TLSv1.1,
	 * TLSv1, SSLv2, SSLv3.
	 * 
	 * @param	session	The SSLSession to get the used protocol
	 * 					from to determine SSL version.
	 */
	private void determineSSLVersion(SSLSession session) {
		resultLine.setSslVersion(session.getProtocol());
	}
}
