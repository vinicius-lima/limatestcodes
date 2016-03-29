package draw;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import sync.StateManager;

public class Window extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private DrawString stringPanel;
	private StateManager manager;
	private MoveString moveIt;
	private JMenuItem send;
	private JMenuItem get;
	
	public Window(){
		super("SyncString");
		
		setResizable(false);
		setSize(200, 140);
		
		send = new JMenuItem("Send");
		get = new JMenuItem("Get");
		JMenuBar bar = new JMenuBar();
		bar.add(send);
		bar.add(get);
		setJMenuBar(bar);
		send.addActionListener(this);
		get.addActionListener(this);
		
		stringPanel = new DrawString(getWidth(), getHeight() - 25);
		add(stringPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		moveIt = new MoveString(this);
		moveIt.start();
	}
	
	public DrawString getStringPanel(){
		return stringPanel;
	}
	
	public void setStateManager(StateManager manager){
		this.manager = manager;
		manager.setMover(moveIt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == send){
			manager.sendState();
			moveIt.stopRun();
		}
		else if(e.getSource() == get){
			manager.getState();
			moveIt.startRun();
		}
	}
}
