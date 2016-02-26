package manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import window.EditionPanel;

public class StateManager implements Runnable {
	
	public static final int DEFAULT_PORT = 5577;
	public static final String DEFAULT_IP = "localhost";
	
	private static StateManager stateManager;
	private EditionPanel editionPanel;

	private int localPort;
	private String remoteIP;
	private int remotePort;
	private ServerSocket sock;
	
	private StateManager(int localPort, String remoteIP, int remotePort) {
		this.localPort = localPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public static void startStateManager(int localPort, String remoteIP, int remotePort) {
		if(stateManager == null)
			stateManager = new StateManager(localPort, remoteIP, remotePort);
	}
	
	public static StateManager getStateManager() {
		//startStateManager(DEFAULT_PORT, DEFAULT_IP, DEFAULT_PORT);
		// TODO: Analisar quando for iniciado no modo offline
		return stateManager;
	}
	
	public void setEdititonPanel(EditionPanel panel) {
		editionPanel = panel;
	}

	@Override
	public void run() {
		try {
			sock = new ServerSocket(localPort);
			//System.out.println("Server socket opened!!!");
			while(true){
				receive();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void receive() {
		try {
			Socket source = sock.accept();
			DataInputStream in = new DataInputStream(source.getInputStream());
			String text = in.readUTF();
			if(text != null)
				editionPanel.setText(text);
			source.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean transferState() {
		try {
			Socket transfer = new Socket(remoteIP, remotePort);
			DataOutputStream out = new DataOutputStream(transfer.getOutputStream());
			out.writeUTF(editionPanel.getText());
			transfer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
