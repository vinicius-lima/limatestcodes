package server;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame {
	
	private int port;
	private JPanel panel;
	private JTextArea textArea;
	private Thread thread;
	
	public Server(int port){
		this.port = port;
		
		panel = new JPanel(new GridLayout(1, 1));
		textArea = new JTextArea();
		//textArea.setText("\n\n\n\n");
		panel.add(new JScrollPane(textArea));
		super.add(panel);
		super.setSize(200, 250);
		//super.pack();
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		thread = new Thread(new ServerThread());
		thread.start();
	}
	
	private class ServerThread implements Runnable {
		@Override
		public void run() {
			String clientSentence;
	        String capitalizedSentence;
	        String text;
	        
	        try{
		        ServerSocket welcomeSocket = new ServerSocket(port);
		        while(true)
		        {
		           Socket connectionSocket = welcomeSocket.accept();
		           BufferedReader inFromClient = new BufferedReader(
		        		   new InputStreamReader(connectionSocket.getInputStream()));
		           DataOutputStream outToClient = new DataOutputStream(
		        		   connectionSocket.getOutputStream());
		           clientSentence = inFromClient.readLine();
		           text = textArea.getText();
		           text += "Received: " + clientSentence + "\n";
		           textArea.setText(text);
		           capitalizedSentence = clientSentence.toUpperCase() + '\n';
		           outToClient.writeBytes(capitalizedSentence);
		           connectionSocket.close();
		        }
	        }
	        catch(Exception e){
	        	JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
	        }
		}
	}
}
