
public class Projectile {
	//class for projectiles
	//start at user vehicle angel/position and travel in a straight line
	//disappear when hit radius around target or go off screen

	public static final double PROJECTILE_SPEED = 10;

    private double _x, _y, _theta;
    private double _dx, _dy;

    private Simulator _s = null;

    public Projectile(double[] shooterPosition) {
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

}
