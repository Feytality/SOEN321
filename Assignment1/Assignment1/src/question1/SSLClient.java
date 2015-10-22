package question1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.net.ssl.*;

public class SSLClient {
	
	SSLSocket socket;
	SSLSocketFactory sf;
	String host;
	BufferedReader reader = null;
	PrintWriter writer = null;

	public SSLClient(Map<Integer, String> csvDao) {

		sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
	}
	
	//this is how we would order the calls after creating the object elsewhere
	/**
	 * Gets all the site information needed for the output file.
	 */
	public void getSiteInfo(){
		
		connectToSite("google.com", 443);
		try {
			//ask for header
			sendHeaderRequest();
			printHeader();  //read and print header
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectToSite(String host, int port) {
		try {
			socket = (SSLSocket) sf.createSocket(host, port);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printHeader() throws IOException {
		
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// read the response
		for (String line; (line = reader.readLine()) != null;) {
			if (line.isEmpty())
				break; // stop when headers are completed, ignore html
			System.out.println(line);
		}
		
		// close reader/writer after we header is acquired
		if (reader != null) reader.close();
		if (writer != null) writer.close();

	}

	public void sendHeaderRequest() throws IOException {
		PrintWriter writer = null;
		writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

		writer.println("GET / HTTP/1.1");
		writer.println("Host: " + host);
		writer.println("Accept: */*");
		writer.println("User-Agent: Java");
		writer.println(""); // important, needed to end request
		writer.flush();
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
