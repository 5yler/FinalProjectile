/**
 * FollowingController
 * 16.35 Assignment #3 Final Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.text.DecimalFormat;

public class FollowingController extends VehicleController {

    private GroundVehicle _prey;
    private int _lastCheckedSec;    // last checked time (sec)
    private int _lastCheckedMSec;   // last checked time (msec)


//    private final double dt; // timestep increment for controls [sec]

    private double maxTransSpeed = 10;
    private double minTransSpeed = 5;
    private double targetFollowingDistance = 0.5;

    private final boolean print = true;   // set to true for print statements
    private final boolean debug = true;   // set to true for debug statements

    private final DecimalFormat df = new DecimalFormat("+#.0;-0");

    /**
     * @param sim simulator object
     * @param v specific GroundVehicle being controlled
     * @param prey GroundVehicle which v follows
     */
    public FollowingController(Simulator sim, GroundVehicle v, GroundVehicle prey) {
        super(sim, v);
        _prey = prey;
        //TODO: req
        _v.color = Simulator.FOLLOWING;
    }

    /**
     * The getControl method takes as an argument a time value in seconds
     * and milliseconds, and returns a control if one should be issued at
     * this time. Provides the functionality that causes the vehicle to
     * follow a specific trajectory.
     */
    public Control getControl(int sec, int msec) {

        double s, omega;
        double[] vVel;// = {0, 0, 0};
        double[] vPose;// = {0, 0, 0};
        double vSpeed;// = 0;

        double[] preyVel;// = {0, 0, 0};
        double[] preyPose;// = {0, 0, 0};
        double preySpeed;// = 0;

            // get ground vehicle state
        preyPose = _prey.getPosition();
        preyVel = _prey.getVelocity();
        preySpeed = Math.sqrt(Math.pow(preyVel[0], 2) + Math.pow(preyVel[1], 2));

        // position of follower vehicle
        vPose = _v.getPosition();
        vVel = _v.getVelocity();

        // define difference in x, y, and theta between vehicles
        double DX = preyPose[0] - vPose[0];
        double DY = preyPose[1] - vPose[1];

        // define next difference in x, y
        double X = DX+(preyVel[0] - vVel[0])*_dt;
        double Y = DY+(preyVel[1] - vVel[1])*_dt;

        // linear distance between vehicles
      //  double preyDistance = Math.sqrt(Math.pow(DX,2) + Math.pow(DY,2)); // d = (x^2 + y^2)^(1/2)
        double preyDistance = Simulator.distance(preyPose,vPose); // d = (x^2 + y^2)^(1/2)


        // define next angle between vehicles
        double nextPhi = getAngle(X,Y);
        
        // if vehicles are in the same place
         if (DX == 0 && DY == 0) {
             // slow down and stop turning
             s = minTransSpeed;
             omega = 0;
         } else {
             // make rotational vel of control proportional to future angle between vehicles
             nextPhi = normalizeAngle(nextPhi);
             omega = normalizeAngle(nextPhi - vPose[2]);

             if (preyDistance > targetFollowingDistance) {
                 s = maxTransSpeed;
             } else {
                 s = preySpeed;
             }
         }

        // clamp velocities
        Control nextControl = clampControl(s,omega);

        return nextControl;
    }


}





