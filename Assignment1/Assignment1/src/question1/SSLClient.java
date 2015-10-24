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
 * Purpose of this class is to act as the SSL Client for a website and
 * retrieve the information needed.
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
	public void getSiteInfo(){
		
		try {
			int attempt = 1;
			//socket = (SSLSocket) sf.createSocket(HOST, PORT);
			connectToSite(host, PORT,attempt);
			
			// PUTTING THIS IN A METHOD SCREWS STUFF UP DONT KNOW WHY.
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.println("GET / HTTP/1.1");
			writer.println("Host: " + host);
			writer.println("Accept: */*");
			writer.println("User-Agent: Java");
			writer.println(""); //important, needed to end request
			writer.flush();
			
			parseHeader();
		
			// Re-enable RC4
			reenableRC4();
			
			// Get session after RC4 is re-enabled
			SSLSession session = socket.getSession();
			
			// Session contains a lot of information, look at the doc java8
			
			// get peer certificate stuff
			X509Certificate[] x509certificates = session.getPeerCertificateChain();
			// iterate through the array and just pritn it out or look at getter methods
			// gets signature algorithm SHA256 etc
			
			// look at the session object getter methods
		} catch (Exception e) {
			// handle errors
		}
	}
	
	/**
	 * Connects to a site by opening a new SSL socket using the host and the port
	 * 
	 */
	public void connectToSite(String host, int port,int attempt) {
		// Add a way to retry the connection. Due to connection refused or connection time out.
		try {
			socket = (SSLSocket) sf.createSocket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}catch(ConnectException e){
			if(attempt<4){
			System.out.println("RETRYING CONNECTION TO '"+ host+ "'");
				connectToSite( host, port, attempt+1);
			}else{
				System.out.println("CONNECTION FAILED");
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		}
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
		writer.println(""); // important, needed to end request
		writer.flush();
	}

	/**
	 * Prints and parses the header into parts of the ResultLine that it can
	 * (determines HTTPS and if is HSTS)
	 * 
	 * @throws IOException
	 */
	public void parseHeader() throws IOException {
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// read the response
		for (String line; (line = reader.readLine()) != null;) {
			if (line.isEmpty())
				break; // stop when headers are completed, ignore html
			if (line.contains("Location") && line.contains("https")){
				resultLine.setHttps(true);
			}
			if (line.contains("Strict-Transport-Security")) {
				resultLine.setHSTS(true);
			}
			System.out.println(line);
		}
		
		// close reader/writer after we header is acquired
		if (reader != null) reader.close();
		if (writer != null) writer.close();
	}
	
	/**
	 * Must do this for each SSL socket.
	 * Code taken from Tutorial #4 slides.
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
}
