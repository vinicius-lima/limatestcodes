package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	public static String sendMessage(String serverIP, int port, String sentence){
		String modifiedSentence = null;
		
		try {
			Socket socket = new Socket(serverIP, port);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			outToServer.writeBytes(sentence + "\n");
			modifiedSentence = inFromServer.readLine();
			socket.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return modifiedSentence;
	}
}
