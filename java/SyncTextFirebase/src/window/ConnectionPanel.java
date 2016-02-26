package window;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import manager.StateManager;

public class ConnectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private JTextField field;
	private JButton okButton;
	private JButton offlineButton;
	
	private StateManager manager;

	public ConnectionPanel(final Screen screen, StateManager stateManager) {
		super(new GridLayout(2, 2));
		
		label = new JLabel("User name: ");
		
		field = new JTextField();
		field.setText("SyncTextUser");
		
		okButton = new JButton("Connect to Firebase");
		offlineButton = new JButton("Start offline");
		
		super.add(label);
		super.add(field);
		super.add(okButton);
		super.add(offlineButton);
		
		manager = stateManager;
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String user_name = field.getText();
				manager.startStateManager(user_name);
				screen.changePanel("Edit");
			}
		});
		
		offlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				screen.changePanel("Edit");
			}
		});
	}
	
}
