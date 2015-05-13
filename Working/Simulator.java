/**
 * Simulator
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


public class Simulator extends Thread {

    public static final int SIM_MS_INCREMENT = 50; // should be 100 for assignment 4
    public static final int USER1 = 1;
    public static final int USER2 = 2;
    public static final int LEADING = 3;
    public static final int FOLLOWING = 4;



    private long _startupTime;  // time when the VehicleController starts running

    private static DisplayClient _dc;

    // set maximum simulation size based on DisplayServer window dimensions
    public static final int SIM_X = DisplayServer.DISPLAY_X/5;
    public static final int SIM_Y = DisplayServer.DISPLAY_Y/5;



    // number of GroundVehicles waiting for controls
    protected int _vehicleControlQueue = 0; /* shared resource */

    // number of GroundVehicles waiting to advance state
    protected int _vehicleAdvanceQueue = 0; /* shared resource */

    // ID of VehicleController whose turn it is to update
    protected int _turnID = 0;  /* shared resource */

    public List<GroundVehicle> _vehicleList;  // list of GroundVehicles inside Simulator

    // user controllers //TODO: req
    private UserController _uc1;
    private UserController _uc2;

    private List<Projectile> _projectileList;  // list of projectiles inside Simulator

    private double _startupMS;  // time when the Simulator starts running
    
    private ArrayList<FollowingController> _followerList;  // list of FollowingControllers inside Simulator
    private ArrayList<LeadingController> _leaderList;  // list of LeadingControllers inside Simulator


