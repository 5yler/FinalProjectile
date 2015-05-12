/**
 * GroundVehicle
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

import java.util.Random;

public class GroundVehicle extends Thread {

	private double _x, _y, _theta;    	/* shared resources */
	private double _dx, _dy, _dtheta; 	/* shared resources */


	public static final double MIN_VEL = 1;
	public static final double MAX_VEL = 10;
	public static final double MAX_OMEGA = Math.PI/2;

	private Simulator _sim;		// Simulator associated with GroundVehicle

	private long _startupTime;	// time when the GroundVehicle starts running

	public static final int GV_MS_INCREMENT = 100; // should be 100 for assignment 4

	protected final String _ID;	// unique string identifier
	private final int _numID; 	// unique numeric identifier for ordering all GroundVehicles

	static int vehicleCount = 0;	// number of GroundVehicles in existence

	private final boolean print = true;	// set to true for print statements
	private final boolean debug =false;         // set to true for debug statements

/* CONSTRUCTORS */
	public GroundVehicle (double pose[], double s, double omega) {
		if (pose.length != 3)
			throw new IllegalArgumentException("First argument must be array of length 3");      
		
		_x = pose[0]; 
		_y = pose[1]; 
		_theta = pose[2];

		_dx = s * Math.cos(_theta);
		_dy = s * Math.sin(_theta);
		_dtheta = omega;

		clampPosition();
		clampVelocity();

		// create random alphanumeric ID for vehicle
		_ID = randomString(2);
		vehicleCount += 1;
		_numID = vehicleCount;

	}

    public GroundVehicle (double pose[], double dx, double dy, double dtheta) {
        if (pose.length != 3)
            throw new IllegalArgumentException("First argument must be array of length 3");

        _x = pose[0];
        _y = pose[1];
        _theta = pose[2];

        _dx = dx;
        _dy = dy;
        _dtheta = dtheta;

        clampPosition();
        clampVelocity();

		// create random alphanumeric ID for vehicle
		_ID = randomString(2);
		vehicleCount += 1;
		_numID = vehicleCount;

		// set max simulation size
	}

/* STATIC METHODS */
	/**
	 * Generates a random double sampled from a Gaussian distribution with specified
	 * mean and variance ~N(mean, variance)
	 * @param mean mean of Gaussian distribution
	 * @param variance variance of Gaussian distribution
	 * @return random double sampled from Gaussian distribution
	 */
	private static double randomGaussianDouble(double mean, double variance) {
		Random r = new Random();
		return mean + r.nextGaussian()*variance;
	}

	public static int getVehicleCount() {
		return vehicleCount;
	}

/* GET METHODS */
	public synchronized double[] getPosition() {
		double[] position = new double[3];
		position[0] = _x;
		position[1] = _y;
		position[2] = _theta;

		return position;
	}

	public synchronized double[] getVelocity() {
		double[] velocity = new double[3];
		velocity[0] = _dx;
		velocity[1] = _dy;
		velocity[2] = _dtheta;

		return velocity;
	}

	public int getNumID() {
		return _numID;
	}

/* SET METHODS */
	/**
	 *
	 * @param sim Simulator associated with GroundVehicle
	 */
	public void setSimulator(Simulator sim){
		this._sim = sim;
	}

	public synchronized void setPosition(double[] newPos) {
		if (newPos.length != 3)
			throw new IllegalArgumentException("newPos must be of length 3");      

		_x = newPos[0];
		_y = newPos[1];
		_theta = newPos[2];

		clampPosition();
	}

	public synchronized void setVelocity(double[] newVel) {
		if (newVel.length != 3)
			throw new IllegalArgumentException("newVel must be of length 3");      

		_dx = newVel[0];
		_dy = newVel[1];
		_dtheta = newVel[2];		

		clampVelocity();
	}

