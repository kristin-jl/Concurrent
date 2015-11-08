class AlleyMonitor {
	private int up, down;
	
	public synchronized void enterUp() throws InterruptedException {
		while(down > 0) {
			wait();
		}
		up++;
	}
	
	public synchronized void enterDown() throws InterruptedException {
		while(up > 0) {
			wait();
		}
		down++;
	}
	
	public synchronized void leaveUp() {
		up--;
		notifyAll();
	}
	
	public synchronized void leaveDown() {
		down--;
		notifyAll();
	}
}