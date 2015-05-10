

public class UserController extends VehicleController {

    private DisplayServer _ds;
//    private ControlPanel cp;

    private double minTransSpeed = GroundVehicle.MIN_VEL;
    private double maxTransSpeed = GroundVehicle.MAX_VEL;
    private double maxRotSpeed = Math.PI / 4;



    private final boolean debug = false; // set to true for debug statements

    public UserController(Simulator sim, GroundVehicle v, DisplayServer ds){
	super(sim, v);
        _ds = ds;
        _sim = sim;

    }

//    public void addDisplayServer(DisplayServer ds) {
//        _ds = ds;
//    }

    public GroundVehicle getUserVehicle() {
        return _v;
    }

    /**
     * Clamps speed and omega to allowable ranges and returns control with
     * bounded values of s and omega.
     * @param s forward speed
     * @param omega angular velocity
     * @return control with clamped linear and angular velocity values
     */
    private Control clampControl(double s, double omega) {

        double clampedSpeed;
        double clampedOmega;

        // clamp speed if it is above 10 or below 5
        if (s > maxTransSpeed)
            clampedSpeed = maxTransSpeed;
        else if (s < minTransSpeed)
            clampedSpeed = minTransSpeed;
        else
            clampedSpeed = s;

        // clamp angular velocity if it is above the allowed range
        clampedOmega = Math.min(Math.max(omega, -Math.PI/4), Math.PI/4);

        // create a control with the clamped s and omega values
        Control clampedControl = new Control(clampedSpeed, clampedOmega);
        return clampedControl;
    }


    public Control getControl(int sec, int msec) {
        double _nextSpeed = _ds.getUserSpeed();
        double _nextOmega = _ds.getUserOmega();
        if (debug) {
            System.out.println("s: "+_nextSpeed+" omega: "+_nextOmega);
        }
        return clampControl(_nextSpeed, _nextOmega);
    }











}



/*
public class UserController extends VehicleController {

    private DisplayServer _ds;
//    private ControlPanel cp;

    private double minTransSpeed = GroundVehicle.MIN_VEL;
    private double maxTransSpeed = GroundVehicle.MAX_VEL;
//
//    private double _startSpeed = 7.5;
//    private double _startOmega = 0;

    private double _nextSpeed = 7.5;
    private double _nextOmega = 0;

    private double SPEED_INCREMENT = 0.1;
    private double MAX_USER_OMEGA = Math.PI/3;

    private final boolean debug = false; // set to true for debug statements

    public UserController(Simulator sim, GroundVehicle v, DisplayServer ds){
        super(sim, v);
        _ds = ds;
        _sim = sim;

    }

//    public void addDisplayServer(DisplayServer ds) {
//        _ds = ds;
//    }

    public GroundVehicle getUserVehicle() {
        return _v;
    }


    private Control clampControl(double s, double omega) {

        double clampedSpeed;
        double clampedOmega;

        // clamp speed if it is above 10 or below 5
        if (s > maxTransSpeed)
            clampedSpeed = maxTransSpeed;
        else if (s < minTransSpeed)
            clampedSpeed = minTransSpeed;
        else
            clampedSpeed = s;

        // clamp angular velocity if it is above the allowed range
        clampedOmega = Math.min(Math.max(omega, -MAX_USER_OMEGA), MAX_USER_OMEGA);

        // create a control with the clamped s and omega values
        Control clampedControl = new Control(clampedSpeed, clampedOmega);
        return clampedControl;
    }


    public Control getControl(int sec, int msec) {

        return clampControl(_nextSpeed, _nextOmega);

    }




    public void increaseSpeed() {
        _nextSpeed += SPEED_INCREMENT;
    }


    public void decreaseSpeed() {
        _nextSpeed -= SPEED_INCREMENT;
    }

    public void turnLeft() {
        _nextOmega = MAX_USER_OMEGA;
    }

    public void turnRight() {
        _nextOmega = -MAX_USER_OMEGA;
    }

    public void stopTurning() {
        _nextOmega = 0;
    }










}

*/