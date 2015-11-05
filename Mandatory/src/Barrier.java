
class Barrier {
	
	private static boolean on;

	public void sync() throws InterruptedException {
		if (on) {
			
		}
	}
	
	public void on() {
		on = true;
	}
	
	public void off() {
		on = false;
	}
	
}
