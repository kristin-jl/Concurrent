
class Barrier {
	
	private static boolean active;
	private static int count, n = 8;
	private static Semaphore turnstile = new Semaphore(0), turnstile2 = new Semaphore(1);
	private static Semaphore mutex = new Semaphore(1);
	
	// solution is from The Little Book of Semaphores pages 39-41
	public void sync() throws InterruptedException {
			if (active) {
				mutex.P();
				count++;
				if (count == n) {
					turnstile2.P();
					turnstile.V();
				}
				mutex.V();
				
				turnstile.P();
				turnstile.V();
				
				mutex.P();
				count--;
				if (count == 0) {
					turnstile.P();
					turnstile2.V();
				}
				mutex.V();
				
				turnstile2.P();
				turnstile2.V();
			}
		}
		
	
	
	public void on() {
		active = true;
	}
	
	public void off() {
		active = false;
	}
	
}
