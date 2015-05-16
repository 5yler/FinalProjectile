/**
 * FollowingController
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 **/

public class FollowingController extends VehicleController {

    public static final double FOLLOWING_MAX_VEL = 0.5*GroundVehicle.MAX_VEL;
    public static final double FOLLOWING_MIN_VEL = 5*GroundVehicle.MIN_VEL;

    public static final double FOLLOWING_DISTANCE = 0.5; // threshold distance for following

    private GroundVehicle _prey;

    /**
     * @param sim simulator object
     * @param v specific GroundVehicle being controlled
     * @param prey GroundVehicle which v follows
     */
    public FollowingController(Simulator sim, GroundVehicle v, GroundVehicle prey) {
        super(sim, v);
        if (prey == null) {
            throw new IllegalArgumentException("Prey GroundVehicle must not be null");
        }
        _prey = prey;
        _v._color = Simulator.FOLLOWING_COLOR;
    }
    
    public GroundVehicle getPrey(){
    	return _prey;
    }

    /**
     * The getControl method takes as an argument a time value in seconds
     * and milliseconds, and returns a control if one should be issued at
     * this time. Provides the functionality that causes the vehicle to
     * follow a specific trajectory.
     */
    public Control getControl(int sec, int msec) {

        double s, omega;
        double[] vPose;// = {0, 0, 0};

        double[] preyVel;// = {0, 0, 0};
        double[] preyPose;// = {0, 0, 0};
        double preySpeed;// = 0;

            // get ground vehicle state
        preyPose = _prey.getPosition();
        preyVel = _prey.getVelocity();
        preySpeed = Math.sqrt(Math.pow(preyVel[0], 2) + Math.pow(preyVel[1], 2));

        // position of follower vehicle
        vPose = _v.getPosition();

        // define difference in x, y, and theta between vehicles
        double DX = preyPose[0] - vPose[0];
        double DY = preyPose[1] - vPose[1];

        // linear distance between vehicles
        double preyDistance = Simulator.distance(preyPose,vPose); // d = (x^2 + y^2)^(1/2)

        // define next angle between vehicles
        double nextPhi = Math.atan2(DY,DX);

        // if vehicles are in the same place
         if (DX == 0 && DY == 0) {
             // slow down and stop turning
             s = FOLLOWING_MIN_VEL;
             omega = 0;
         } else {
             // make rotational vel of control proportional to future angle between vehicles
             omega = normalizeAngle(nextPhi - vPose[2]);

             if (preyDistance > FOLLOWING_DISTANCE) {
                 s = FOLLOWING_MAX_VEL;
             } else {
                 s = preySpeed;
             }
         }

        // clamp velocities
        Control nextControl = clampControl(s,omega);

        return nextControl;
    }


}





