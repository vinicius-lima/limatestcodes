package window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EditionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextArea editionArea;
	private JScrollPane scroll;
	
	public EditionPanel() {
		super(new FlowLayout());
		
		editionArea = new JTextArea();
		//editionArea.setSize(400, 400);
		editionArea.setLineWrap(true);
		
		scroll = new JScrollPane(editionArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(400, 400));
		
		super.add(scroll, BorderLayout.CENTER);
	}
	
	public void setText(String text) {
		editionArea.setText(text);
	}
	
	public String getText() {
		return editionArea.getText();
	}

}
