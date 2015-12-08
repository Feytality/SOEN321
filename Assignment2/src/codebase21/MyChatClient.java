package codebase21;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;

import infrastructure.ChatClient;

/**
 * ChatClient implements the fundamental communication capabilities for your
 * server, but it does not take care of the semantics of the payload it carries.
 * 
 * Here MyChatClient (of your choice) extends it and implements the actual
 * client-side protocol. It must be replaced with/adapted for your designed
 * protocol.
 *
 * Note that A and B are distinguished by the boolean value with the
 * constructor.
 */
class MyChatClient extends ChatClient {

	// This is the minimum constructor you must preserve
	MyChatClient(boolean IsA) { 
		super(IsA); // IsA indicates whether it's client A or B
		startComm(); // starts the communication
	}

	/** The current user that is logged in on this client **/
	public String curUser = "";

	/** The current password for the user that is logged in **/
	public String curPass = "";

	/** The Json array storing the internal history state */
	JsonArray chatlog;

	/**
	 * Actions received from UI
	 */

	/**
	 * Someone clicks on the "Login" button
	 */
	public void LoginRequestReceived(String uid, String pwd) {
		// before asking to log in, user asks to be challenged for knowledge of
		// secret
		ChatPacket p = new ChatPacket();
		p.request = ChatRequest.BEGINCR;
		p.uid = uid;
		curPass = pwd;
		SerializeNSend(p);

	}

	/**
	 * Callback invoked when the certificate file is selected
	 * 
	 * @param path
	 *            Selected certificate file's path
	 */
	public void FileLocationReceivedCert(File path) {
		// TODO
	}

	/**
	 * Callback invoked when the private key file is selected
	 * 
	 * @param path
	 *            Selected private key file's path
	 */
	public void FileLocationReceivedPriv(File path) {
		// TODO
	}

	/**
	 * Callback invoked when an authentication mode is selected.
	 * 
	 * @param IsPWD
	 *            True if password-based (false if certificate-based).
	 */
	public void ReceivedMode(boolean IsPWD) {
		// TODO
	}

	/**
	 * Someone clicks on the "Logout" button
	 */
	public void LogoutRequestReceived() {
		ChatPacket p = new ChatPacket();
		p.request = ChatRequest.LOGOUT;
		SerializeNSend(p);
	}

	/**
	 * Someone clicks on the "Send" button
	 * 
	 * @param message
	 *            Message to be sent (user's level)
	 */
	public void ChatRequestReceived(byte[] message) {
		ChatPacket p = new ChatPacket();
		p.request = ChatRequest.CHAT;
		p.uid = curUser;
		p.data = message;
		SerializeNSend(p);
	}

	/**
	 * Methods for updating UI
	 */

	/**
	 * This will refresh the messages on the UI with the JSON array chatlog
	 */
	void RefreshList() {
		String[] list = new String[chatlog.size()];
		for (int i = 0; i < chatlog.size(); i++) {
			String from = chatlog.getJsonObject(i).getString("from");
			String to = chatlog.getJsonObject(i).getString("to");
			String message = chatlog.getJsonObject(i).getString("message");
			list[i] = (from + "->" + to + ": " + message);
		}
		UpdateMessages(list);
	}

	/**
	 * Methods invoked by the network stack
	 */

