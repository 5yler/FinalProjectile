
/**
 * CircleController
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

public class CircleController extends VehicleController {

    private double maxTransSpeed = 10; //[m/s]
    private double minTransSpeed = 5;  //[m/s]

    private int Xc = 50;     //[m]    x location of circle center
    private int Yc = 50;     //[m]    y location of circle center
    private int R  = 25;     //[m]    circle radius
    private double Vc = maxTransSpeed; //[m/s]  desired velocity for circular motion

    private final boolean print = true;   // set to true for print statements
    private final boolean debug = true;   // set to true for debug statements

    /**
     * @param s simulator object
     * @param v specific GroundVehicle being controlled
     */
    public CircleController(Simulator s, GroundVehicle v) {
        super(s, v);
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

        // position of vehicle
        vPose = _v.getPosition();
        vVel = _v.getVelocity();
        vSpeed = Math.sqrt(Math.pow(vVel[0], 2) + Math.pow(vVel[1], 2));

        // define x and y, and radius with respect to circle center
        double DX = vPose[0] - Xc;
        double DY = vPose[1] - Yc;
        double r = Math.sqrt(DX*DX+DY*DY);

        // define angle alpha relative to circle center
        double alpha = Math.atan2(DY,DX);

        // define angle Tc and rotational velocity Wc vehicle would have
        // if on circle trajectory with same alpha
        double Tc = alpha + Math.PI/2;
        Tc = normalizeAngle(Tc);
        double Wc = R*Vc;

        // define errors
        double angleError   = normalizeAngle(Tc - vPose[2]);
        double radiusError  = R - r;

        // define gains for error terms in rotational velocity control calculation
        double Ka = 5;      // angle
        double Kr = 0.75;   // radius

        // adjust rotational velocity to depend on angle error and radius error
        omega = Ka*angleError - Kr*radiusError;

        // set forward speed to be constant
        s = Vc;

        // clamp velocities
        Control nextControl = clampControl(s,omega);

        return nextControl;
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

}
