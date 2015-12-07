package codebase31;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import infrastructure.ChatServer;

/**
 * ChatServer implements the fundamental communication capabilities for your
 * server, but it does not take care of the semantics of the payload it carries.
 * 
 * Here MyChatServer (of your choice) extends it and implements the actual
 * server-side protocol. It must be replaced with/adapted for your designed
 * protocol.
 *
 */
class MyChatServer extends ChatServer {

	/** A Json array loaded from disk file storing plaintext uids and pwds. */
	JsonArray database;

	/**
	 * Client login status; "" indicates not logged in or otherwise is set to
	 * uid.
	 **/
	String statA = "";
	String statB = "";
	
	private UUID aliceAuth;
	private UUID bobAuth;
	
	private PublicKey alicePubKey;
	private PublicKey bobPubKey;
	
	private final String SERVER_PRIV_KEY_PATH = "resources/Server/server.pkcs8";

	// In Constructor, the user database is loaded.
	MyChatServer() {
		try {
			InputStream in = new FileInputStream("database.json");
			JsonReader jsonReader = Json.createReader(in);
			database = jsonReader.readArray();

		} catch (FileNotFoundException e) {
			System.err.println("Database file not found!");
			System.exit(-1);
		}
	}

	/**
	 * Methods invoked by the network stack
	 */

	/**
	 * Overrides the function in ChatServer Whenever a packet is received this
	 * method is called and IsA indicates whether it is from A (or B) with the
	 * byte array of the raw packet
	 */
	public void PacketReceived(boolean IsA, byte[] buf) {
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(is);
			Object o = in.readObject();
			ChatPacket p = (ChatPacket) o;

			if (p.request == ChatRequest.LOGIN) {
				// Check if we got the message back correctly.
				String dec = AsgUtils.decrypt(p.data, AsgUtils.getPrivateKey(SERVER_PRIV_KEY_PATH));
				boolean isValid = false;
				if(IsA) {
					if (dec.equals(aliceAuth.toString())) {
						isValid = true;
					}
				} else {
					if (dec.equals(bobAuth.toString())) {
						isValid = true;
					}
				}
				
				if(isValid) {
//					if (p.uid.equals(IsA ? statB : statA))
//						continue;

					// Update the corresponding login status
					if (IsA) {
						statA = p.uid;
					} else {
						statB = p.uid;
					}

					// Update the UI to indicate this
					UpdateLogin(IsA, p.uid);

					// Inform the client that it was successful
					RespondtoClient(IsA, "LOGIN");
				}

			

				if ((IsA ? statA : statB).equals("")) {
					// Oops, this means a failure, we tell the client so
					RespondtoClient(IsA, "");
				}
			} else if (p.request == ChatRequest.LOGOUT) {
				if (IsA) {
					statA = "";
				} else {
					statB = "";
				}
				UpdateLogin(IsA, "");
				RespondtoClient(IsA, "LOGOUT");

			} else if (p.request == ChatRequest.CHAT) {
				// This is a chat message

				// Whoever is sending it must be already logged in
				if ((IsA && statA != "") || (!IsA && statB != "")) {
					// Decrypt using server's private key
					String dec = AsgUtils.decrypt(p.data, AsgUtils.getPrivateKey(SERVER_PRIV_KEY_PATH));
					// Re-encrypt using recipient's public key.
					PublicKey recipient;
					if (IsA) {
						recipient = bobPubKey;
					} else {
						recipient = alicePubKey;
					}
					buf=AsgUtils.encrypt(dec, recipient);
					
					ChatPacket p2 = new ChatPacket();
					p2.request = ChatRequest.CHAT;
					p2.uid = "Server";
					p2.data=buf;
					// Forward the original packet to the recipient
//					SendtoClient(!IsA, buf);
					SerializeNSend(!IsA, p2);
					p.request = ChatRequest.CHAT_ACK;
					p.uid = (IsA ? statB : statA);

					// Flip the uid and send it back to the sender for updating
					// chat history
					if (IsA) {
						recipient = alicePubKey;
					} else {
						recipient = bobPubKey;
					}
					p.data= AsgUtils.encrypt(dec, recipient);
					SerializeNSend(IsA, p);
				}
			} else if (p.request == ChatRequest.BEGINCR) {
				String username = AsgUtils.parseCertficateToString(p.data);
				// check to see if user exists
				// We want to go through all records
				for (int i = 0; i < database.size(); i++) {
					JsonObject l = database.getJsonObject(i);

					if (l.getString("uid").equals(username)) {
						PublicKey userPubKey = AsgUtils.getPublicKey(getPublicKeyPath(username));
						
						byte[] enc;
						
						if(IsA) {
							alicePubKey = userPubKey;
							aliceAuth = UUID.randomUUID();
							enc = AsgUtils.encrypt(aliceAuth.toString(), userPubKey);

						} else {
							bobPubKey = userPubKey;
							bobAuth = UUID.randomUUID();
							enc = AsgUtils.encrypt(bobAuth.toString(), userPubKey);
						}
						p.uid=username;
						p.data = enc;
						p.request = ChatRequest.CHALLENGE;
						SerializeNSend(IsA, p);
					}
				}

				if ((IsA ? statA : statB).equals("")) {
					// Oops, this means a failure, we tell the client so
					RespondtoClient(IsA, "");
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methods for updating UI
	 */

	// You can use this.UpdateServerLog("anything") to update the TextField on
	// the server portion of the UI
	// when needed

	/**
	 * Methods invoked locally
	 */

	/**
	 * This method serializes (into byte[] representation) a Java object
	 * (ChatPacket) and sends it to the corresponding recipient (A or B)
	 */
	private void SerializeNSend(boolean IsA, ChatPacket p) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(os);
			out.writeObject(p);
			byte[] packet = os.toByteArray();
			SendtoClient(IsA, packet);
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
	 * This method composes the packet needed to respond to a client (indicated
	 * by IsA) regarding whether the login/logout request was successful
	 * p.success would be "" if failed or "LOGIN"/"LOGOUT" respectively if
	 * successful
	 */
	void RespondtoClient(boolean IsA, String Success) {
		ChatPacket p = new ChatPacket();
		p.request = ChatRequest.RESPONSE;
		p.uid = IsA ? statA : statB;
		p.success = Success;

		SerializeNSend(IsA, p);
	}

	/**
	 * Gives the path of the local pub key file (user-based)
	 */
	private String getPublicKeyPath(String username) {
		return "resources/" + username + "/" + username.toLowerCase() + ".der";
	}
	
}