	/**
	 * Callback invoked when a packet has been received from the server (as the
	 * client only talks with the server, but not the other client)
	 * 
	 * @param	buf	Incoming message
	 */
	public void PacketfromServer(byte[] buf) {
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(is);
			Object o = in.readObject();
			ChatPacket p = (ChatPacket) o;

			if (p.request == ChatRequest.RESPONSE && p.success.equals("LOGIN")) {
				// This indicates a successful login
				curUser = p.uid;

				// Time to load the chatlog
				InputStream ins = null;
				JsonReader jsonReader;
				File f = new File(this.getChatLogPath());
				if (f.exists() && !f.isDirectory()) {
					try {
						String chatlogstr = decryptChatLog();// decrypt based on
																// MAC
						ins = new ByteArrayInputStream(chatlogstr.getBytes(StandardCharsets.UTF_8));
						jsonReader = Json.createReader(ins);
						chatlog = jsonReader.readArray();
					} catch (FileNotFoundException e) {
						System.err.println("Chatlog file could not be opened.");
					}
				} else {
					try {
						f.createNewFile();
						ins = new FileInputStream(this.getChatLogPath());
						chatlog = Json.createArrayBuilder().build();
					} catch (IOException e) {
						System.err.println("Chatlog file could not be created or opened.");
					}
				}
				RefreshList();

			} else if (p.request == ChatRequest.CHALLENGE) {// NEW
				// User responds to servers challenge as means to login
				p.password = AsgUtils.simpleHash(curPass, p.nonce + 1);
				p.request = ChatRequest.LOGIN;
				SerializeNSend(p);
			}

			else if (p.request == ChatRequest.RESPONSE && p.success.equals("LOGOUT")) {
				// Logged out, save chat log and clear messages on the UI
				SaveChatHistory();
				curUser = "";
				UpdateMessages(null);
			} else if (p.request == ChatRequest.CHAT && !curUser.equals("")) {
				// A new chat message received
				Add1Message(p.uid, curUser, p.data);
			} else if (p.request == ChatRequest.CHAT_ACK && !curUser.equals("")) {
				// This was sent by us and now it's confirmed by the server, add
				// it to chat history
				Add1Message(curUser, p.uid, p.data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gives the path of the local chat history file (user-based)
	 */
	private String getChatLogPath() {
		return "log/Q2.1chatlog-" + curUser + ".json";
	}

	/**
	 * Methods dealing with local processing
	 */

	/**
	 * This method saves the JSON array storing the chat log back to file
	 */
	public void SaveChatHistory() {// modified to read file into byte array for
									// security feature
		if (curUser.equals("")) {
			return;
		}
		
		try {
			// The chatlog file is named after both the client and the user
			// logged in
			FileOutputStream fos = new FileOutputStream(new File(this.getChatLogPath()));
			String log = encryptChatLog();
			byte[] bytes = log.getBytes();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
			baos.write(bytes, 0, bytes.length);
			baos.writeTo(fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Similar to the one in MyChatServer, serializes and send the Java object
	 * 
	 * @param p
	 *            ChatPacket to serialize and send
	 */
	private void SerializeNSend(ChatPacket p) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput out = null;
		
		try {
			out = new ObjectOutputStream(os);
			out.writeObject(p);
			byte[] packet = os.toByteArray();
			SendtoServer(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a message to the internal's client state
	 * 
	 * @param from
	 *            From whom the message comes from
	 * @param to
	 *            To whom the messaged is addressed
	 * @param buf
	 *            Message
	 */
	private void Add1Message(String from, String to, byte[] buf) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (int i = 0; i < chatlog.size(); i++) {
			builder.add(chatlog.getJsonObject(i));
		}
		try {
			builder.add(Json.createObjectBuilder().add("from", from).add("to", to).add("time", "").add("message",
					new String(buf, "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JsonArray newl = builder.build();
		chatlog = newl;
		RefreshList();

	}

	/**
	 * Encrypt the chatlog by dumping the json into a string and running Aes on
	 * it based on mac address
	 * 
	 * @return
	 */
	public String encryptChatLog() {// NEW
		String chatlogString = "";
		for (int i = 0; i < chatlog.size(); i++) {
			if (i != 0) {
				chatlogString += ",";
			}
			chatlogString += chatlog.getJsonObject(i).toString();
		}
		String macAddress = AsgUtils.getMac();
		macAddress = AsgUtils.shortenMac(macAddress);
		chatlogString = AsgUtils.encrpyt(chatlogString, macAddress);
		return chatlogString;
	}

	/**
	 * Decrypts the chatlog, leaving it in a string representing JSON, AES
	 * decryption based on MAC address.
	 * 
	 * @return	decoded	The decrypted chatlog.
	 * 
	 * @throws IOException
	 */
	public String decryptChatLog() throws IOException {
		// Read the file
		InputStream ins = null;
		ins = new FileInputStream(this.getChatLogPath());
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = ins.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		
		// Decrypt the file.
		String decoded = new String(buffer.toByteArray(), "UTF-8");
		String macAddress = AsgUtils.getMac();
		macAddress = AsgUtils.shortenMac(macAddress);
		decoded = AsgUtils.decrypt(decoded, macAddress);
		decoded = "[" + decoded + "]";
		ins.close();
		return decoded;
	}

}
