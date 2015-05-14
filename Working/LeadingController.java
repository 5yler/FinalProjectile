/**
 * LeadingController
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * VehicleController subclass called LeadingController that generates
 * controls to move away from all the other GroundVehicle objects.
 */
public class LeadingController extends VehicleController {

    //TODO: remove followerList?
//    private ArrayList<GroundVehicle> _followerList;  // list of GroundVehicles following LeadingController
//    private ArrayList<Integer> _followerIndexList;  // list of indexes of GroundVehicles following LeadingController


    private double LEADING_MAX_VEL = GroundVehicle.MAX_VEL;
    private double LEADING_MIN_VEL = 5;
    double DANGER_ZONE = 20; // size of boundary that is judged too close to wall


    private final boolean print = true;   // set to true for print statements
    private final DecimalFormat df = new DecimalFormat("+#.0;-0");

    private final boolean debug = false;   // set to true for debug statements

    /**
     * @param sim simulator object
     * @param v specific GroundVehicle being controlled
     */
    public LeadingController(Simulator sim, GroundVehicle v) {
        super(sim, v);
//        _followerIndexList = new ArrayList<Integer>();
        //TODO: req update for below
        _v.color = Simulator.LEADING;
    }


    /**
     * @return closest GroundVehicle from the list of GroundVehicles
     * following the LeadingController
     */
    private GroundVehicle getClosestFollower() {


        /* TODO: delete this if we don't need to store location of each follower

        double[] distances = new double[_followerList.size()];

        for (int i = 0; i < _followerList.size(); i++) {     // iterate over list of i vehicles
            GroundVehicle follower = _followerList.get(i);    // get vehicle at index i

            // get position of folower
            double[] followerPosition = follower.getPosition();      // get [x, y, theta] of vehicle
            double followerX = followerPosition[0];
            double followerY = followerPosition[1];

            // calculate linear distance of follower
            distances[i] = Math.sqrt(followerX*followerX + followerY*followerY);
        }

        */

        // position of leader vehicle
        double[] vPose;
        double[] vVel;
        double vSpeed;

        // position of leader vehicle
        vPose = _v.getPosition();
        vVel = _v.getVelocity();
        vSpeed = Math.sqrt(Math.pow(vVel[0], 2) + Math.pow(vVel[1], 2));

        // set placeholder value for shortest distance to be the maximum possible
        // distance between two vehicles
        double shortestDistance = Math.sqrt(Simulator.SIM_X *Simulator.SIM_X + Simulator.SIM_Y *Simulator.SIM_Y);

        // make variable to store index of closest follower
        GroundVehicle closest = null;

        for (GroundVehicle follower : _sim._vehicleList) {    // iterate over list of i vehicles

            if (follower != _v) {
                // get position of follower
                double[] followerPosition = follower.getPosition(); // [x, y, theta] of vehicle
                double followerX = followerPosition[0];
                double followerY = followerPosition[1];

                // define difference in x, y between vehicles
                double DX = followerX - vPose[0];
                double DY = followerY - vPose[1];

                // calculate linear distance of follower
                double followerDistance = Math.sqrt(DX * DX + DY * DY);

                if (followerDistance < shortestDistance) {
                    // update distance to closest follower
                    closest = follower;
                }
            }

        }

        // retrieve nearest vehicle
//        int closest = _followerIndexList.get(closestIndex)-1;
        return closest;
    }

    /**
     *
     * @param vehiclePosition position of leading GroundVehicle
     * @return true if close to wall
     */
    private boolean tooCloseToWalls(double[] vehiclePosition) {
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
            double followerSpeed = Math.sqrt(Math.pow(followerVel[0], 2) + Math.pow(followerVel[1], 2));

            // define difference in x, y, and theta between vehicles
            double DX = vPose[0] - followerPose[0];
            double DY = vPose[1] - followerPose[1];

            // define next difference in x, y
            double X = DX + (vVel[0] - followerVel[0]) * _dt;
            double Y = DY + (vVel[1] - followerVel[1]) * _dt;

            // define next angle between vehicles
            double nextPhi = Math.atan(Y / X);
            if (X < 0) {
                nextPhi += Math.PI;
            }

            // make rotational vel of control proportional to future angle between vehicles
            nextPhi = normalizeAngle(nextPhi);
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
