
class Alley {
	
	private static Semaphore up = new Semaphore(1); 
	private static Semaphore down = new Semaphore(1);
	private static boolean passing;
	public static int inAlley = 0;
	
	public void enter(int no) throws InterruptedException {
			switch(no) {
			case 1:
			case 2:
			case 3:
			case 4:
				up.P();
				if (!passing) {
					passing = true;
					down.P();
				}
				inAlley++;
				up.V();
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				down.P();
				if (!passing) {
					passing = true;
					up.P();
				}
				inAlley++;
				down.V();
				break;
			default:
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
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				if (--inAlley == 0) {
					passing = false;
					up.V();
				}
				break;
				default:
			}
	}

}
