/**
 * LeadingController
 * 16.35 Assignment #3 Final Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * VehicleController subclass called LeadingController that generates
 * controls to move away from all the other GroundVehicle objects.
 */
public class LeadingController extends RandomController {

    //TODO: remove followerList?
//    private ArrayList<GroundVehicle> _followerList;  // list of GroundVehicles following LeadingController
    private ArrayList<Integer> _followerIndexList;  // list of indexes of GroundVehicles following LeadingController


    private double maxTransSpeed = 10;
    private double minTransSpeed = 5;
    double dangerZone = 20; // size of boundary that is judged too close to wall


    private final boolean print = true;   // set to true for print statements
    private final DecimalFormat df = new DecimalFormat("+#.0;-0");

    private final boolean debug = false;   // set to true for debug statements

    /**
     * @param s simulator object
     * @param v specific GroundVehicle being controlled
     */
    public LeadingController(Simulator sim, GroundVehicle v) {
        super(sim, v);
        _followerIndexList = new ArrayList<Integer>();
    }


    /**
     * @return closest GroundVehicle from the list of GroundVehicles
     * following the LeadingController
     */
    private int getClosestFollower() {


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
        int closestIndex = 0;

        Collections.sort(_followerIndexList);

        for (int i = 0; i < _followerIndexList.size(); i++) {    // iterate over list of i vehicles
            GroundVehicle follower = _sim.getVehicle(_followerIndexList.get(i) - 1);  // get vehicle from sim's list

            // get position of follower
            double[] followerPosition = follower.getPosition(); // [x, y, theta] of vehicle
            double followerX = followerPosition[0];
            double followerY = followerPosition[1];

            // define difference in x, y between vehicles
            double DX = followerX - vPose[0];
            double DY = followerY - vPose[1];

            // calculate linear distance of follower
            double distance = Math.sqrt(DX * DX + DY * DY);

            if (distance < shortestDistance) {
                // update distance to closest follower
                shortestDistance = distance;
                // save index of closest follower
                closestIndex = i;
            }

        }

        // retrieve nearest vehicle
        int closest = _followerIndexList.get(closestIndex)-1;
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

        if ((x < dangerZone) || (x > Simulator.SIM_X -dangerZone)) {
            return true;
        } else if ((y < dangerZone) || (y > Simulator.SIM_Y -dangerZone)) {
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


        if (_followerIndexList.size()==0) {

            // move randomly if there are no followers
            nextControl = super.getControl(sec, msec);

        } else {

            // compute a trajectory that moves away from the nearest vehicle

            GroundVehicle follower = _sim.getVehicle(getClosestFollower());

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
            s = maxTransSpeed;

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
            if (x < dangerZone) {
                desiredTheta = 0;
                wallDistance += x;
            } else if (x > Simulator.SIM_X - dangerZone) {
                desiredTheta = -Math.PI;
                wallDistance += Simulator.SIM_X - x;
            } else if (y < dangerZone) {
                desiredTheta = Math.PI / 2;
                wallDistance += y;
            } else { // if (y > Simulator.SIM_Y-dangerZone)
                desiredTheta = -Math.PI / 2;
                wallDistance += Simulator.SIM_Y - y;
            }

            omega = normalizeAngle(desiredTheta - vPose[2]);
            s = maxTransSpeed - (dangerZone - wallDistance) / dangerZone * (maxTransSpeed - minTransSpeed);

            // clamp velocities
            nextControl = clampControl(s, omega);

        }

        return nextControl;

    }

    /**
     *
     * @param follower GroundVehicle that is following LeadingController
     */
    public synchronized void addFollower(GroundVehicle follower) {
        // add vehicle ID to list
        _followerIndexList.add(follower.getNumID());
        Collections.sort(_followerIndexList);
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



