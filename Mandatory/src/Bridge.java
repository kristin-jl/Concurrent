class Bridge {
	private int limit = 6; // Default value
	private int onBridge;
	public static Semaphore bridge = new Semaphore(limit); 
	public static Semaphore mutex = new Semaphore(0);
	
	public void enter(int no) {
		mutex.P();
		if (onBridge < limit) {
			bridge.P();
			onBridge++ ;
		}
		else {
			bridge.V();
		}
		mutex.V();
	}
	
	public void leave(int no) {
		mutex.P();
		onBridge--;
		mutex.V();
	}
	
	public void setLimit(int k) {
		limit = k;
		bridge = new Semaphore(limit); 
	}
}