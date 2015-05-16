/**
 * GroundVehicleTest
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class GroundVehicleTest {

	//TODO: update with changes

    /**
     * Method: GroundVehicle(pose, s, dtheta) constructor
     */
    @Test
    public void testConstructor() {

        double [] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        double [] newPose = gv.getPosition();
        assertEquals(pose[0], newPose[0], 1e-6);
        assertEquals(pose[1], newPose[1], 1e-6);
        assertEquals(pose[2], newPose[2], 1e-6);

        double [] newVel = gv.getVelocity();
        assertEquals(dx, newVel[0], 1e-6);
        assertEquals(dy, newVel[1], 1e-6);
        assertEquals(dtheta, newVel[2], 1e-6);
    }

    /**
     * Method: GroundVehicle(pose, dx, dy, dtheta) constructor
     */
    @Test
    public void testConstructor2() {

        double [] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, dx, dy, dtheta);

        double [] newPose = gv.getPosition();
        assertEquals(pose[0], newPose[0], 1e-6);
        assertEquals(pose[1], newPose[1], 1e-6);
        assertEquals(pose[2], newPose[2], 1e-6);

        double [] newVel = gv.getVelocity();
        assertEquals(dx, newVel[0], 1e-6);
        assertEquals(dy, newVel[1], 1e-6);
        assertEquals(dtheta, newVel[2], 1e-6);
    }

    /**
     * Method: getVehicleCount()
     *
     * Test if static variable vehicleCount increases when new
     * GroundVehicle are created.
     */
    @Test
    public void testVehicleCount() {

        int vehicleCountBefore = GroundVehicle.getVehicleCount();

        // create ground vehicle
        double [] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv1 = new GroundVehicle(pose, dx, dy, dtheta);

        // check if vehicleCount increased to 1
        int expectedVehicleCount = vehicleCountBefore + 1;
        int vehicleCount = GroundVehicle.getVehicleCount();
        assertEquals(expectedVehicleCount, vehicleCount);

        GroundVehicle gv2 = new GroundVehicle(pose, dx, dy, dtheta);

        // check if vehicleCount increased to 2
        expectedVehicleCount++;
        vehicleCount = GroundVehicle.getVehicleCount();
        assertEquals(expectedVehicleCount, vehicleCount);

        // create ground vehicle with second constructor
        GroundVehicle gv3 = new GroundVehicle(pose, s, dtheta);

        // check if vehicleCount increased to 3
        expectedVehicleCount++;
        vehicleCount = GroundVehicle.getVehicleCount();
        assertEquals(expectedVehicleCount, vehicleCount);

        GroundVehicle gv4 = new GroundVehicle(pose, s, dtheta);

        // check if vehicleCount increased to 4
        expectedVehicleCount++;
        vehicleCount = GroundVehicle.getVehicleCount();
        assertEquals(expectedVehicleCount, vehicleCount);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsInConstructor() {
        // too many arguments in pose constructor
        double [] pose = {0, 0, 0, 0};
        new GroundVehicle(pose, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsInConstructor() {
        // too few arguments in pose constructor
        double [] pose = {0};
        new GroundVehicle(pose, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsInConstructor2() {
        // too many arguments in pose constructor
        double [] pose = {0, 0, 0, 0};
        new GroundVehicle(pose, 0, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsInConstructor2() {
        // too few arguments in pose constructor
        double [] pose = {0};
        new GroundVehicle(pose, 0, 0, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsSetPosition() {
        // too many arguments in setPosition
        double [] pose = {0, 0, 0};
        GroundVehicle gv = new GroundVehicle(pose, 0, 0);
        double [] newPose = {0, 0, 0, 0};
        gv.setPosition(newPose);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsSetPosition() {
        // too few arguments in setPosition
        double [] pose = {0, 0, 0};
        GroundVehicle gv = new GroundVehicle(pose, 0, 0);
        double [] newPose = {0};
        gv.setPosition(newPose);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooManyArgumentsSetVelocity() {
        // too many arguments in setVelocity
        double [] pose = {0, 0, 0};
        GroundVehicle gv = new GroundVehicle(pose, 0, 0);
        double [] newVel = {0, 0, 0, 0};
        gv.setVelocity(newVel);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooFewArgumentsSetVelocity() {
        // too few arguments in setVelocity
        double [] pose = {0, 0, 0};
        GroundVehicle gv = new GroundVehicle(pose, 0, 0);
        double [] newVel = {0};
        gv.setVelocity(newVel);
    }

    /**
     * Method: getPosition()
     * Method: setPosition(double[] newPos)
     *
     * Test get/set Position at all legal position bounds
     */
    @Test
    public void testGetSetPositionValid() {

        double[] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        double[] newPose = gv.getPosition();
        assertEquals(pose[0], newPose[0], 1e-6);
        assertEquals(pose[1], newPose[1], 1e-6);
        assertEquals(pose[2], newPose[2], 1e-6);

        pose[0] = 0;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[0], newPose[0], 1e-6);

        pose[0] = Simulator.SIM_X;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[0], newPose[0], 1e-6);

        pose[1] = 0;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[1], newPose[1], 1e-6);

        pose[1] = Simulator.SIM_Y;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[1], newPose[1], 1e-6);

        pose[2] = -Math.PI;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[2], newPose[2], 1e-6);

        pose[2] = Math.PI - 1e-6;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(pose[2], newPose[2], 1e-6);
    }

    /**
     * Method: getVelocity()
     * Method: setVelocity(double[] newVel)
     *
     * Test getVelocity and setVelocity at all legal position bounds
     */
    @Test
    public void testGetSetVelocityValid() {

        double[] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        double[] newVel = gv.getVelocity();
        assertEquals(dx, newVel[0], 1e-6);
        assertEquals(dy, newVel[1], 1e-6);
        assertEquals(dtheta, newVel[2], 1e-6);

        double [] vel = gv.getVelocity();

        double maxSpeed = 10;
        double minRotVel = -Math.PI/4; // inclusive
        double maxRotVel = Math.PI/4;  // non-inclusive

        vel[0] = 5;
        vel[1] = 0;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[0], newVel[0], 1e-6);

        vel[0] = maxSpeed;
        vel[1] = 0;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[0], newVel[0], 1e-6);

        vel[0] = 0;
        vel[1] = 5;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[1], newVel[1], 1e-6);

        vel[0] = 0;
        vel[1] = maxSpeed;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[1], newVel[1], 1e-6);

        vel[2] = minRotVel;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[2], newVel[2], 1e-6);

        vel[2] = maxRotVel;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(vel[2], newVel[2], 1e-6);
    }

    /**
     * Method: getPosition()
     * Method: setPosition(double[] newPos)
     *
     * Test get/set Position and Velocity at illegal position bounds
     */
    @Test
    public void testGetSetPositionInvalid(){

        double [] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);
        double [] newPose = gv.getPosition();

        // test getPosition and setPosition at illegal bounds
        // since all bounds violations get clamped to legal limits,
        // we can test all three dimensions of position at once

        pose[0] = -1;
        pose[1] = -1;
        pose[2] = -2*Math.PI;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(0, newPose[0], 1e-6);
        assertEquals(0, newPose[1], 1e-6);
        assertEquals(-Math.PI, newPose[2], 1e-6);

        pose[0] = Simulator.SIM_X+1;
        pose[1] = Simulator.SIM_Y+1;
        pose[2] = Math.PI;
        gv.setPosition(pose);
        newPose = gv.getPosition();
        assertEquals(Simulator.SIM_X, newPose[0], 1e-6);
        assertEquals(Simulator.SIM_Y, newPose[1], 1e-6);
        assertEquals(-Math.PI, newPose[2], 1e-6);

    }

    /**
     * Method: getVelocity()
     * Method: setVelocity(double[] newVel)
     *
     * Test get/set Velocity at illegal position bounds
     */
    @Test
    public void testGetSetVelocityInvalid(){

        double [] pose = {1, 2, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        // test getVelocity and setVelocity at illegal bounds
        // since all bounds violations get clamped to legal limits,
        // we can test all three dimensions of velocity at once

        double [] vel = gv.getVelocity();
        vel[0] = 0;
        vel[1] = 0.5*GroundVehicle.MIN_VEL;
        vel[2] = -GroundVehicle.MAX_OMEGA-1;
        gv.setVelocity(vel);
        double [] newVel = gv.getVelocity();
        assertEquals(0, newVel[0], 1e-6);
        assertEquals(GroundVehicle.MIN_VEL, newVel[1], 1e-6);
        assertEquals(-GroundVehicle.MAX_OMEGA, newVel[2], 1e-6);

        vel[0] = 0;
        vel[1] = GroundVehicle.MAX_VEL+1;
        vel[2] = GroundVehicle.MAX_OMEGA+1;
        gv.setVelocity(vel);
        newVel = gv.getVelocity();
        assertEquals(0, newVel[0], 1e-6);
        assertEquals(GroundVehicle.MAX_VEL, newVel[1], 1e-6);
        assertEquals(GroundVehicle.MAX_OMEGA, newVel[2], 1e-6);
    }

    /**
     * Method: controlVehicle(Control c)
     */
    @Test
    public void testControlVehicle() {
        double [] pose = {0, 0, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 0;
        GroundVehicle gv = new GroundVehicle(pose, dx, dy, dtheta);

        // apply null control, velocities should not change

        Control c = null;
        gv.controlVehicle(c);

        double [] nullVel = gv.getVelocity();

        assertEquals(5, nullVel[0], 1e-6);
        assertEquals(0, nullVel[1], 1e-6);
        assertEquals(0, nullVel[2], 1e-6);

        // acceleration in x

        c = new Control(10, 0);
        gv.controlVehicle(c);

        double [] newVel = gv.getVelocity();

        assertEquals(10, newVel[0], 1e-6);
        assertEquals(0, newVel[1], 1e-6);
        assertEquals(0, newVel[2], 1e-6);

        // acceleration in y

        pose[0] = 0;
        pose[1] = 0;
        pose[2] = Math.PI/2;
        gv.setPosition(pose);
        double [] vel = {10, 0, 0};
        gv.setVelocity(vel);

        c = new Control(10, 0);
        gv.controlVehicle(c);

        newVel = gv.getVelocity();
        assertEquals(0, newVel[0], 1e-6);
        assertEquals(10, newVel[1], 1e-6);
        assertEquals(0, newVel[2], 1e-6);

        // acceleration at PI/4 from 5 m/s to 10 m/s.

        vel[0] = Math.sqrt(12.5);
        vel[1] = Math.sqrt(12.5);
        vel[2] = Math.PI/4;
        gv.setVelocity(vel);
        c = new Control(10, 0);
        gv.controlVehicle(c);

        newVel = gv.getVelocity();
        assertEquals(10, Math.sqrt(newVel[0] * newVel[0] + newVel[1] * newVel[1]), 1e-6);

        // rotational acceleration in x

        vel[2] = 0;
        gv.setVelocity(vel);
        c = new Control(5, Math.PI/8);
        gv.controlVehicle(c);

        newVel = gv.getVelocity();
        assertEquals(Math.PI / 8, newVel[2], 1e-6);
    }
    
   
    /**
     * Method: updateState(int sec, int msec)
     */
    @Test
    public void testUpdateState() {
        double [] pose = {0, 0, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        // straight-line motion along x

        gv.updateState(1, 0);

        double [] newPose = gv.getPosition();
        assertEquals(5, newPose[0], 1e-6);
        assertEquals(0, newPose[1], 1e-6);
        assertEquals(0, newPose[2], 1e-6);

        // straight-line motion along y

        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;
        gv.setPosition(pose);
        double [] vel = {0, 5, 0};
        gv.setVelocity(vel);

        gv.updateState(1, 0);

        newPose = gv.getPosition();
        assertEquals(0, newPose[0], 1e-6);
        assertEquals(5, newPose[1], 1e-6);
        assertEquals(0, newPose[2], 1e-6);

        // straight-line motion along PI/4

        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;
        gv.setPosition(pose);

        // set vehicle moving at 5 m/s along PI/4

        vel[0] = Math.sqrt(12.5);
        vel[1] = Math.sqrt(12.5);
        vel[2] = 0;
        gv.setVelocity(vel);

        double [] newVel = gv.getVelocity();
        assertEquals(vel[0], newVel[0], 1e-6);
        assertEquals(vel[1], newVel[1], 1e-6);
        assertEquals(vel[2], newVel[2], 1e-6);

        gv.updateState(1, 0);

        newPose = gv.getPosition();
        assertEquals(Math.sqrt(12.5), newPose[0], 1e-6);
        assertEquals(Math.sqrt(12.5), newPose[1], 1e-6);
        assertEquals(0, newPose[2], 1e-6);

        // rotational motion

        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;
        gv.setPosition(pose);

        vel[0] = Math.sqrt(5);
        vel[1] = Math.sqrt(5);
        vel[2] = Math.PI/8;
        gv.setVelocity(vel);

        gv.updateState(1, 0);

        newPose = gv.getPosition();
        assertEquals(Math.PI / 8, newPose[2], 1e-6);
    }

    /**
     * Method: advance(int sec, int msec)
     *
     * Each dimension of motion is tested multiple times.
     * The average result of repeating the same velocity inputs is
     * compared to the expected effect it would have to the vehicle.
     * Since the noise is normally distributed, averaging the results
     * will get a closer value to the expected one and cancel out
     * the fluctuations present in individual trials.
     */
    @Test
    public void testAdvance() {

        // create variables to store sums
        double xSum = 0;
        double ySum = 0;
        double thetaSum = 0;
        double dxSum = 0;
        double dySum = 0;
        double dThetaSum = 0;

        int n = 100000; // number of trials



        double [] pose = {0, 0, 0};
        double dx = 5, dy = 0, dtheta = 0;
        double s = 5;

        // define error tolerance for tests
        double tolerance = 5*1e-3;
        /* this must be higher than 1e-6 used in updateState() because
           the new equations of motion are non-deterministic and have both
           crossrange and downrange noise */

        // create ground vehicle
        GroundVehicle gv = new GroundVehicle(pose, s, dtheta);

        // straight-line motion along x
        double [] vel = {5, 0, 0};

        for (int i = 0; i < n; i++) {
            gv.setPosition(pose);
            gv.setVelocity(vel);

            gv.advance(1, 0);

            double[] newPose = gv.getPosition();
            xSum += newPose[0];
            ySum += newPose[1];
            thetaSum += newPose[2];
        }

        double [] avgPose = {xSum/n, ySum/n, thetaSum/n};
        assertEquals(5, avgPose[0], tolerance);
        assertEquals(0, avgPose[1], 1e-1);
        assertEquals(0, avgPose[2], tolerance);

        // reset sums
        xSum = 0;
        ySum = 0;
        thetaSum = 0;

        // straight-line motion along y
        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;
        vel[0] = 0;
        vel[1] = 5;
        vel[2] = 0;

        for (int i = 0; i < n; i++) {
            gv.setPosition(pose);
            gv.setVelocity(vel);

            gv.advance(1, 0);

            double[] newPose = gv.getPosition();
            xSum += newPose[0];
            ySum += newPose[1];
            thetaSum += newPose[2];
        }

        avgPose[0] = xSum/n;
        avgPose[1] = ySum/n;
        avgPose[2] = thetaSum/n;
        assertEquals(0, avgPose[0], 1e-1);
        assertEquals(5, avgPose[1], tolerance);
        assertEquals(0, avgPose[2], tolerance);

        // reset sums
        xSum = 0;
        ySum = 0;
        thetaSum = 0;

        // straight-line motion along PI/4
        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;

        // set vehicle moving at 5 m/s along PI/4
        vel[0] = Math.sqrt(12.5);
        vel[1] = Math.sqrt(12.5);
        vel[2] = 0;

        for (int i = 0; i < n; i++) {
            gv.setPosition(pose);
            gv.setVelocity(vel);

            gv.advance(1, 0);

            double[] newPose = gv.getPosition();
            xSum += newPose[0];
            ySum += newPose[1];
            thetaSum += newPose[2];
        }

        avgPose[0] = xSum/n;
        avgPose[1] = ySum/n;
        avgPose[2] = thetaSum/n;
        assertEquals(Math.sqrt(12.5), avgPose[0], tolerance);
        assertEquals(Math.sqrt(12.5), avgPose[1], tolerance);
        assertEquals(0, avgPose[2], tolerance);

        // reset sums
        xSum = 0;
        ySum = 0;
        thetaSum = 0;

        // rotational motion

        pose[0] = 0;
        pose[1] = 0;
        pose[2] = 0;
        vel[0] = Math.sqrt(5);
        vel[1] = Math.sqrt(5);
        vel[2] = Math.PI/8;

        for (int i = 0; i < n; i++) {
            gv.setPosition(pose);
            gv.setVelocity(vel);

            gv.advance(1, 0);

            double[] newPose = gv.getPosition();
            xSum += newPose[0];
            ySum += newPose[1];
            thetaSum += newPose[2];
        }

        avgPose[2] = thetaSum/n;
        assertEquals(Math.PI / 8, avgPose[2], tolerance);
    }

    public static void main(String[] args){
        JUnitCore.main(GroundVehicleTest.class.getName());
    }

} 
