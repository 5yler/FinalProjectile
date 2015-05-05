/**
 * VehicleController
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/


public class VehicleController extends Thread {

    protected Simulator _sim;         // simulator object
    protected GroundVehicle _v;     // specific GroundVehicle being controlled

    private long _startupTime;  // time when the VehicleController starts running
    public static final int VC_MS_INCREMENT = 100; // should be 100 for assignment 4

    public final double _dt;    // timestep increment for advancing GroundVehicles

    private int _numSides = 5;

    // Hard-coded constraints come from documentation. Min translation
    // speed of the vehicle is 5 m/_s, max translation speed is 10 m/_s,
    // max rotational speed is PI/4. Radius of outer circle is 50

    private double minTransSpeed = GroundVehicle.MIN_VEL;
    private double maxTransSpeed = GroundVehicle.MAX_VEL;
    private double maxRotSpeed = Math.PI / 4;

    private double circumCircleRadius = 25.0;

    private boolean isTurning = false;
    private boolean controllerInitialized = false;

    private double turnDuration;
    private double edgeTravelDuration;

    private double timeOfManoeuverStart;

    protected final int _ID; 	// unique numeric identifier for ordering all VehicleControllers
    protected static int controllerCount = 0;	// number of VehicleControllers in existence

    private final boolean print = false;   // set to true for print statements
    private final boolean debug = false;   // set to true for debug statements

/* CONSTRUCTOR */
    public VehicleController(Simulator sim, GroundVehicle v) {
        _sim = sim;
        _v = v;

//        _dt = _v.VEHICLE_SEC_INCREMENT+_v.VEHICLE_MSEC_INCREMENT/1e3;
        _dt = VC_MS_INCREMENT;

        _ID = controllerCount;
        controllerCount++;
    }

/* GET METHODS */
    public int getNumSides() {
        return _numSides;
    }

    /**
     * The getControl method takes as an argument a time value in seconds
     * and milliseconds, and returns a control if one should be issued at
     * this time. If no control should be issued, getControl returns null.
     *
     * @param sec  time in seconds
     * @param msec time in milliseconds
     * @return control that should be issued at time given, if any
     */
    public Control getControl(int sec, int msec) {

        double controlTime = sec + msec * 1E-3;

        Control nextControl = null;

        if (!controllerInitialized) initializeController();
        if (isTurning) {
            if (controlTime - timeOfManoeuverStart < turnDuration)
                nextControl = new Control(minTransSpeed, maxRotSpeed);
            else {
                isTurning = false;
                timeOfManoeuverStart = controlTime;
                nextControl = new Control(maxTransSpeed, 0);
            }
        } else {
            if (controlTime - timeOfManoeuverStart < edgeTravelDuration)
                nextControl = new Control(maxTransSpeed, 0);
            else {
                isTurning = true;
                timeOfManoeuverStart = controlTime;
                nextControl = new Control(minTransSpeed, maxRotSpeed);
            }
        }
        return nextControl;
    }

    /**
     *
     * @return GroundVehicle associated with the VehicleController
     */
    public GroundVehicle getGroundVehicle() {
        return _v;
    }

    /**
     *
     * @return Simulator associated with the VehicleController
     */
    public Simulator getSimulator() {
        return _sim;
    }


    /**
     *
     * @return number of VehicleControllers in existence
     */
    public static int getControllerCount() {
        return controllerCount;
    }

    /**
     *
     * @return  VehicleController's unique numeric ID used for ordering
     */
    public int getID() {
        return _ID;
    }

/* SET METHODS */
    public int setNumSides(int n) {
        // Simulator as well?
        if (n >= 3 && n <= 10) {
            _numSides = n;
        }
        return _numSides;
    }

