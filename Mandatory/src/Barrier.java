
class Barrier {
	
	private static boolean active, car0Active;
	private static int count, n = 8;
	private static Semaphore turnstile = new Semaphore(0), turnstile2 = new Semaphore(1);
	private static Semaphore mutex = new Semaphore(1);
	
	// solution is from The Little Book of Semaphores pages 39-41
	public void sync() throws InterruptedException {
		mutex.P();
		count++;
		if (count == n) { // if true, let cars that are waiting at the line proceed
			turnstile2.P();
			turnstile.V();
		}
		mutex.V();
			
		// wait if not all cars have arrived
		turnstile.P();
		turnstile.V();
		// if the barrier is not active at this point release all cars that have been blocked
		if (!active) {
			return;
		}
				
		mutex.P();
		count--;
		if (count == 0) { // if true, let all cars pass the line
			turnstile.P();
			turnstile2.V();
		}
		mutex.V();
			
		// wait for all cars to be ready to pass the line
		turnstile2.P();
		turnstile2.V();
		mutex.P();
		// if car0 has been deactivated at this point, make sure that n is set to 8
		if (!car0Active && n == 9) {
			n = 8;
		}
		mutex.V();
	}
		
	
	
	public void on() {
		active = true;
		// make sure that the turnstile semaphore and the count are reset
		turnstile = new Semaphore(0);
		try {
			mutex.P();
		} catch (InterruptedException e) {}
		count = 0;
		mutex.V();
	}
	
	public void off() {
		active = false;
		// make sure that any cars that are at the barrier are released
		turnstile.V();
		
	}
	
	// is the barrier active
	public boolean isActive() {
		return active;
	}
	
	// used to activate or deactivate barrier for car0
	public void barrierCar0() throws InterruptedException {
		mutex.P();
		if (n == 8) {
			n = 9;
			car0Active = true;
		} else {
			car0Active = false;
		}
		mutex.V();
	}
	
}
