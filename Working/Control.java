/**
 * Control
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
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
        if (s < 1 || s > 10)
            throw new IllegalArgumentException("S out of range");
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
