/**
 * LeadingController
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

/**
 * VehicleController subclass called LeadingController that generates
 * controls to move away from all the other GroundVehicle objects.
 */
public class LeadingController extends VehicleController {

    public static final double LEADING_MIN_VEL = 5 * GroundVehicle.MIN_VEL;
    public static final double LEADING_MAX_VEL = GroundVehicle.MAX_VEL;
    public static final double DANGER_ZONE = 20; // size of boundary that is judged too close to wall

    /**
     * @param sim simulator object
     * @param v specific GroundVehicle being controlled
     */
    public LeadingController(Simulator sim, GroundVehicle v) {
        super(sim, v);
        _v._color = Simulator.LEADING_COLOR;
    }

    /**
     * @return closest GroundVehicle from the list of GroundVehicles
     * following the LeadingController
     */
    public GroundVehicle getClosestFollower() {

        // position of leader vehicle
        double[] vPosition = _v.getPosition();

        // set placeholder value for shortest distance to be the maximum possible
        // distance between two vehicles
        double shortestDistance = Math.sqrt(Simulator.SIM_X *Simulator.SIM_X + Simulator.SIM_Y *Simulator.SIM_Y);

        // make variable to store index of closest follower
        GroundVehicle closestVehicle = null;

        for (GroundVehicle otherVehicle : _sim._vehicleList) {    // iterate over Simulator vehicle list
            if (otherVehicle != _v) { // only iterate over vehicles not associated with this controller

                // get position of follower
                double[] otherVehiclePosition = otherVehicle.getPosition(); // [x, y, theta] of vehicle
                double otherX = otherVehiclePosition[0];
                double otherY = otherVehiclePosition[1];

                // define difference in x, y between vehicles
                double DX = otherX - vPosition[0];
                double DY = otherY - vPosition[1];

                // calculate linear distance of follower
                double otherVehicleDistance = Math.sqrt(DX * DX + DY * DY);

                if (otherVehicleDistance < shortestDistance) {
                    // update distance to closest follower
                    closestVehicle = otherVehicle;
                    shortestDistance = otherVehicleDistance;
                }
            }
        }

        return closestVehicle;
    }

    /**
     *
     * @param vehiclePosition position of leading GroundVehicle
     * @return true if close to wall
     */
    public boolean tooCloseToWalls(double[] vehiclePosition) {
        double x = vehiclePosition[0];
        double y = vehiclePosition[1];

        if ((x < DANGER_ZONE) || (x > Simulator.SIM_X - DANGER_ZONE)) {
            return true;
        } else if ((y < DANGER_ZONE) || (y > Simulator.SIM_Y - DANGER_ZONE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The getControl method takes as an argument a time value in seconds
     * and milliseconds, and returns a control if one should be issued at
     * this time. Provides the functionality that causes the vehicle to
     * follow a specific trajectory.
     */
    public Control getControl(int sec, int msec) {

        Control nextControl = null;

        double s;
        double omega;

        // position of leader vehicle
        double[] vPose = _v.getPosition();
        double[] vVel = _v.getVelocity();


        if (_sim._vehicleList.size() <= 1) {

            // move randomly if there are no followers
            nextControl = super.getControl(sec, msec);

        } else {

            // compute a trajectory that moves away from the nearest vehicle

            GroundVehicle follower = getClosestFollower();

            // position and velocity of closest follower
            double[] followerPose = follower.getPosition();
            double[] followerVel = follower.getVelocity();

            // define difference in x, y, and theta between vehicles
            double DX = vPose[0] - followerPose[0];
            double DY = vPose[1] - followerPose[1];

            // define next difference in x, y
            double X = DX + (vVel[0] - followerVel[0]) * _dt;
            double Y = DY + (vVel[1] - followerVel[1]) * _dt;

            // define next angle between vehicles
            double nextPhi = Math.atan2(Y,X);

            // make rotational vel of control proportional to future angle between vehicles
            omega = normalizeAngle(nextPhi - vPose[2]);
            s = LEADING_MAX_VEL;

            // clamp velocities
            nextControl = clampControl(s, omega);
        }

        // stay in the boundary of the visible space

        if (tooCloseToWalls(vPose)) { // if too close to walls

            double x = vPose[0];
            double y = vPose[1];

            double desiredTheta;
            // point away from walls
            double wallDistance = 0;
            if (x < DANGER_ZONE) {
                desiredTheta = 0;
                wallDistance += x;
            } else if (x > Simulator.SIM_X - DANGER_ZONE) {
                desiredTheta = -Math.PI;
                wallDistance += Simulator.SIM_X - x;
            } else if (y < DANGER_ZONE) {
                desiredTheta = Math.PI / 2;
                wallDistance += y;
            } else { // if (y > Simulator.SIM_Y-DANGER_ZONE)
                desiredTheta = -Math.PI / 2;
                wallDistance += Simulator.SIM_Y - y;
            }

            omega = normalizeAngle(desiredTheta - vPose[2]);
            s = LEADING_MAX_VEL - (DANGER_ZONE - wallDistance) / DANGER_ZONE * (LEADING_MAX_VEL - LEADING_MIN_VEL);

            // clamp velocities
            nextControl = clampControl(s, omega);
        }

        return nextControl;
    }
}
