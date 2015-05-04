/**
 * Simulator
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.util.ArrayList;
import java.util.Random;


public class Simulator implements Runnable {

    public static final int COMPLETELY_ARBITRARY_MS_INCREMENT = 50; // should be 100 for assignment 4

    private static DisplayClient _dc;

    // number of GroundVehicles waiting for controls
    protected int _vehicleControlQueue = 0; /* shared resource */

    // number of GroundVehicles waiting to advance state
    protected int _vehicleAdvanceQueue = 0; /* shared resource */

    // ID of VehicleController whose turn it is to update
    protected int _turnID = 0;  /* shared resource */

    private ArrayList<GroundVehicle> _vehicleList;  // list of GroundVehicles inside Simulator

    private double _startupMS;  // time when the Simulator starts running


/* SETTINGS */

    private final boolean print = true;         // set to true for print statements
    private final boolean debug = true;         // set to true for debug statements

    private static final boolean lead   = false;    // set to true for LeadingController
    private static final boolean lead3  = false;    // set to true for multiple LeadingController test
    private static final boolean circ   = true;     // one CircleController

/* CONSTRUCTORS */
    public Simulator() {
        _vehicleList = new ArrayList<GroundVehicle>();
        _dc = null;
    }

    public Simulator(DisplayClient dc) {
        _vehicleList = new ArrayList<GroundVehicle>();
        _dc = dc;
    }

/* STATIC METHODS */
    /**
     * Generates random double within a specified range.
     * @param rangeMin lower bound of range
     * @param rangeMax upper bound of range
     * @return random double in range
     */
    public static double randomDoubleInRange(double rangeMin, double rangeMax){
        Random r = new Random();
        double doubleInRange = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return doubleInRange;
    }

    /**
     * Generates random starting point and orientation for a GroundVehicle, with
     * x, y, and theta values within the allowable ranges.
     * @return random starting position [x, y, theta] for GroundVehicle
     */
    public static double[] randomStartingPosition() {

        double minTheta = -Math.PI;        // inclusive
        double maxTheta = Math.PI - 1e-6;  // non-inclusive

        double[] startingPosition = {randomDoubleInRange(0,100.0), randomDoubleInRange(0,100.0), randomDoubleInRange(minTheta,maxTheta)}; // [x, y, theta]
        return startingPosition;
    }

/* GET METHODS */
    /**
     * @param index index of desired GroundVehicle in _vehicleList
     * @return GroundVehicle at given index
     */
    public synchronized GroundVehicle getVehicle(int index) throws IndexOutOfBoundsException {

        // if index is greater than size of list
        if (index >= _vehicleList.size()) {
            throw new IndexOutOfBoundsException("GroundVehicle doesn't exist");
        }
        return _vehicleList.get(index);
    }

    /**
     * Returns number of queued GroundVehicles waiting for controls.
     * @return value of _vehicleControlQueue
     */
    public synchronized int getControlQueueSize() {
        return _vehicleControlQueue;
    }

    public DisplayClient getDisplayClient() {
        return _dc;
    }

/* OTHER METHODS */
    /**
     * Adds a GroundVehicle to the list of GroundVehicles inside Simulator
     * @param gv
     */
    public synchronized void addVehicle(GroundVehicle gv) {
        // add vehicle to list
        _vehicleList.add(gv);

        // associate this simulator with the groundVehicle
        gv.setSimulator(this);

        // increase counter of vehicles waiting for controls
        _vehicleControlQueue++;
    }

    public synchronized double getCurrentMSec() {
        return ((System.nanoTime()/1e6) - _startupMS);
    }

    public synchronized double getStartupMSec() {
        return _startupMS;
    }

    public synchronized long getCurrentRealTimeMSec() {
//        long currentTime = System.nanoTime();
//        return (currentTime - _startupTime);
        return 0; //TODO: fix
    }

    /* RUN METHOD */
    public void run() {

        // clear display of previous trajectories
        _dc.clear();
        _dc.traceOn();  // display mode showing complete trajectories,
        // not just current positions

        double[] gvX = new double[_vehicleList.size()];
        double[] gvY = new double[_vehicleList.size()];
        double[] gvTheta = new double[_vehicleList.size()];

        _startupMS = System.nanoTime()/1e6;
        double lastUpdateTime = getCurrentMSec();

        while (getCurrentMSec() < 100*1e3) { // while time less than 100s

            if ((getCurrentMSec() - lastUpdateTime) >= COMPLETELY_ARBITRARY_MS_INCREMENT) { // update once every 100ms

                synchronized (this) {

                    for (int i = 0; i < _vehicleList.size(); i++) {     // iterate over list of i vehicles
                        GroundVehicle vehicle = _vehicleList.get(i);    // get vehicle at index i
                        double[] position = vehicle.getPosition();      // get [x, y, theta] of vehicle
                        gvX[i] = position[0];
                        gvY[i] = position[1];
                        gvTheta[i] = position[2];

                        if (debug) {
                            System.out.println(vehicle.getNumID() + "[>  ] position on display is being updated");
                            //                        System.out.println(vehicle._ID + " " + vehicle.getNumID() + "[>  ] position on display is being updated");
                        }
                    } // end for (int i = 0; i < _vehicleList.size(); i++)

                    notifyAll();
                } // end synchronized (this)

                lastUpdateTime = getCurrentMSec();
                // update display client with vehicle positions
                _dc.update(_vehicleList.size(), gvX, gvY, gvTheta);

            } // end if (100ms since last update)
        } // end while (time < 100s)

        _dc.traceOff();
        System.out.println("Cleared");

    } // end run()


