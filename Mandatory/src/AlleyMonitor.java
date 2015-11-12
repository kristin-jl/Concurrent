class AlleyMonitor {
	private int up, down, activeUp, activeDown, enteredUp, enteredDown;
	private boolean locked;
	
	public synchronized void enterUp() throws InterruptedException {
		while(down > 0 || locked) {
			wait();
		}
		if (activeUp > enteredUp) {
			while(locked) {
				wait();
			}
			enteredUp++;
			notifyAll();
		} else {
			while(down > 0 && locked) {
				wait();
			}
			enteredUp++;
		}
		up++;
	}
	
	public synchronized void enterDown() throws InterruptedException {
		while(up > 0 || !locked) {
			wait();
		}
		if (activeDown > enteredDown) {
			enteredDown++;
			notifyAll();
		} else {
			while (up > 0 && !locked) {
				wait();
			}
			enteredDown++;
		}
		down++;
	} 
	
	public synchronized void leaveUp() {
		up--;
		if (enteredUp >= activeUp && activeDown != 0 ) {
			enteredUp = 0;
			locked = true;
		}
		notifyAll();
	}
	
	public synchronized void leaveDown() {
		down--;
		if (enteredDown >= activeDown && activeUp != 0) {
			enteredDown = 0;
			locked = false;
		}
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
		notifyAll();
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
		notifyAll();
	}
}

// Change