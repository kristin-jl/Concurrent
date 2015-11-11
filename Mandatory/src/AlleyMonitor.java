class AlleyMonitor {
	private static int up, down, activeUp, activeDown, enterUp, enterDown;
	
	public synchronized void enterUp() throws InterruptedException {
		while(down > 0) {
			wait();
		}
		if (activeUp > enterUp && down == 0) {
			enterUp++;
			notifyAll();
			
		} else {
			enterUp = 0;
			notifyAll();
			while(enterDown <= 4 && enterDown >= 0) {
				wait();
			}
		}
		up++;
	}
	
	public synchronized void enterDown() throws InterruptedException {
		while(up > 0) {
			wait();
		}
		if (activeDown > enterDown && up == 0) {
			enterDown++;
			notifyAll();
		} else {
			enterDown = 0;
			notifyAll();
			while (enterUp <= 4 && enterUp >= 0) {
				wait();
			}
		}
		down++;
	}
	
	public synchronized void leaveUp() {
		up--;
		notifyAll();
	}
	
	public synchronized void leaveDown() {
		down--;
		notifyAll();
	}
	
	public synchronized void activeInc(String direction) {
		switch(direction) {
		case "up": 
			activeUp++;
			break;
		case "down":
			activeDown++;
			break;
		default:
			break;
		}
	}
	
	public synchronized void activeDec(String direction) {
		switch(direction) {
		case "up":
			activeUp--;
			break;
		case "down":
			activeDown--;
			break;
		default:
			break;	
		}
	}
}

// Change