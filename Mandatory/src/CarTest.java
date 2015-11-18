//Prototype implementation of Car Test class
//Mandatory assignment
//Course 02158 Concurrent Programming, DTU, Fall 2015

//Hans Henrik Løvengreen    Oct 6,  2015

public class CarTest extends Thread {

    CarTestingI cars;
    int testno;

    public CarTest(CarTestingI ct, int no) {
        cars = ct;
        testno = no;
    }

    public void run() {
        try {
            switch (testno) { 
            case 0:
                // Demonstration of startAll/stopAll.
                // Should let the cars go one round (unless very fast)
                cars.startAll();
                sleep(3000);
                cars.stopAll();
                break;
            case 1:
            	// Demonstration of the barrier with all cars enabled
            	cars.barrierOn();
            	cars.startAll();
            	sleep(12000);
            	cars.stopAll();
            	cars.barrierOff();
            	break;
            case 2:
            	// Demonstration of the barrier with all but car0 active
            	cars.startAll();
            	cars.barrierOn();
            	sleep(15000);
            	cars.stopAll();
            	cars.barrierOff();
            	break;
            case 3:
            	// Demonstration of barrier shutdown functionality
            	cars.startAll();
            	sleep(200);
            	cars.barrierOn();
            	sleep(10000);
            	cars.barrierShutDown();
            	cars.stopAll();
            	break;
            case 4:
            	// Demonstration of fair alley synchronization
            	cars.startAll();
            	cars.setSlow(true);
            	sleep(15000);
            	cars.stopAll();
            	cars.setSlow(false);
            	break;
            case 5:
            	// Demonstration of removal and restoring of cars
            	cars.startAll();
            	sleep(1500);
            	cars.removeCar(5);
            	cars.removeCar(1);
            	sleep(1500);
            	cars.restoreCar(5);
            	sleep(1500);
            	cars.restoreCar(1);
            	cars.stopAll();
            	break;
            case 19:
                // Demonstration of speed setting.
                // Change speed to double of default values
                cars.println("Doubling speeds");
                for (int i = 1; i < 9; i++) {
                    cars.setSpeed(i,50);
                };
                break;

            default:
                cars.println("Test " + testno + " not available");
            }

            cars.println("Test ended");

        } catch (Exception e) {
            System.err.println("Exception in test: "+e);
        }
    }

}



