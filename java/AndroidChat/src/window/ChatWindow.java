package window;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cloud.Chat;
import cloud.ConnectionManager;

public class ChatWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private JTextArea textArea;
	private JScrollPane scroll;
	private JTextField message;
	private JButton sendButton;
	
	private ConnectionManager manager;
	private ArrayList<String> added;

	public ChatWindow() {
		super("Firebase Chat");
		GridBagConstraints cons = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		panel = new JPanel(layout);
		textArea = new JTextArea();
		
		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(300, 200));
		
		message = new JTextField(30);
		message.setText("");
		sendButton = new JButton("Send");
		
		cons.fill = GridBagConstraints.BOTH;
		cons.gridy = cons.gridx = 0;
		cons.gridwidth = 2;
		panel.add(scroll, cons);
		
		cons.gridwidth = 1;
		cons.gridy = 1;
		panel.add(message, cons);
		
		cons.gridx = 2;
		panel.add(sendButton, cons);
		
		super.add(panel);
		//super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setLocationRelativeTo(null);
		super.pack();
		
		manager = new ConnectionManager(this);
		super.addWindowListener(new ConnectionCleaner(manager));
		sendButton.addActionListener(this);
		
		added = new ArrayList<String>();
	}
	
	public void populateView(ArrayList<Chat> data, ArrayList<String> keys) {
		int index = 0;
		
		for(String key: keys){
			if(!added.contains(key)){
				Chat c = data.get(index);
				String msg = textArea.getText();
				msg += c.getAuthor() + ": " + c.getMessage() + "\n";
				textArea.setText(msg);
				added.add(key);
			}
			index++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton){
			manager.sendMessage(message.getText());
			message.setText("");
		}
	}
}
