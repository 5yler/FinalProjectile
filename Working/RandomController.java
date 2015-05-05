/**
 * RandomController
 * 16.35 Assignment #3 Final Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.util.Random;

/**
 * Generates controls randomly. You may decide how you want to generate
 * controls randomly: simply changing the speed and rotational velocity is
 * unlikely to be interesting. You can generate more interesting behavior
 * using more structured randomness.
 */
public class RandomController extends VehicleController {

    private boolean controllerInitialized;
    private double _startSpeed;
    private double _startOmega;

    /**
     * @param s simulator object
     * @param v specific GroundVehicle being controlled
     */
    public RandomController(Simulator s, GroundVehicle v) {
        super(s, v);

        controllerInitialized = false; // starting velocity controls will be set during initialization

    }

    /**
     * The getControl method takes as an argument a time value in seconds
     * and milliseconds, and returns a control if one should be issued at
     * this time. Provides the functionality that causes the vehicle to
     * follow a specific trajectory.
     */
    public Control getControl(int sec, int msec) {

        Control nextControl;
        double nextSpeed;
        double nextOmega;

        // random control generation
        if (!controllerInitialized) {
            initializeController();
            nextControl = new Control(_startSpeed,_startOmega);
        } else {

            // get current speed and omega
            double[] currentVel = _v.getVelocity();
            double currentSpeed = Math.sqrt(Math.pow(currentVel[0], 2) + Math.pow(currentVel[1], 2));
            double currentOmega = currentVel[2];

            // small random increments
            Random r = new Random();
            int n = r.nextInt(3) - 1;
            double ds = r.nextDouble() * r.nextDouble();
            double dOmega = n * 0.1 * r.nextDouble();

            nextSpeed = currentSpeed + ds;
            nextOmega = currentOmega + dOmega;

            if (nextSpeed < 5 || nextSpeed > 10) { // check to make sure s is in range.
                // if s is outside allowable range, start again with random s in range
                nextSpeed = randomDoubleInRange(5, 10);
            }
            if (nextOmega < -Math.PI/4 || nextOmega >= Math.PI/4) { // check to make sure theta is in range.
                // if omega is outside allowable range, start again with random omega in range
                nextOmega = randomDoubleInRange(-Math.PI/4, Math.PI/4);
            }

            nextControl = new Control(nextSpeed,nextOmega);
        }

        return nextControl;
    }

    private void initializeController() {

        // generate starting values
        _startSpeed = randomDoubleInRange(5,10);
        _startOmega = randomDoubleInRange(-Math.PI/4, Math.PI/4);

        // initialize controller
        controllerInitialized = true;
    }

    /**
     * Generates random double within a specified range.
     * @param rangeMin lower bound of range
     * @param rangeMax upper bound of range
     * @return random double in range
     */
    private double randomDoubleInRange(double rangeMin, double rangeMax){
        Random r = new Random();
        double doubleInRange = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return doubleInRange;
    }
}

