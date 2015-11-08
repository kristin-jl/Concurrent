class Bridge {
	private static int limit = 6; // Default value
	public static Semaphore bridge = new Semaphore(limit - 1); 
	public static Semaphore mutex = new Semaphore(1);
	
	private static boolean active = false;

	// pos.col >= 1 && pos.col <= 3 && pos.row >= 9 && pos.row <= 2
	
	public boolean isActive() {
		return active;
	}
	
	public void enter() throws InterruptedException{
		mutex.P();
		bridge.P();
		mutex.V();
	}
	
	public void leave() throws InterruptedException {
		bridge.V();
	}
	
	public void setLimit(int k) throws InterruptedException {
		mutex.P();
		limit = k;
		bridge = new Semaphore(limit - 1); 
		mutex.V();
	}
	
	public void on() {
		active = true;
	}
	
	public void off() {
		active = false;
	}
}