class AlleyMonitor {
	private int up, down, activeUp, activeDown, enteredUp, enteredDown;
	private boolean locked;
	
	public synchronized void enterUp() throws InterruptedException {
		while(down > 0) {
			wait();
		}
		if (activeUp > enteredUp) {
			while(locked) {
				wait();
			}
			enteredUp++;
			notifyAll();
		} else {
			enteredUp = 0;
			locked = true;
			notifyAll();
			while(enteredDown > 0 && locked) {
				wait();
			}
			enteredUp++;
		}
		up++;
	}
	
	public synchronized void enterDown() throws InterruptedException {
		while(up > 0) {
			wait();
		}
		if (activeDown > enteredDown) {
			enteredDown++;
			notifyAll();
		} else {
			enteredDown = 0;
			locked = false;
			notifyAll();
			while (enteredUp > 0) {
				wait();
			}
			enteredDown++;
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