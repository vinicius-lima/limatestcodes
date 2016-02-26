package server;

import javax.swing.JOptionPane;

public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String port = JOptionPane.showInputDialog("Type the server port");
		if(port == null)
			System.exit(1);
		
		Server server = new Server(Integer.parseInt(port));
		server.setVisible(true);
	}

}
