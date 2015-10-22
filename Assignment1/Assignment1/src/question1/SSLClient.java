package question1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;
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
	public void getSiteInfo(){
		
		ConnectToSite("facebook.com", 443);
		try {
			sendHeaderRequest();//ask for header
			printHeader();//read and print header
			if (reader != null) reader.close();
			if (writer != null) writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public void ConnectToSite(String host, int port) {
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
