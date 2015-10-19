
class Alley {
	
	private Semaphore[] cars = new Semaphore[8];
	
	private int inAlley = 0;
	
	public void enter(int no) throws InterruptedException {
		cars[no].P();
		if (inAlley++ == 0) {
			switch(no) {
			case 1:
			case 2:
			case 3:
			case 4:
				for (int i = 4; i < 8; i++) {
					cars[i].P();
				}
			default:
				for (int i = 0; i < 4; i++) {
					cars[i].P();
				}
			}
		}
	}
	
	public void leave(int no) {
		cars[no].V();
		if (--inAlley == 0) {
			switch(no) {
			case 1:
			case 2:
			case 3:
			case 4:
				for (int i = 4; i < 8; i++) {
					cars[i].V();
				}
			default:
				for (int i = 0; i < 4; i++) {
					cars[i].V();
				}
			}
		}
	}

}
