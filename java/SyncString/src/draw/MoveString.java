package draw;

public class MoveString extends Thread{
	private Window window;
	private boolean running;
	
	public MoveString(Window window){
		this.window = window;
		running = true;
	}

	@Override
	public void run() {
		//setVisible(true);
		try {
			while(true){
				Thread.sleep(10);
				if(running)
					window.repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startRun(){
		running = true;
	}
	
	public void stopRun(){
		running = false;
	}
}