/* OTHER METHODS */
    /**
     *
     * The bulk of this method is to determine how long to spend turning at
     * each corner of the polygon, and how long to spend driving along each
     * edge. We calculate turnDuration and edgeTravelDuration, and then use
     * these inside getControl to decide when to switch from turning to
     * travelling straight and back again.

     */
    private void initializeController() {

		/*
		 * Firstly, we know we need to turn the vehicle by PI - the internal
		 * angle of the polygon
		 */

        double interiorAngle = Math.PI * (_numSides - 2) / _numSides;
        double turningAngle = Math.PI - interiorAngle;

		/*
		 * And we're going to turn the vehicle along the circumference of the
		 * smallest circle we can make.
		 */

        double minTurningRadius = minTransSpeed / maxRotSpeed;

		/*
		 * The distance we have to travel along that smallest circle is a
		 * function of the angle and the radius, and is an arc along that small
		 * circle.
		 */
        double arcLength = turningAngle * minTurningRadius;

		/*
		 * We can work out how long each turn will take based on the arcLength
		 * and how fast we are travelling. Of course, we could also work it out
		 * based on the angle and our maximum angular velocity.
		 */

        turnDuration = arcLength / minTransSpeed;

        // Edge length of n-polygon
        double polyEdge = 2 * circumCircleRadius * Math.cos(interiorAngle / 2);
        // Subtract by chord length spent for turns
        double edgeLength = polyEdge - 2
                * (minTurningRadius * Math.tan(turningAngle / 2));

		/*
		 * And we now have the amount of time to spend travelling straight along
		 * each edge.
		 */
        edgeTravelDuration = edgeLength / maxTransSpeed;

		/*
		 * Also in method, we initialize the controller state. It'_sim a little
		 * ugly, but we'll start as if we're half-way through a turn, and
		 * tangent to the outer circle. This makes it easy to put the vehicle on
		 * a legal part of the polygon, rather than having to drive to it.
		 */

        isTurning = true;
        timeOfManoeuverStart = -turnDuration / 2.0;

        controllerInitialized = true;
    }


    /**
     *
     * @param theta angle
     * @return angle wrapped normalized to interval [-PI, PI]
     */
    public static double normalizeAngle(double theta) {
        while (theta < -Math.PI) {
            theta += 2 * Math.PI;
        }
        while (theta > Math.PI) {
            theta -= 2 * Math.PI;
        }
        return theta;
    }

/* RUN METHOD */
    /**
     * The run method should be able to get the state of the
     * GroundVehicle and therefore be able to generate controls based on
     * the GroundVehicle position. What the VehicleController does not
     * do is advance the time; we want to have multiple vehicles running
     * in parallel, and so we need some centralized mechanism of
     * advancing all the GroundVehicle states at once. That is what the
     * simulator will do.
     */
    public void run() {

        _startupTime = System.nanoTime();
        long currentTime = System.nanoTime();
        long updateTime = System.nanoTime();

        while ((currentTime - _startupTime) < 100*1e9) { // while time less than 100s

            currentTime = System.nanoTime();

            if ((currentTime - updateTime) >= VC_MS_INCREMENT *1e6) { // update once every 100ms

                long controlTime = currentTime - _startupTime;
                int controlSec = (int) (controlTime/1e9);
                int controlMSec = (int) ((controlTime-controlSec*1e9)/1e6);

                // get next control
                Control c = getControl(controlSec, controlMSec);
                // apply control to GroundVehicle
                _v.controlVehicle(c);
//
                // reset last update time
                updateTime = System.nanoTime();

            } // end if (100ms since last update)
        } // end while (time < 100s)
    } // end run()


//
//    //TODO: complete
//    public static void main(String args[]) {
//        SchedulingParameters scheduling = new PriorityParameters(PriorityScheduler.MIN_PRIORITY+10);
//        AsyncEventHandler missHandler = new DeadlineMissHandler();
//        ReleaseParameters release = new PeriodicParameters(
//                new RelativeTime(),         // release at .start()
//                new RelativeTime(100, 0),   // period 100ms
//                new RelativeTime(100, 0),   // cost
//                new RelativeTime(100, 0),   // deadline 100ms
//                null,                       // overrun handler
//                missHandler);               // deadline miss handler
////
//        RealtimeThread rt = new VehicleController(scheduling, release);
//    }
}
