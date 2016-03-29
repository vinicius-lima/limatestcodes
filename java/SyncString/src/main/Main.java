package main;

import sync.StateManager;
import draw.Window;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Window window = new Window();
		StateManager manager = new StateManager(window.getStringPanel(),
				Integer.parseInt(args[0]),
				args[1],
				Integer.parseInt(args[2]));
		window.setStateManager(manager);
		window.setVisible(true);
		
		Thread t = new Thread(manager);
		t.start();
		
		try {
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
