class Bridge {
	private static int limit = 6; // Default value
	private static int onBridge = 0;
	public static Semaphore bridge = new Semaphore(limit); 
	public static Semaphore mutex = new Semaphore(0);
	
	// pos.col >= 1 && pos.col <= 3 && pos.row >= 9 && pos.row <= 2
	
	
	public void enter() throws InterruptedException{
		mutex.P();
		if (onBridge < limit) {
			bridge.P();
			onBridge++ ;
		}
		mutex.V();
	}
	
	public void leave() throws InterruptedException {
		mutex.P();
		onBridge--;
		mutex.V();
	}
	
	public void setLimit(int k) throws InterruptedException {
		mutex.P();
		limit = k;
		bridge = new Semaphore(limit); 
		mutex.V();
	}
}