/**
 * VehicleController
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 **/


public class VehicleController extends Thread {

    protected Simulator _sim;         // simulator object
    protected GroundVehicle _v;     // specific GroundVehicle being controlled

    public static final int UPDATE_MS = FinalProjectile.CONTROLLER_MS;

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
        if (sim == null) {
            throw new IllegalArgumentException("Simulator must not be null");
        }
        if (v == null) {
            throw new IllegalArgumentException("GroundVehicle must not be null");
        }

        _sim = sim;
        _v = v;
        _v._vc = this;

//        _dt = _v.VEHICLE_SEC_INCREMENT+_v.VEHICLE_MSEC_INCREMENT/1e3;
        _dt = UPDATE_MS;

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


    public void setGroundVehicle(GroundVehicle v) {
        _v = v;
    }


    public void removeGroundVehicle() {
        _v = null;
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
     * Clamps speed and omega to allowable ranges and returns control with
     * bounded values of s and omega.
     * @param s forward speed
     * @param omega angular velocity
     * @return control with clamped linear and angular velocity values
     */
    public Control clampControl(double s, double omega) {

        double clampedSpeed;
        double clampedOmega;

        // clamp speed if it is above 10 or below 5
        if (s > GroundVehicle.MAX_VEL) {
            clampedSpeed = GroundVehicle.MAX_VEL;
        } else if (s < GroundVehicle.MIN_VEL){
            clampedSpeed = GroundVehicle.MIN_VEL;
        } else {
            clampedSpeed = s;
        }

        // clamp angular velocity if it is above the allowed range
        clampedOmega = Math.min(Math.max(omega, -GroundVehicle.MAX_OMEGA), GroundVehicle.MAX_OMEGA);

        // create a control with the clamped s and omega values
        Control clampedControl = new Control(clampedSpeed, clampedOmega);

        return clampedControl;
    }

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


    /**
     * Calculates angle from an X and Y using arctan function
     * @param X
     * @param Y
     * @return angle in radians
     */
    public static double getAngle(double X, double Y) {
        double nextAngle = Math.atan(Y/X);
        if (X < 0)
            nextAngle += Math.PI;
        return nextAngle;
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

        long startupTime = System.nanoTime();
        long currentTime = System.nanoTime();
        long updateTime = System.nanoTime();

        while ((currentTime - startupTime) < FinalProjectile.GAME_TIME*1e9) { // while time less than game time

            currentTime = System.nanoTime();

            if ((currentTime - updateTime) >= UPDATE_MS *1e6) { // update once every increment

                long controlTime = currentTime - startupTime;
                int controlSec = (int) (controlTime/1e9);
                int controlMSec = (int) ((controlTime-controlSec*1e9)/1e6);

                if (_v != null){
                    // get next control
                    Control c = getControl(controlSec, controlMSec);
                    // apply control to GroundVehicle
                    _v.controlVehicle(c);
                }
//
                // reset last update time
                updateTime = System.nanoTime();

            } // end if (UPDATE_MS since last update)
        } // end while (time < FinalProjectile.GAME_TIME)
    } // end run()


}
