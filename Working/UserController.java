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

    private final boolean debug = false; // set to true for debug statements

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



    public GroundVehicle getUserVehicle() {
        return _v;
    }

    //TODO: req modified for MULTIPLAYER
    public Control getControl(int sec, int msec) {
        double _nextSpeed = _ds.getUserSpeed(_userID);
        double _nextOmega = _ds.getUserOmega(_userID);
        boolean isShooting = _ds.getProjectileGenerated(_userID);
        if (isShooting) {
            _sim.generateProjectile(this);
        }

        if (debug) {
            System.out.println("s: "+_nextSpeed+" omega: "+_nextOmega);
        }
        return clampControl(_nextSpeed, _nextOmega);
    }
}
