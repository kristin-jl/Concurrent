
class BarrierMonitor {
	private int count, n = 8;
	private boolean OK, active;
	
	public synchronized void sync() {
		while(OK) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		count++;
		if (count == n) {
			OK = true;
			notifyAll();
		}
		while (!OK) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		count--;
		if (count == 0) {
			OK = false;
			notifyAll();
		}
	}
	
	public synchronized void on() {
		active = true;
	}
	
	public synchronized void off() {
		active = false;
		notifyAll();
	}
}
