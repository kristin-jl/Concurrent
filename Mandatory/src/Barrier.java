
class Barrier {
	
	private static boolean on;
	private static Semaphore mutex = new Semaphore(1), protect = new Semaphore(1);
	private static int atBar;
	private static int count;
	
	public void sync() throws InterruptedException {
		protect.P();
		if (on) {
			if (atBar == 7) {
				atBar--;
				off();
				if (atBar == 0) {
					on();
			} else {
				atBar++;
			}
		}
		protect.V();
//		atBar++;
//		
//		if (atBar == 8 && on){
//			off();
//			% dec
//		}
//		if (atBar == 0 && !on) {
//			on();
//		}
//		protect.V();
	}
		
			mutex.P();
			if (on && atBAr==8) {
				if (atBar == 8) {
					atBar = 0;
					release();
					mutex.V();
					return;
				}
				mutex.V();
				bar.P();
			}
		}
		
	
	
	public void on() {
		on = true;
	}
	
	public void off() {
		on = false;
	}
	
}
