package window;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import manager.FileManager;
import manager.StateManager;

public class Screen extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel panels;
	private ConnectionPanel connect;
	private EditionPanel edit;
	private JFileChooser chooser;
	private StateManager stateManager;
	
	private JMenuItem newFile;
	private JMenuItem open;
	private JMenuItem save;
	private JMenuItem exit;
	private JMenuItem transfer;
	
	public Screen() {
		super("SyncTextFirebase");
		
		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
		chooser.setCurrentDirectory(new File("/home/vinicius/workspace/SyncTextEditor"));
		
		panels = new JPanel(new CardLayout());
		edit = new EditionPanel();
		stateManager = new StateManager(edit);
		connect = new ConnectionPanel(this, stateManager);
		panels.add(connect, "Connect");
		panels.add(edit, "Edit");
		
		super.add(panels, BorderLayout.CENTER);
		super.setJMenuBar(createMenuBar());
		super.setSize(400, 100);
		//super.pack();
		super.addWindowListener(new ConnectionCleaner(stateManager));
		super.setLocationRelativeTo(null);
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu menu = new JMenu("File");
		newFile = new JMenuItem("New");
		newFile.addActionListener(this);
		open = new JMenuItem("Open");
		open.addActionListener(this);
		save = new JMenuItem("Save");
		save.addActionListener(this);
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		transfer = new JMenuItem("Transfer");
		transfer.addActionListener(this);
		
		menu.add(newFile);
		menu.add(open);
		menu.add(save);
		menu.add(exit);
		
		bar.add(menu);
		bar.add(transfer);
		
		return bar;
	}
	
	public void changePanel(String panelName) {
		CardLayout cl = (CardLayout)panels.getLayout();
		cl.show(panels, panelName);
		super.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newFile){
			int opt = JOptionPane.showConfirmDialog(null, "Do you want to create a new file?",
					null, JOptionPane.YES_NO_OPTION);
			if(opt == JOptionPane.YES_OPTION)
				edit.setText("");
		}
		else if(e.getSource() == open){
			int returnVal = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				String text = FileManager.openFile(chooser.getSelectedFile());
				edit.setText(text);
				changePanel("Edit");
			}
		}
		else if(e.getSource() == save){
			int returnVal = chooser.showSaveDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				String text = edit.getText();
				FileManager.saveFile(chooser.getSelectedFile(), text);
			}
		}
		else if(e.getSource() == exit){
			stateManager.cleanUp();
			System.exit(0);
		}
		else if(e.getSource() == transfer){
			// TODO: If state manager is not null.
			stateManager.transferState();
		}
	}

}
