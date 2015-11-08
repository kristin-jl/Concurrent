
import java.awt.Color;

class Gate {

    Semaphore g = new Semaphore(0);
    Semaphore e = new Semaphore(1);
    boolean isopen = false;

    public void pass() throws InterruptedException {
        g.P(); 
        g.V();
    }

    public void open() {
        try { e.P(); } catch (InterruptedException e) {}
        if (!isopen) { g.V();  isopen = true; }
        e.V();
    }

    public void close() {
        try { e.P(); } catch (InterruptedException e) {}
        if (isopen) { 
            try { g.P(); } catch (InterruptedException e) {}
            isopen = false;
        }
        e.V();
    }

}

class Car extends Thread {

    int basespeed = 100;             // Rather: degree of slowness
    int variation =  50;             // Percentage of base speed

    CarDisplayI cd;                  // GUI part

    int no;                          // Car number
    Pos startpos;                    // Startpositon (provided by GUI)
    Pos barpos;                      // Barrierpositon (provided by GUI)
    Color col;                       // Car  color
    Gate mygate;                     // Gate at startposition


    int speed;                       // Current car speed
    Pos curpos;                      // Current position 
    Pos newpos;                      // New position to go to
    
    //private static Alley alley = new Alley();
    private static AlleyMonitor alleyMonitor = new AlleyMonitor();
    
    // Semaphore representation of the map
    private static Semaphore[][] map = new Semaphore[11][12];
    
    static {
    	for (int i = 0; i < 11; i++) {
    		for (int j = 0; j < 12; j++) {
    			map[i][j] = new Semaphore(1);
    		}
    	}
    }
    
    //private static Barrier barrier = new Barrier();
    private static BarrierMonitor barrier = new BarrierMonitor();

    public Car(int no, CarDisplayI cd, Gate g) {

        this.no = no;
        this.cd = cd;
        mygate = g;
        startpos = cd.getStartPos(no);
        barpos = cd.getBarrierPos(no);  // For later use

        col = chooseColor();

        // do not change the special settings for car no. 0
        if (no==0) {
            basespeed = 0;  
            variation = 0; 
            setPriority(Thread.MAX_PRIORITY); 
        }
    }

    public synchronized void setSpeed(int speed) { 
        if (no != 0 && speed >= 0) {
            basespeed = speed;
        }
        else
            cd.println("Illegal speed settings");
    }

    public synchronized void setVariation(int var) { 
        if (no != 0 && 0 <= var && var <= 100) {
            variation = var;
        }
        else
            cd.println("Illegal variation settings");
    }

    synchronized int chooseSpeed() { 
        double factor = (1.0D+(Math.random()-0.5D)*2*variation/100);
        return (int)Math.round(factor*basespeed);
    }

    private int speed() {
        // Slow down if requested
        final int slowfactor = 3;  
        return speed * (cd.isSlow(curpos)? slowfactor : 1);
    }

    Color chooseColor() { 
        return Color.blue; // You can get any color, as longs as it's blue 
    }

    Pos nextPos(Pos pos) {
        // Get my track from display
        return cd.nextPos(no,pos);
    }

    boolean atGate(Pos pos) {
        return pos.equals(startpos);
    }
    
    // called before the car enters the alley
    boolean entering(Pos pos) {
    	if (pos.equals(new Pos(0,1)) || pos.equals(new Pos(8,1)) || pos.equals(new Pos(9,3))) {
    		return true;
    	}
    	return false;
    }
    
    // called when the car leaves the alley
    boolean leaving(Pos pos) {
    	if (pos.equals(new Pos(1,1)) || pos.equals(new Pos(10,2))) {
    		return true;
    	}
    	return false;
    }
    
   void passBarrier() throws InterruptedException {
	   if ((no < 5 && no > 0 && curpos.equals(new Pos(startpos.row + 1, startpos.col))) ||
			   (no >= 5 && curpos.equals(new Pos(startpos.row - 1, startpos.col))) || 
			   (no == 0 && curpos.equals(new Pos(startpos.row, startpos.col + 1)))) {
		   barrier.sync();
	   } 
   }

   public void run() {
        try {

            speed = chooseSpeed();
            curpos = startpos;
            cd.mark(curpos,col,no);

            while (true) { 
                sleep(speed());
                
                //cd.println(curpos.toString());

                if (atGate(curpos)) { 
                    mygate.pass(); 
                    speed = chooseSpeed();
                }
                
                if (entering(curpos)) {
                	if (no < 5) {
                		alleyMonitor.enterUp();
                	}
                	else {
                		alleyMonitor.enterDown();
                	}
                }
                
               if (leaving(curpos)) {
            	   if (no < 5) {
            		   alleyMonitor.leaveUp();
            	   }
            	   else {
            		   alleyMonitor.leaveDown();
            	   }
               }
               
               newpos = nextPos(curpos);
               
               // check that there is not a another car on the next position
               if (no != 0)
            	   map[newpos.row][newpos.col].P();
               
                //  Move to new position 
                cd.clear(curpos);
                cd.mark(curpos,newpos,col,no);
                sleep(speed());
                cd.clear(curpos,newpos);
                cd.mark(newpos,col,no);
                
                // leave the current position
                if (no != 0)
                	map[curpos.row][curpos.col].V();

                curpos = newpos;
                
                passBarrier();
            }

        } catch (Exception e) {
            cd.println("Exception in Car no. " + no);
            System.err.println("Exception in Car no. " + no + ":" + e);
            e.printStackTrace();
        }
    }

}

public class CarControl implements CarControlI{

    CarDisplayI cd;           // Reference to GUI
    Car[]  car;               // Cars
    Gate[] gate;              // Gates
    //private Barrier barrier = new Barrier();
    private BarrierMonitor barrier = new BarrierMonitor();

    public CarControl(CarDisplayI cd) {
        this.cd = cd;
        car  = new  Car[9];
        gate = new Gate[9];

        for (int no = 0; no < 9; no++) {
            gate[no] = new Gate();
            car[no] = new Car(no,cd,gate[no]);
            car[no].start();
        } 
    }

   public void startCar(int no) {
        gate[no].open();
        if (no == 0)
			barrier.barrierCar0();
    }

    public void stopCar(int no) {
        gate[no].close();
        if (no == 0)
			barrier.barrierCar0();
        	
    }

    public void barrierOn() { 
        //cd.println("Barrier On not implemented in this version");
    	this.barrier.on();
    }

    public void barrierOff() { 
        //cd.println("Barrier Off not implemented in this version");
    	this.barrier.off();
    }

    public void barrierShutDown() { 
        cd.println("Barrier shut down not implemented in this version");
        // This sleep is for illustrating how blocking affects the GUI
        // Remove when shutdown is implemented.
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        // Recommendation: 
        //   If not implemented call barrier.off() instead to make graphics consistent
    }

    public void setLimit(int k) { 
        cd.println("Setting of bridge limit not implemented in this version");
    }

    public void removeCar(int no) { 
        cd.println("Remove Car not implemented in this version");
    }

    public void restoreCar(int no) { 
        cd.println("Restore Car not implemented in this version");
    }

    /* Speed settings for testing purposes */

    public void setSpeed(int no, int speed) { 
        car[no].setSpeed(speed);
    }

    public void setVariation(int no, int var) { 
        car[no].setVariation(var);
    }

}






