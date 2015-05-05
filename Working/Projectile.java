
public class Projectile extends Thread {
	//class for projectiles
	//start at user vehicle angel/position and travel in a straight line
	//disappear when hit radius around target or go off screen

	public static final double PROJECTILE_SPEED = 3*GroundVehicle.MAX_VEL;

    private double _x, _y, _theta;
    private double _dx, _dy;

    private Simulator _sim;

	private long _startupTime;  // time when the VehicleController starts running

	public static final int COMPLETELY_ARBITRARY_MS_INCREMENT = 100; // should be 100 for assignment 4


	public Projectile(double[] shooterPosition, Simulator sim) {

		if (shooterPosition.length != 3)
			throw new IllegalArgumentException("First argument must be array of length 3");

		_sim = sim;

    	_x = shooterPosition[0];
    	_y = shooterPosition[1];
    	_theta = shooterPosition[2];

    	_dx = PROJECTILE_SPEED * Math.cos(_theta);
		_dy = PROJECTILE_SPEED * Math.sin(_theta);
    }

	public synchronized void shoot(int sec, int msec) {
		double t = sec + msec * 1e-3;

		// straight motion, no change in theta
	    _x = _x + _dx * t;
	    _y = _y + _dy * t;
    }

	public synchronized double[] getPosition() {
		double[] position = new double[2];
		position[0] = _x;
		position[1] = _y;
		return position;
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

			if ((currentTime - updateTime) >= COMPLETELY_ARBITRARY_MS_INCREMENT*1e6) { // update once every 100ms

				long advanceTime = currentTime - updateTime;
				int advanceSec = (int) (advanceTime/1e9);
				int advanceMSec = (int) ((advanceTime-advanceSec*1e9)/1e6);
				shoot(advanceSec, advanceMSec);

				// reset last update time
				updateTime = System.nanoTime();

			} // end if (100ms since last update)
		} // end while (time < 100s)
	} // end run()

}
