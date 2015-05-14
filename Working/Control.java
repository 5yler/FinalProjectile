/**
 * Control
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 **/

public class Control {
    private double s; // forward velocity (speed)
    private double omega; // angular velocity

    /**
     * @param s speed
     * @param omega angular velocity
     */
    public Control (double s, double omega){
        // check to make sure s is in range.
        if (s < GroundVehicle.MIN_VEL || s > GroundVehicle.MAX_VEL)
            throw new IllegalArgumentException("S out of range");
        	//System.out.println(s);
        // check to make sure theta is in range.
        if (omega < -Math.PI || omega >= Math.PI)
            throw new IllegalArgumentException("Omega out of range");

        this.s = s;
        this.omega = omega;
    }

    public double getSpeed() {
        double speed = this.s;
        return speed;
    }

    public double getRotVel() {
        double rotVel = this.omega;
        return rotVel;
    }
}
