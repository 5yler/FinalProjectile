/**
 * Projectile
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

public class Projectile extends Thread {
	//class for projectiles
	//start at user vehicle angel/position and travel in a straight line
	//disappear when hit radius around target or go off screen

	public static final double PROJECTILE_SPEED = 6*GroundVehicle.MAX_VEL;
	public static final double HIT_DISTANCE = 4;


    private double _x, _y, _theta;
    private double _dx, _dy;

	public int _color;	// index of vehicle color in DisplayServer.COLORS array
	public int ID; 		// UserController ID

	private Simulator _sim;
	public UserController _uc;

	public static final int UPDATE_MS = FinalProjectile.PROJECTILE_MS;

	public static int SHOTS_FIRED = 0;	// total number of projectiles fired


	public Projectile(double[] shooterPosition, Simulator sim, UserController uc) {
		if (shooterPosition.length != 3) {
			throw new IllegalArgumentException("First argument must be array of length 3");
		}
		if (sim == null) {
			throw new IllegalArgumentException("Simulator must not be null");
		}
		if (uc == null) {
			throw new IllegalArgumentException("UserController must not be null");
		}

		_sim = sim;
		_uc = uc;

		_color = _uc.getUserVehicle()._color;
		ID = _uc.UserID;

		_x = shooterPosition[0];
    	_y = shooterPosition[1];
    	_theta = shooterPosition[2];

    	_dx = PROJECTILE_SPEED * Math.cos(_theta);
		_dy = PROJECTILE_SPEED * Math.sin(_theta);

		SHOTS_FIRED++;
    }

	public synchronized void shoot(int sec, int msec) {
		double t = sec + msec * 1e-3;

		// straight motion, no change in theta
	    _x = _x + _dx * t;
	    _y = _y + _dy * t;
    }

	public synchronized double[] getPosition() {
		double[] position = new double[3];
		position[0] = _x;
		position[1] = _y;
		position[2] = _theta;
		return position;
	}


	public synchronized double[] getDisplayData() {
		double[] displayData = new double[3];
		displayData[0] = _x;
		displayData[1] = _y;
		displayData[2] = _color;
		return displayData;
	}

	/* RUN METHOD */
	public void run() {

		long startupTime = _sim.STARTUP_TIME;
		long currentTime = System.nanoTime();
		long updateTime = System.nanoTime();

		while ((currentTime - startupTime) < FinalProjectile.GAME_TIME*1e9) { // while time less than game time

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

			if ((currentTime - updateTime) >= UPDATE_MS *1e6) { // update once every increment

				long advanceTime = currentTime - updateTime;
				int advanceSec = (int) (advanceTime/1e9);
				int advanceMSec = (int) ((advanceTime-advanceSec*1e9)/1e6);
				shoot(advanceSec, advanceMSec);

				// reset last update time
				updateTime = System.nanoTime();

			} // end if (UPDATE_MS since last update)
		} // end while (time < FinalProjectile.GAME_TIME)

	} // end run()

}
