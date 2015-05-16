/**
 * Simulator
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 **/

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Simulator extends Thread {

    // set maximum simulation size based on DisplayServer window dimensions
    public static final int SIM_X = DisplayServer.DISPLAY_X/5;
    public static final int SIM_Y = DisplayServer.DISPLAY_Y/5;

    public static final int UPDATE_MS = FinalProjectile.SIMULATOR_MS;

    // colors // TODO:req
    public static final int USER1_COLOR = 1;
    public static final int USER2_COLOR = 2;
    public static final int LEADING_COLOR = 3;
    public static final int FOLLOWING_COLOR = 4;




    public long STARTUP_TIME;  // time when the simulator starts running
    private long[] _lastProjectileTime = new long[2]; // TODO:req// time when last projectile was fired for each usercontroller

    private static DisplayClient _dc;







    public List<GroundVehicle> _vehicleList;  // list of GroundVehicles inside Simulator
    public List<Projectile> _projectileList;  // list of projectiles inside Simulator

    // user controllers //TODO: req
    private UserController _uc1;
    private UserController _uc2;



/* SETTINGS */

    private final boolean print = false;         // set to true for print statements
    private final boolean debug = false;         // set to true for debug statements

    private static final boolean lead   = false;    // set to true for LeadingController
    private static final boolean lead3  = false;    // set to true for multiple LeadingController test
    private static final boolean circ   = true;     // one CircleController

    private static final boolean debug_projectiles   = FinalProjectile.debug_projectiles;     // projectile debug statements



    public static NumberFormat scoreFormat = new DecimalFormat("###.#");


    public Simulator(DisplayClient dc) {
        //TODO: req copyonwrite arrays

        _vehicleList = new CopyOnWriteArrayList<GroundVehicle>();
        _projectileList = new CopyOnWriteArrayList<Projectile>(); // CopyOnWriteArrayList is a thread-safe variant of ArrayList
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



    public DisplayClient getDisplayClient() {
        return _dc;
    }
    
    public UserController getUserController(int index) {
    	if (index == 1){
    		return uc1;
    	}
    	else if (index == 2){
    		return uc2;
    	}
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
    }

    /**
     * Associates a UserController with the Simulator
     * //TODO: req modified for MULTIPLAYER
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
     * Generates a projectile based on the position of the UserController associated with Simulator
     *TODO: make this accommodate multiple userControllers
     * TODO: req changed
     */
    public void generateProjectile(UserController uc) {

        long timeSinceLastProjectile = (System.nanoTime() - _lastProjectileTime[uc._userID]) / 1000000; // [ms]

        // check if REACTION_TIME has passed since last projectile being fired by user //TODO: req
        if (timeSinceLastProjectile > UserController.REACTION_TIME) {
            if (debug_projectiles) {
                System.out.println(timeSinceLastProjectile + "ms since last projectile fired by user " + (uc._userID + 1));
            }

            Projectile p = new Projectile(uc.getUserVehicle().getPosition(), this, uc);
            _projectileList.add(p);
            p.start();

            // increase counter for shots fired
            uc._shots++;

            // reset last projectile time
            _lastProjectileTime[uc._userID] = System.nanoTime();

            if (debug_projectiles) {
                System.out.println("Projectile " + _projectileList.size() + " generated!");
            }
        }
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
                    if (v._color == FOLLOWING_COLOR) {

                        // remove follower if shot
                        _vehicleList.remove(v);

                        // increment user hit counter
                        p._uc._hits++;

                        // increment kill counters
                        p._uc._kills++;
                        UserController.TOTAL_KILLS++;

                        System.out.println("VEHICLE SHOT AGAIN! GAME OVER, BUDDY!");

//                        _projectileList.remove(p);
                    }
                    if (v._color == LEADING_COLOR) {

                        // switch leadingcontroller to followingcontroller
                        switchVehicleControllers(v._vc);

                        // increment hit counter
                        p._uc._hits++;

                        System.out.println("Switched controllers!");
                        System.out.println("VEHICLE SHOT!");

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

//TODO: req
        int[] userShots = {0, 0};
        int[] userHits  = {0, 0};
        int[] userKills = {0, 0};

        // set startup time
        STARTUP_TIME = System.nanoTime();

        // initialize current time, last update time, and last projectile time
        _lastProjectileTime[0] = STARTUP_TIME;
        _lastProjectileTime[1] = STARTUP_TIME;
        long currentTime = System.nanoTime();
        long updateTime = System.nanoTime();

        playing:
        while ((currentTime - STARTUP_TIME) < FinalProjectile.GAME_TIME*1e9) { // while time less than game time

            currentTime = System.nanoTime();

            if ((currentTime - updateTime) >= UPDATE_MS *1e6) { // update once every increment

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


                userShots[0]    = _uc1._shots;
                userHits[0]     = _uc1._hits;
                userKills[0]    = _uc1._kills;

                if (FinalProjectile.MULTIPLAYER) {
                    userShots[1]    = _uc2._shots;
                    userHits[1]     = _uc2._hits;
                    userKills[1]    = _uc2._kills;
                }
              
                // update display client with vehicle positions
                // update display client with projectile positions
                if (FinalProjectile.debug_scores) {
                    System.out.println(userShots[0] + " " + userShots[1] + " " + userHits[0] + " " + userHits[1] + " Simulator.run()");
                }
                _dc.update(userShots, userHits, userKills, gvC.length, gvX, gvY, gvTheta, gvC, pC.length, pX, pY, pC);

                // Check if projectile is near GroundVehicle and switch controller if so
                synchronized (this) {
                    changeShotVehicles();
                    notifyAll();

                }	// end synchronized (this)

                if (UserController.TOTAL_KILLS == FinalProjectile.NUM_VEHICLES) {

                    // update score counts
                    synchronized (this) {
                        userShots[0] = _uc1._shots;
                        userHits[0] = _uc1._hits;
                        userKills[0] = _uc1._kills;

                        if (FinalProjectile.MULTIPLAYER) {
                            userShots[1] = _uc2._shots;
                            userHits[1] = _uc2._hits;
                            userKills[1] = _uc2._kills;
                        }
                        _dc.update(userShots, userHits, userKills, gvC.length, gvX, gvY, gvTheta, gvC, pC.length, pX, pY, pC);
                        changeShotVehicles();
                    }
                    break playing;
                }


            } // end if (UPDATE_MS since last update)
        } // end while (time < FinalProjectile.GAME_TIME)


        // send final scores
        _dc.over(userShots, userHits, userKills);


        System.out.println("SHOTS FIRED: " + Projectile.SHOTS_FIRED);

        System.out.println("USER 1 -------------------------------------");
        System.out.println("Shots: "+_uc1._shots);
        System.out.println("Hits: "+_uc1._hits);
        System.out.println("Kills: " +_uc1._kills);
        System.out.println("Accuracy: " + accuracy(_uc1._hits,_uc1._shots));

        if (FinalProjectile.MULTIPLAYER) {

            System.out.println("USER 2 -------------------------------------");
            System.out.println("Shots: " + _uc2._shots);
            System.out.println("Hits: " + _uc2._hits);
            System.out.println("Kills: " + _uc2._kills);
            System.out.println("Accuracy: " + accuracy(_uc2._hits,_uc2._shots));

        }


        try {
            Thread.sleep(FinalProjectile.GAME_OVER_TIMEOUT*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // kill application after time is over
        System.exit(0);


    } // end run()

    public static String accuracy(int hits, int shots) {
        if (shots == 0) {
            return "NaN%";
        } else {
            double acc1 = 100.0 * hits / shots;
            return scoreFormat.format(acc1)+"%";
        }
    }

}
