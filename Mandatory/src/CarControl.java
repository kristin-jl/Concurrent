
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
    
    static int activeUp;			 // Numbers of active cars from 1-4
    static int activeDown;			 // Numbers of active cars from 5-8
    static int enterUp;				 // Counter with modulo for cars 1-4
    static int enterDown;			 // Counter with modulo for cars 5-8
    
    boolean removed; 				 // Flag for removal of cars
    boolean inAlley;				 // Flag for alley
    
    static Semaphore halt1 = new Semaphore(1);
    static Semaphore halt2 = new Semaphore(1);
    
    //private static Alley alley = new Alley();
    private AlleyMonitor alleyMonitor = new AlleyMonitor();
    private static Bridge bridge = new Bridge();
    
    // Semaphore representation of the map
    private static Semaphore[][] map = new Semaphore[11][12];
    
    
    static {
    	for (int i = 0; i < 11; i++) {
    		for (int j = 0; j < 12; j++) {
    			map[i][j] = new Semaphore(1);
    		}
    	}
    }
    
    private Semaphore active = new Semaphore(1);
    
    //private static Barrier barrier = new Barrier();
    private BarrierMonitor barrier;

    public Car(int no, CarDisplayI cd, Gate g, BarrierMonitor barrier) {

        this.no = no;
        this.cd = cd;
        mygate = g;
        this.barrier = barrier;
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
    	if (pos.equals(new Pos(0,0)) || pos.equals(new Pos(8,1)) || pos.equals(new Pos(9,3))) {
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
   
    // called when a car is about to pass the barrier
   void passBarrier() throws InterruptedException {
	   if ((no < 5 && no > 0 && curpos.equals(new Pos(startpos.row + 1, startpos.col))) ||
			   (no >= 5 && curpos.equals(new Pos(startpos.row - 1, startpos.col))) || 
			   (no == 0 && curpos.equals(new Pos(startpos.row, startpos.col + 1)))) {
		   barrier.sync();
	   } 
   }
   
   // called when a car gets on the bridge
   boolean enterBridge(Pos pos, int no) {
	  if (bridge.isActive() && ((no < 5 && pos.col == 0 && pos.row == 1) || (no >= 5 && pos.col == 4 && pos.row == 0))) {
		  return true;
	  }
	  return false;
   }
   
   // called when a car leaves the bridge
   boolean leaveBridge(Pos pos, int no) {
	  if (bridge.isActive() && ((no < 5 && pos.col == 3 && pos.row == 1) || (no >= 5 && pos.col == 1 && pos.row == 0))) {
		  return true;
	  }
	  return false;
   }
   
   void removeCar() {
	   
	   try {
		active.P();
	   } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	   map[newpos.row][newpos.col].V();
	   removed = true;
	   
	   if (inAlley) {
		  if (no < 5 ) {
			  alleyMonitor.leaveUp();
		  }
		  else {
			  alleyMonitor.leaveDown();
		  }
	   }
   }
   
   void restoreCar() {
       if (removed) {
		   curpos = startpos;
		   active.V();
		   removed = false;
       }
   }
   
   void setAlley(AlleyMonitor alley) {
	   this.alleyMonitor = alley;
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
//                		if (activeUp -1> enterUp){ 
//                			halt2.P();
//                			halt2.V();
//                			enterUp++;
//                			cd.println("EU "+enterUp);
//                		}
//                		else {
//                			enterUp = 0;
//                			cd.println("LU " + enterUp + " release U");
//                			halt1.V();
//                			halt2.P();
//                		}
                		alleyMonitor.enterUp();
                	}
                	if (no >= 5) {
//                		if (activeDown -1 > enterDown){
//                			halt1.P();
//                			halt1.V();
//                			enterDown++;
//                			cd.println("ED " + enterDown);
//                		}
//                		else {
//                			enterDown = 0;
//                			cd.println("LD " + enterDown + " release D");
//                			halt2.V();
//                			halt1.P();
//                		}
                		alleyMonitor.enterDown();
                		
                	}
                	inAlley = true;
                }
                
               if (leaving(curpos)) {
            	   if (no < 5) {
            		   alleyMonitor.leaveUp();
            	   }
            	   else {
            		   alleyMonitor.leaveDown();
            	   }
            	   inAlley = false;
               }
               
               active.P();
               active.V();
               
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
                
                active.P();
                active.V();
                
                passBarrier();
                
                if (enterBridge(curpos, no)) {
             	   bridge.enter();
                }
                
                if(leaveBridge(curpos,no)) {
             	   bridge.leave();
                }
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
    private AlleyMonitor alley = new AlleyMonitor();
    private Bridge bridge = new Bridge();
    

    public CarControl(CarDisplayI cd) {
        this.cd = cd;
        car  = new  Car[9];
        gate = new Gate[9];

        for (int no = 0; no < 9; no++) {
            gate[no] = new Gate();
            // the barrier has to be parsed to the cars
            // so they have the same object for on/off
            car[no] = new Car(no,cd,gate[no], barrier);
            car[no].setAlley(alley);
            car[no].start();
        } 
    }

   public void startCar(int no) {
        gate[no].open();
        if (no == 0) {
			barrier.barrierCar0();
        }
        else if (no <5 ) {
        	alley.activeInc("up");
        }
        else {
        	alley.activeInc("down");
        }
    }

    public void stopCar(int no) {
        gate[no].close();
        if (no == 0){
			barrier.barrierCar0();
        }
        else if (no <5 ) {
        	Car.activeUp--;
        }
        else {
        	Car.activeDown--;
        }   	
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
    
    public void bridgeOn() {
    	this.bridge.on();
    }
    
    public void bridgeOff() {
    	this.bridge.off();
    }

    public void setLimit(int k) { 
//        cd.println("Setting of bridge limit not implemented in this version");
    	try {
			bridge.setLimit(k);
		} catch (InterruptedException e) {}
    	
    }

    public void removeCar(int no) { 
        cd.println("Remove Car not implemented in this version");
        cd.clear(car[no].newpos);
        car[no].removeCar();
    }

    public void restoreCar(int no) { 
        cd.println("Restore Car not implemented in this version");
        car[no].restoreCar();
    }

    /* Speed settings for testing purposes */

    public void setSpeed(int no, int speed) { 
        car[no].setSpeed(speed);
    }

    public void setVariation(int no, int var) { 
        car[no].setVariation(var);
    }

}






