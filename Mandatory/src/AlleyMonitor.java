class AlleyMonitor {
	private int up, down, activeUp, activeDown, enteredUp, enteredDown;
	
	public synchronized void enterUp() throws InterruptedException {
		while(down > 0) {
			wait();
		}
		if (activeUp > enteredUp && down == 0) {
			enteredUp++;
			
		} else {
			enteredUp = 0;
			while(enteredDown <= 4 && enteredDown > 0) {
				wait();
			}
		}
		up++;
	}
	
	public synchronized void enterDown() throws InterruptedException {
		while(up > 0) {
			wait();
		}
		if (activeDown > enteredDown && up == 0) {
			enteredDown++;
		} else {
			enteredDown = 0;
			while (enteredUp <= 4 && enteredUp > 0) {
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