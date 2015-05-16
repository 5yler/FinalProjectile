/**
 * UserController
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

public class UserController extends VehicleController {


    public static final int REACTION_TIME = 500; // [ms] timeout between successive projectiles firing //TODO: req

    private DisplayServer _ds;

    // score counters
    public int _shots = 0;
    public int _hits = 0;
    public int _kills = 0;
    public static int TOTAL_KILLS = 0;	// total number of kills by all users

    protected static int userControllerCount = 0;	// number of VehicleControllers in existence
    public final int _userID;    // unique UserController ID

    private final boolean debug = FinalProjectile.debug_user; // set to true for debug statements

    /**
     * Constructor
     * @param sim associated Simulator object
     * @param v associated GroundVehicle object
     * @param ds associated DisplayServer which handles keyboard inputs
     */
    public UserController(Simulator sim, GroundVehicle v, DisplayServer ds){
	super(sim, v);
        if (ds == null) {
            throw new IllegalArgumentException("DisplayServer must not be null");
        }

        _ds = ds;

        _userID = userControllerCount;
        userControllerCount++;
        _v._color = userControllerCount;
    }

    /**
     * Constructor used for testing functionality without display
     * @param sim associated Simulator object
     * @param v associated GroundVehicle object
     */
    public UserController(Simulator sim, GroundVehicle v){
        super(sim, v);

        _ds = null;

        _userID = userControllerCount;
        userControllerCount++;
        _v._color = userControllerCount;
    }



    public GroundVehicle getUserVehicle() {
        return _v;
    }
    

    public Control getControl(int sec, int msec) {

        // get next speed and omega values from display server
        double nextSpeed = _ds.getUserSpeed(_userID);
        double nextOmega = _ds.getUserOmega(_userID);
        boolean isShooting = _ds.getProjectileGenerated(_userID);

        if (debug) {
            System.out.println("UserController "+_userID+" s: "+nextSpeed+" omega: "+nextOmega);
        }

        // if user is shooting, generate projectile in Simulator
        if (isShooting) {
            _sim.generateProjectile(this);
        }

        // generate new control
        Control nextControl = new Control(nextSpeed, nextOmega);

        return nextControl;
    }
}
