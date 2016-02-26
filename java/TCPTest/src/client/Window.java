package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Window extends JFrame implements ActionListener{
	private JPanel panel;
	private JTextField serverIP;
	private JTextField port;
	private JTextField sentence;
	private JButton sendButton;
	private JTextArea response;
	
	public Window(){
		serverIP = new JTextField(20);
		port = new JTextField(20);
		sentence = new JTextField(20);
		sendButton = new JButton("SEND");
		sendButton.addActionListener(this);
		response = new JTextArea();
		
		panel = new JPanel(new GridLayout(5, 1));
		panel.add(serverIP);
		panel.add(port);
		panel.add(sentence);
		panel.add(sendButton);
		panel.add(response);
		
		super.add(panel);
		super.setLocationRelativeTo(null);
		super.pack();
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton){
			String modifiedSentence = 
				Client.sendMessage(serverIP.getText(), Integer.parseInt(port.getText()),
						sentence.getText());
			response.setText("Server IP:" + serverIP.getText()
					+ "\nFROM SERVER: " + modifiedSentence + "\n");
		}
	}

}
