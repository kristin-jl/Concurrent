
class Alley {
	
	private Semaphore up, down = new Semaphore(1);
	private boolean passing;
	private int inAlley = 0;
	
	public void enter(int no) throws InterruptedException {
			switch(no) {
			case 1:
			case 2:
			case 3:
			case 4:
				up.P();
				if (!passing) {
					passing = true;
				}
				inAlley++;
				down.P();
				up.V();
			default:
				down.P();
				if (!passing) {
					passing = true;
				}
				inAlley++;
				up.P();
				down.V();
			}
	}
	
	public void leave(int no) {
			switch(no) {
			case 1:
			case 2:
			case 3:
			case 4:
				if (--inAlley == 0) {
					passing = false;
					down.V();
				}
			default:
				if (--inAlley == 0) {
					passing = false;
					up.V();
				}
			}
	}

}
