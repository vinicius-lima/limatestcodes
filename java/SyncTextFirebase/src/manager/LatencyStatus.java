package manager;

public class LatencyStatus {
	private long initialTime;
	private long finalTime;
	private long difference;
	
	public void setInitialTime(long time) {
		initialTime = time;
	}
	
	public void setFinalTime(long time) {
		finalTime = time;
	}
	
	public long getDifference() {
		difference = finalTime - initialTime;
		return difference;
	}
	
	public void clear() {
		initialTime = finalTime = difference = 0L;
	}
}
