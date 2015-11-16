
class Alley {
	
	private static Semaphore up = new Semaphore(1); 
	private static Semaphore down = new Semaphore(1);
	private static Semaphore mutex = new Semaphore(1);
	private static boolean passing;
	public static int inAlley = 0;
	
	public void enter(int no) throws InterruptedException {
			if (no < 5) {
				up.P();
				mutex.P();
				if (!passing) {
					passing = true;
					down.P();
				}
				inAlley++;
				mutex.V();
				up.V();
			} else {
				down.P();
				mutex.P();
				if (!passing) {
					passing = true;
					up.P();
				}
				inAlley++;
				mutex.V();
				down.V();	
			}
	}
	
	public void leave(int no) throws InterruptedException {
			mutex.P();
			if (no < 5) {
				if (--inAlley == 0) {
					passing = false;
					down.V();
				}
			} else {
				if (--inAlley == 0) {
					passing = false;
					up.V();
				}
			}
			mutex.V();
	}

}
