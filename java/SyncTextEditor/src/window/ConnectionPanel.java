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
	
	private JLabel[] labels;
	private JTextField[] fields;
	private JButton okButton;
	private JButton offlineButton;

	public ConnectionPanel(final Screen screen) {
		super(new GridLayout(4, 4));
		
		labels = new JLabel[3];
		labels[0] = new JLabel("Local port");
		labels[1] = new JLabel("Remote IP");
		labels[2] = new JLabel("Remote port");
		
		fields = new JTextField[3];
		fields[0] = new JTextField();
		fields[0].setText("5577");
		fields[1] = new JTextField();
		fields[1].setText("10.16.1.1");
		//fields[1].setText("localhost");
		fields[2] = new JTextField();
		fields[2].setText("7755");
		
		okButton = new JButton("Connect");
		offlineButton = new JButton("Start offline");
		
		for(int i = 0; i < 3; i++){
			super.add(labels[i]);
			super.add(fields[i]);
		}
		super.add(okButton);
		super.add(offlineButton);
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int lPort = Integer.parseInt(fields[0].getText());
				String rIP = fields[1].getText();
				int rPort = Integer.parseInt(fields[2].getText());
				StateManager.startStateManager(lPort, rIP, rPort);
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