/* SETTINGS */

    private final boolean print = false;         // set to true for print statements
    private final boolean debug = false;         // set to true for debug statements

    private static final boolean lead   = false;    // set to true for LeadingController
    private static final boolean lead3  = false;    // set to true for multiple LeadingController test
    private static final boolean circ   = true;     // one CircleController

    private static final boolean debug_projectiles   = FinalProjectile.debug_projectiles;     // projectile debug statements


    /* CONSTRUCTORS */
    public Simulator() {
        //TODO: req copyonwrite arrays
        _vehicleList = new CopyOnWriteArrayList<GroundVehicle>();
        _projectileList = new CopyOnWriteArrayList<Projectile>(); // CopyOnWriteArrayList is a thread-safe variant of ArrayList
        _dc = null;
    }

    public Simulator(DisplayClient dc) {
        _vehicleList = new CopyOnWriteArrayList<GroundVehicle>();
        _projectileList = new CopyOnWriteArrayList<Projectile>(); // CopyOnWriteArrayList is a thread-safe variant of ArrayList
        _dc = dc;
        _followerList = new ArrayList<FollowingController>();
        _leaderList = new ArrayList<LeadingController>();

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

    /**
     * Associates a UserController with the Simulator
     * //TODO: req modified for multiplayer
     * @param uc
     */
    public synchronized void addUserController(UserController uc) {
        if (_uc1 == null) {
            _uc1 = uc;
        } else if (_uc2 == null) {
            _uc2 = uc;
        } else {
            throw new IllegalStateException("Cannot add third UserController to simulation");
        }
    }
    
    /**
     * Adds a FollowingController to the list inside Simulator
     * @param fc
     */
    public synchronized void addFollowingController(FollowingController fc){
    	
    	_followerList.add(fc);
    }
    
    /**
     * Adds a LeadingController to the list inside Simulator
     * @param lc
     */
    public synchronized void addLeadingController(LeadingController lc){
    	_leaderList.add(lc);
    }


    /**
     * Generates a projectile based on the position of the UserController associated with Simulator
     *TODO: make this accommodate multiple userControllers
     * TODO: req changed
     */
    public void generateProjectile(UserController uc) {
        Projectile p = new Projectile(uc.getUserVehicle().getPosition(), this, uc);
        _projectileList.add(p);
        p.start();

        if (debug_projectiles) {
            System.out.println("Projectile "+_projectileList.size()+" generated!");
        }
    }


    public synchronized double getCurrentMSec() {
        return ((System.nanoTime()/1e6) - _startupMS);
    }

    public synchronized double getStartupMSec() {
        return _startupMS;
    }

    /**
     * Switches GroundVehicle from a Leading to a FollowingController
     * @param oldController
     */
    public synchronized void switchVehicleControllers(VehicleController oldController) {
    	//TODO: Requirements
        //  check if OC actually has a vehicle
        if (oldController.getGroundVehicle() == null) {
            throw new IllegalArgumentException("Old VehicleController has no GroundVehicle!");
        }

        // get vehicle from OC
        GroundVehicle v = oldController.getGroundVehicle();
        
        // remove vehicle from OC
        oldController.removeGroundVehicle();
        
        // Create new following controller
        FollowingController newController = new FollowingController(this,v, _uc1.getGroundVehicle());
        newController.start();

        // add followingController to list in Simulator
        this.addFollowingController(newController);


    }


    /**
     * Removes Projectiles that went offscreen from Projectile list.
     */
    public void removeOffscreenProjectiles() {

        for (Projectile p : _projectileList) {
            double[] position = p.getPosition();    // get [x, y, theta] of projectile
            double x = position[0];
            double y = position[1];

            if (projectileOffScreen(position)) {    // check if projectile is offscreen
                _projectileList.remove(p);          // remove offscreen projectile
            }
        }

    }


    /**
     * //TODO: NEW REQUIREMENTS
     * Checks if projectile shot a vehicle.
     * @param projectilePos projectile [x, y, theta]
     * @param vPos vehicle [x, y, theta]
     */
    public boolean projectileShotVehicle(double[] projectilePos, double[] vPos) {
        if (projectilePos.length != 3) {
            throw new IllegalArgumentException("projectilePos must be of length 3");
        }
        if (vPos.length != 3) {
            throw new IllegalArgumentException("vPos must be of length 3");
        }

        boolean wasShot = false;

        if (checkWithinDistance(projectilePos, vPos, Projectile.HIT_DISTANCE)) {
            wasShot = true;
        }

        return wasShot;
    }


    /**
     * Removes Projectiles that went offscreen from Projectile list.
     */
    public void changeShotVehicles() {
        for (GroundVehicle v : _vehicleList) {
            for (Projectile p : _projectileList) {
                if (projectileShotVehicle(p.getPosition(), v.getPosition())) {
                    System.out.println("VEHICLE SHOT!");
                    if (v.color == FOLLOWING) {
                        _vehicleList.remove(v);
                        System.out.println("VEHICLE SHOT AGAIN! GAME OVER, BUDDY!");

//                        _projectileList.remove(p);
                    }
                    if (v.color == LEADING) {
                        switchVehicleControllers(v.controller);
                        System.out.println("Switched controllers!");
//                        _projectileList.remove(p);
                    }
                }
            }

        }

    }


    boolean projectileOffScreen(double[] projectilePos) {
        if (projectilePos.length != 3) {
            throw new IllegalArgumentException("obj1pos must be of length 3");
        }
        boolean isOffScreen = false;

        double x = projectilePos[0];
        double y = projectilePos[1];

        // check projectiles x-limits
        if (x > SIM_X || x < 0) {
            isOffScreen = true;
        }
        // check projectiles y-limits
        if (y > SIM_Y || y < 0) {
            isOffScreen = true;
        }
        return isOffScreen;

    }


    
    /**
     * Checks if two GroundVehicle/Projectile objects are within a certain distance of each other
     * @param obj1pos object 1 [x, y, theta]
     * @param obj2pos object 2 [x, y, theta]
     * @param thresholdDistance threshold distance for comparison
     * @return true if distance between objects is at or below the threshold distance
     */
    public boolean checkWithinDistance(double[] obj1pos, double[] obj2pos, double thresholdDistance) {
    	if (obj1pos.length != 3) {
            throw new IllegalArgumentException("obj1pos must be of length 3");
        }
    	if (obj2pos.length != 3) {
            throw new IllegalArgumentException("obj2pos must be of length 3");
        }
    	if (thresholdDistance <= 0) {
            throw new IllegalArgumentException("Threshold must be greater than 0");
        }
    	
    	boolean isWithinDistance = false;
    	
    	double xDiff = obj1pos[0] - obj2pos[0];
    	double yDiff = obj1pos[1] - obj2pos[1];
    	
    	// calculate distance  sqrt(x^2+x^2)
    	double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    	
    	// return true if distance < thresholdDistance
    	if (distance < thresholdDistance){
    		isWithinDistance = true;
    	}
    	
    	return isWithinDistance;
    }
    
    /**
     * Calculates linear distance between two positions
     * @param obj1pos object 1 [x, y, theta]
     * @param obj2pos object 2 [x, y, theta]
     * @return linear distance between two object positions
     */
    public static double distance(double[] obj1pos, double[] obj2pos) {
        if (obj1pos.length != 3) {
            throw new IllegalArgumentException("obj1pos must be of length 3");
        }
        if (obj2pos.length != 3) {
            throw new IllegalArgumentException("obj2pos must be of length 3");
        }

        double xDiff = obj1pos[0] - obj2pos[0];
    	double yDiff = obj1pos[1] - obj2pos[1];
    	
    	// calculate distance  sqrt(x^2+x^2)
    	return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
    }
    
    /* RUN METHOD */
    public void run() {

        // clear display of previous trajectories
        _dc.clear();
        _dc.traceOn();  // display mode showing complete trajectories,
        // not just current positions

        // vehicle arrays to send to display
        double[] gvX;
        double[] gvY;
        double[] gvTheta;
        double[] gvC; // color array

        // projectile arrays to send to display
        double[] pX;
        double[] pY;
        double[] pC; // color array

        _startupTime = System.nanoTime();
        long currentTime = System.nanoTime();
        long updateTime = System.nanoTime();

        while ((currentTime - _startupTime) < 200*1e9) { // while time less than 100s

            currentTime = System.nanoTime();

            if ((currentTime - updateTime) >= SIM_MS_INCREMENT*1e6) { // update once every 100ms

                synchronized (this) {

                    gvX = new double[_vehicleList.size()];
                    gvY = new double[_vehicleList.size()];
                    gvTheta = new double[_vehicleList.size()];
                    gvC = new double[_vehicleList.size()]; // color array

                    for (int i = 0; i < _vehicleList.size(); i++) {     // iterate over list of i vehicles
                        GroundVehicle vehicle = _vehicleList.get(i);    // get vehicle at index i
                        double[] displayData = vehicle.getDisplayData();      // get [x, y, theta] of vehicle
                        gvX[i] = displayData[0];
                        gvY[i] = displayData[1];
                        gvTheta[i] = displayData[2];
                        gvC[i] = displayData[3];


                        if (debug) {
                            System.out.println(vehicle.getNumID() + "[>  ] position on display is being updated");
                            //                        System.out.println(vehicle._ID + " " + vehicle.getNumID() + "[>  ] position on display is being updated");
                        }
                    } // end for (int i = 0; i < _vehicleList.size(); i++)

                    notifyAll();
                } // end synchronized (this)

                /**/
                synchronized (this) {

                    pX = new double[_projectileList.size()];
                    pY = new double[_projectileList.size()];
                    pC = new double[_projectileList.size()];

                    for (int i = 0; i < _projectileList.size(); i++) {     // iterate over list of i projectiles
                        Projectile p = _projectileList.get(i);    // get projectile at index i
                        double[] displayData = p.getDisplayData();      // get [x, y, theta] of vehicle
                        pX[i] = displayData[0];
                        pY[i] = displayData[1];
                        pC[i] = displayData[2];

                        if (debug_projectiles) {
                            System.out.println("px " + displayData[0] + " py " + displayData[1]);
                        }
                    } // end for (int i = 0; i < __projectileList.size(); i++)

                    removeOffscreenProjectiles();


                    notifyAll();
                } // end synchronized (this)


                updateTime = System.nanoTime();

                // update display client with vehicle positions
                // update display client with projectile positions
                _dc.update(_vehicleList.size(), gvX, gvY, gvTheta, gvC, _projectileList.size(), pX, pY, pC);

                // Check if projectile is near GroundVehicle and switch controller if so
                synchronized (this) {
                    changeShotVehicles();
                    notifyAll();

                }	// end synchronized (this)



            } // end if (100ms since last update)
        } // end while (time < 100s)

        _dc.traceOff();
        System.out.println("SHOTS FIRED: " + Projectile.SHOTS_FIRED);

        // kill application after time is over
        System.exit(0);


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