/* MAIN METHOD */
    /*
	public static void main(String[] args) {

        String host;
        int nVehicles = 0;

        // check if two command line arguments are present
        if (args.length == 2) {
            try {
                // try to parse the first argument as as integer
                if (Integer.parseInt(args[0]) == 0) {
                    System.err.println("Number of vehicles must be greater than zero.");
                    System.exit(-1);
                } else {
                    nVehicles = Integer.parseInt(args[0]);
                }
            } catch (NumberFormatException e) {
                System.err.println("First argument must be an integer.");
                System.exit(-1);
            }
        } else { // if there are not two arguments
            System.err.println("Expected format: $ java Simulator <number of vehicles> <IP of running DisplayServer>");
            System.exit(-1);
        }


        // get IP address from second command line argument
        host = args[1];

        // create DisplayClient
        DisplayClient dc = new DisplayClient(host);

        // create new Simulator
        Simulator sim = new Simulator(dc);

        if (lead) { // have vehicles follow LeadingController

            GroundVehicle lv = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
            LeadingController lc = new LeadingController(sim, lv);
            sim.addVehicle(lv);
            lv.start();
            lc.start();
            // create n ground vehicles at random positions and velocities
            for (int i = 1; i < nVehicles; i++) {

                GroundVehicle gv = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                VehicleController vc = new FollowingController(sim, gv, lv);
                lc.addFollower(gv);
                sim.addVehicle(gv);
                gv.start();
                vc.start();
            }

        } else if (lead3) { // have three LeadingControllers

            GroundVehicle lv    = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
            GroundVehicle lv2   = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
            GroundVehicle lv3   = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
            LeadingController lc    = new LeadingController(sim, lv);
            LeadingController lc2   = new LeadingController(sim, lv2);
            LeadingController lc3   = new LeadingController(sim, lv3);
            sim.addVehicle(lv);
            sim.addVehicle(lv2);
            sim.addVehicle(lv3);
            lv.start();
            lv2.start();
            lv3.start();
            lc.start();
            lc2.start();
            lc3.start();
            lc.addFollower(lv2);
            lc.addFollower(lv3);
            lc2.addFollower(lv);
            lc2.addFollower(lv3);
            lc3.addFollower(lv);
            lc3.addFollower(lv2);

        } else if (circ) { // one CircleController

            GroundVehicle lv = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0, 10), randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
            CircleController cc = new CircleController(sim, lv);
            sim.addVehicle(lv);
            lv.start();
            cc.start();

        } else { // have vehicles follow RandomController

            // create n ground vehicles at random positions and velocities
            for (int i = 0; i < nVehicles; i++) {
                GroundVehicle gv = new GroundVehicle(randomStartingPosition(), randomDoubleInRange(0,10), randomDoubleInRange(-Math.PI/4,Math.PI/4));
                VehicleController vc;
                if (i == 0) {
                    // the first vehicle should use a RandomController
                    vc = new RandomController(sim, gv);
                } else {
                    // the other vehicles should use FollowingController to follow the first vehicle
                    GroundVehicle target = sim.getVehicle(0);
                    vc = new FollowingController(sim, gv, target);
                }
                sim.addVehicle(gv);
                gv.start();
                vc.start();
            }
        }

        // run simulator
        sim.run();
		 
	}
	*/
}
