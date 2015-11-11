// implementation based on solution from Synchronization Mechanisms page 9
class BarrierMonitor {
	private int count, n = 8;
	private boolean OK, active, car0Active;
	
	public synchronized void sync() {
		if (active) {
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
			
			if (active) {
				count--;
				if (count == 0) {
					OK = false;
					notifyAll();
				}
				
				if (!car0Active && n == 9) {
					n = 8;
				}
			}
			
		}
	}
	
	public synchronized void on() {
		active = true;
		count = 0;
		OK = false;
	}
	
	public synchronized void off() {
		active = false;
		OK = true;
		notifyAll();
	}
	
	public synchronized void barrierCar0() {
		if (n == 8) {
			n = 9;
			car0Active = true;
		} else {
			car0Active = false;
		}
	}
	
	public synchronized void shutdown() {
		
	}
}
