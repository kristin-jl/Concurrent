class Bridge {
	private static int limit = 6; // Default value
	private static int onBridge;
	public static Semaphore bridge = new Semaphore(limit); 
	public static Semaphore mutex = new Semaphore(0);
	
	public void enter(int no) {
		try {
			mutex.P();
		} catch (InterruptedException e1) {}
		if (onBridge < limit) {
			try {
				bridge.P();
			} catch (InterruptedException e) {}
			onBridge++ ;
		}
		else {
			bridge.V();
		}
		mutex.V();
	}
	
	public void leave(int no) {
		try {
			mutex.P();
		} catch (InterruptedException e) {}
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