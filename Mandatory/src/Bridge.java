class Bridge {
	private static int limit = 6; // Default value
	public static Semaphore bridge = new Semaphore(limit - 1); 
	public static Semaphore mutex = new Semaphore(1);
	
	private static boolean active = false;
	
	public boolean isActive() {
		return active;
	}
	
	public void enter() throws InterruptedException{
		bridge.P();
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
		bridge = new Semaphore(limit - 1);
		mutex = new Semaphore(1);
	}
	
	public void off() {
		active = false;
		bridge = new Semaphore(limit - 1);
		mutex = new Semaphore(1);
	}
}