/* OTHER METHODS */
	/**
	 *
	 * @param length desired length of string
	 * @return random alphanumeric string of chosen length
	 */
	private String randomString(int length) {
//		String A1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String A1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random r = new Random();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(A1.charAt(r.nextInt(A1.length())));
		}

		return sb.toString();
	}

	private void clampPosition() {
		_x = Math.min(Math.max(_x,0),Simulator.SIM_X);
		_y = Math.min(Math.max(_y,0),Simulator.SIM_Y);
		_theta = Math.min(Math.max(_theta, -Math.PI), Math.PI);
		if (_theta - Math.PI == 0 || Math.abs(_theta - Math.PI) < 1e-6)
			_theta = -Math.PI;
	}

	private void clampVelocity() {
		double velMagnitude = Math.sqrt(_dx*_dx+_dy*_dy);
		if (velMagnitude > GroundVehicle.MAX_VEL) {
			_dx = GroundVehicle.MAX_VEL * _dx/velMagnitude;
			_dy = GroundVehicle.MAX_VEL * _dy/velMagnitude;
		}

		if (velMagnitude < GroundVehicle.MIN_VEL) {
			_dx = GroundVehicle.MIN_VEL * _dx/velMagnitude;
			_dy = GroundVehicle.MIN_VEL * _dy/velMagnitude;
		}

		_dtheta = Math.min(Math.max(_dtheta, -MAX_OMEGA), MAX_OMEGA);
	}

	/**
	 * @param c Control to be applied to the vehicle containing
     *          desired forward speed and rotational velocity values
     *          to modify the internal velocities
	 */
	public synchronized void controlVehicle(Control c) {
        if (c == null) {
			// do nothing
		} else {
			// apply control if it's not null
            _dx = c.getSpeed() * Math.cos(_theta);
			_dy = c.getSpeed() * Math.sin(_theta);
            _dtheta = c.getRotVel();

			clampVelocity();
		}
	}

	/**
	 * This method changes the vehicle internal state by computing the appropriate
	 * kinematic and dynamic change that would occur after time t, where t is given
	 * in two arguments: seconds and milliseconds. Includes noise.
	 * @param sec
	 * @param msec
	 */
	public void advance(int sec, int msec) {
		double t = sec + msec * 1e-3;

		// curve model
		// assuming that _dx, _dy, and _dtheta was set beforehand by controlVehicle()
		double s = Math.sqrt(_dx * _dx + _dy * _dy);

		if (Math.abs(_dtheta) > 1e-3) { // the following model is not well defined when _dtheta = 0

			// circle center and radius
			double r = s/_dtheta;

			double xc = _x - r * Math.sin(_theta);
			double yc = _y + r * Math.cos(_theta);

			_theta = _theta + _dtheta * t;

			double rtheta = ((_theta - Math.PI) % (2 * Math.PI));

			if (rtheta < 0) {
				rtheta += 2*Math.PI;
			}
			_theta = rtheta - Math.PI;

			// update positions
			_x = xc + r * Math.sin(_theta);
			_y = yc - r * Math.cos(_theta);

			// update velocities
			_dx = s * Math.cos(_theta);
			_dy = s * Math.sin(_theta);

		} else { // straight motion with no change in theta
			_x = _x + _dx * t;
			_y = _y + _dy * t;
		}

		// generate Gaussian-distributed noise
		double errD = randomGaussianDouble(0,0.15);	// down-range noise
		double errC = randomGaussianDouble(0,0.15);	// cross-range noise

		// add noise terms to updated position
		_x = _x + errD*Math.cos(_theta) - errC*Math.sin(_theta);
		_y = _y + errD*Math.sin(_theta) + errC*Math.cos(_theta);

		clampPosition();
		clampVelocity();
	}

	/**
	 * This method changes the vehicle internal state by computing the appropriate
	 * kinematic and dynamic change that would occur after time t, where t is given
	 * in two arguments: seconds and milliseconds. Includes noise.
	 * @param sec
	 * @param msec
	 */
	public void updateState(int sec, int msec) {
		double t = sec + msec * 1e-3;

		// curve model
		// assuming that _dx, _dy, and _dtheta was set beforehand by controlVehicle()
		double s = Math.sqrt( _dx * _dx + _dy * _dy );

		if (Math.abs(_dtheta) > 1e-3) { // the following model is not well defined when _dtheta = 0

			// circle center and radius
			double r = s/_dtheta;

			double xc = _x - r * Math.sin(_theta);
			double yc = _y + r * Math.cos(_theta);

			_theta = _theta + _dtheta * t;

			double rtheta = ((_theta - Math.PI) % (2 * Math.PI));

			if (rtheta < 0) {
				rtheta += 2*Math.PI;
			}
			_theta = rtheta - Math.PI;

			// update velocities
			_x = xc + r * Math.sin(_theta);
			_y = yc - r * Math.cos(_theta);
			_dx = s * Math.cos(_theta);
			_dy = s * Math.sin(_theta);

		} else { // straight motion with no change in theta
			_x = _x + _dx * t;
			_y = _y + _dy * t;
		}

		clampPosition();
		clampVelocity();
	}

/* RUN METHOD */
	public void run() {

		_startupTime = System.nanoTime();
		long currentTime = System.nanoTime();
		long updateTime = System.nanoTime();

		while ((currentTime - _startupTime) < 100*1e9) { // while time less than 100s

			synchronized (_sim) { /* Conditional critical region */

				try {
					// wait for simulator to update
					_sim.wait();

				} catch (InterruptedException e) {
					System.err.printf("Interrupted Exception");
					e.printStackTrace();

				} // end try-catch
			} // end synchronized (_sim)

			currentTime = System.nanoTime();

			if ((currentTime - updateTime) >= GV_MS_INCREMENT *1e6) { // update once every 100ms

				long advanceTime = currentTime - updateTime;
				int advanceSec = (int) (advanceTime/1e9);
				int advanceMSec = (int) ((advanceTime-advanceSec*1e9)/1e6);
				advance(advanceSec, advanceMSec);

				// reset last update time
				updateTime = System.nanoTime();

			} // end if (100ms since last update)
		} // end while (time < 100s)
	} // end run()

/* run() method using System.currentTimeMillis() instead of System.nanoTime()

	public void run() {

		_startupTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
		updateTime = System.currentTimeMillis();

		while ((currentTime - _startupTime) < 100e3) { // while time less than 100s
			synchronized (_sim) { // Conditional critical region //
				try {
					// wait for simulator to update
					_sim.wait();
				} catch (InterruptedException e) {
					System.err.printf("Interrupted Exception");
					e.printStackTrace();
				}
			}
			currentTime = System.currentTimeMillis();
			if ((currentTime - updateTime) >= 100) { // update once every 100ms
				long advanceTime = currentTime - updateTime;
				int advanceSec = (int) (advanceTime/1e3);
				int advanceMSec = (int) (advanceTime-advanceSec*1e3);
				advance(advanceSec, advanceMSec);

				// reset last update time
				updateTime = System.currentTimeMillis();
			}
		}
	}
*/

}
