import java.lang.IllegalArgumentException;
import static org.junit.Assert.*;
import java.lang.Object;
import java.util.Random;
// import java.util.concurrent.locks.ReentrantLock;

public class GroundVehicle extends Thread
{
    private double _x, _y, _theta;
    private double _dx, _dy, _dtheta;

    private static int totalNumVehicles = 0;
    private int vehicleID;

    private Simulator _s = null;

    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    private Random r;

    // This lock is needed when you try resource-hierarchy solution
    // for deadlock prevention
    // private ReentrantLock mygvLock;

    public GroundVehicle (double pose[], double s, double omega)
    {
	if (pose.length != 3)
	    throw new IllegalArgumentException("newPos must be of length 3");

	synchronized (GroundVehicle.class) {
	    vehicleID = totalNumVehicles;
	    totalNumVehicles++;
	}

	_x = pose[0]; 
	_y = pose[1]; 
	_theta = pose[2];

	_dx = s * Math.cos(_theta);
	_dy = s * Math.sin(_theta);
	_dtheta = omega;
    
	clampPosition();
	clampVelocity();

	r = new Random();
    }

    public void addSimulator(Simulator sim)
    {
	_s = sim;
    }

    public int getVehicleID()
    {
	return vehicleID;
    }

    private void clampPosition() {
	_x = Math.min(Math.max(_x,0),100);
	_y = Math.min(Math.max(_y,0),100);
	_theta = Math.min(Math.max(_theta, -Math.PI), Math.PI);
	if (_theta - Math.PI == 0 || Math.abs(_theta - Math.PI) < 1e-6)
	    _theta = -Math.PI;
    }

    private void clampVelocity() {

	double velMagnitude = Math.sqrt(_dx*_dx+_dy*_dy);
	if (velMagnitude > 10.0) {
	    /* Note: 
	 
	       I could also implement this as 

	       double direction = atan2(_dy, _dx);
	       _dx = 10.0 * cos(direction);
	       _dy = 10.0 * sin(direction);

	       but since 
	       cos(direction) = _dx/velMagnitude;
	       sin(direction) = _dy/velMagnitude; 
	 
	       I can save myself an atan2, a cos and a sin, in exchange for two
	       extra divisions. atan2, cos and sin are very expensive
	       computationally. 

	    */ 

	    _dx = 10.0 * _dx/velMagnitude;
	    _dy = 10.0 * _dy/velMagnitude;
	}

	if (velMagnitude < 5.0) {
	    /* Same logic as above. */ 

	    _dx = 5.0 * _dx/velMagnitude;
	    _dy = 5.0 * _dy/velMagnitude;
	}

	_dtheta = Math.min(Math.max(_dtheta, -Math.PI/4), Math.PI/4);		
    }

    private boolean checkIfNoLock() {
	if (_s == null) {
	    return false;
	}
	try {
	    return DeadlockTester.testLock(this, _s);
	} catch (DeadlockTesterException e) {
	    e.printStackTrace();
	    Runtime.getRuntime().exit(1);
	}
	return false;
    }

    public double [] getPosition() {
	double[] position = new double[3];
	if (checkIfNoLock()) {
	    synchronized(this) {
		position[0] = _x;
		position[1] = _y;
		position[2] = _theta;
    
		return position;
	    }
	}
	return position;
    }

    public double [] getVelocity() {
	double[] velocity = new double[3];
	if (checkIfNoLock()) {
	    synchronized(this) {
		velocity[0] = _dx;
		velocity[1] = _dy;
		velocity[2] = _dtheta;
		
		return velocity;
	    }
	}
	return velocity;	
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

    public synchronized void controlVehicle(Control c) {
	_dx = c.getSpeed() * Math.cos(_theta);
	_dy = c.getSpeed() * Math.sin(_theta);
	_dtheta = c.getRotVel();

	clampVelocity();
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100.0){
	    synchronized(_s){
		currentTime = _s.getCurrentSec();
		currentMTime = _s.getCurrentMSec();

		while(_lastCheckedTime == currentTime && _lastCheckedMTime == currentMTime){
		    try{
			// // DEBUG
			// System.out.printf("GV %d [%d,%d] waiting\n", vehicleID, currentTime, currentMTime);
			// // DEBUG
			_s.wait();
			currentTime = _s.getCurrentSec();
			currentMTime = _s.getCurrentMSec();
		    }
		    catch(java.lang.InterruptedException e){
			System.err.printf("Interupted " + e);
		    }
		}
		// // DEBUG
		// System.out.printf("GV %d [%d,%d] proceeding\n", vehicleID, currentTime, currentMTime);
		// // DEBUG
		_s.notifyAll();
	    }

	    // // DEBUG
	    // System.out.printf("GV %d [%d,%d] advancing\n", vehicleID, currentTime, currentMTime);
	    // // DEBUG

	    // advance(currentTime - _lastCheckedTime, 
	    // 	    currentMTime - _lastCheckedMTime);

	    advanceNoiseFree(currentTime - _lastCheckedTime, 
	                  currentMTime - _lastCheckedMTime);

	    _lastCheckedTime = currentTime;
	    _lastCheckedMTime = currentMTime;

	    synchronized(_s){
		if(_s.numVehicleToUpdate == 0) {
		    //this should not already be zero - something is wrong
		    System.err.println("ERROR: No of vehicles to update already 0\n");
		    System.exit(-1);
		}
		// // DEBUG
		// System.out.printf("GV %d [%d,%d] decrementing numVehicleToUpdate\n", vehicleID, currentTime, currentMTime);
		// // DEBUG
		_s.numVehicleToUpdate--;
		_s.notifyAll();
	    }	
	}

    }

    public static double normalizeAngle(double theta)
    {
	double rtheta = ((theta - Math.PI) % (2 * Math.PI));
	if (rtheta < 0) {	// Note that % in java is remainder, not modulo.
	    rtheta += 2*Math.PI;
	}
	return rtheta - Math.PI;
    }

    public synchronized void advance(int sec, int msec)
    {
	double t = sec + msec * 1e-3;

	double[] newPose = new double[3];
	double errc = Math.sqrt(0.2) * r.nextGaussian();
	double errd = Math.sqrt(0.1) * r.nextGaussian();

	newPose[0] = _x + _dx * t + errd * Math.cos(_theta) - errc * Math.sin(_theta);
	newPose[1] = _y + _dy * t + errd * Math.sin(_theta) + errc * Math.cos(_theta);
	newPose[2] = _theta + _dtheta * t;
	newPose[2] = normalizeAngle(newPose[2]);

	double[] newVel = new double[3];
	double s = Math.sqrt(Math.pow(_dx, 2) + Math.pow(_dy, 2));
	newVel[0] = s * Math.cos(_theta);
	newVel[1] = s * Math.sin(_theta);
	newVel[2] = _dtheta;

	setPosition(newPose);
	setVelocity(newVel);
    }    
   
    public synchronized void advanceNoiseFree(int sec, int msec)
    {
	double t = sec + msec * 1e-3;

	// // Linear approximation model
	// _x = _x + _dx*t;
	// _y = _y + _dy*t;
	// _theta = (_theta + _dtheta*t);

	// if (_theta < -Math.PI)
	//   _theta += 2*Math.PI;
	// if (_theta >= Math.PI)
	//   _theta -= 2*Math.PI;

	// // If _dtheta is non-zero, we just turned and so we need to update our
	// // velocity vector. We could keep _dtheta.
	  
	// double s = Math.sqrt((_dx)*(_dx) +(_dy)*(_dy));
	// _dx = s*Math.cos(_theta);
	// _dy = s*Math.sin(_theta);
	// _dtheta = _dtheta;
    
	// Curve model
	// Assuming that _dx, _dy, and _dtheta was set beforehand by controlVehicle()
	double s = Math.sqrt( _dx * _dx + _dy * _dy );

	if (Math.abs(_dtheta) > 1e-3) { // The following model is not well defined when _dtheta = 0
	    // Circle center and radius
	    double r = s/_dtheta;

	    double xc = _x - r * Math.sin(_theta);
	    double yc = _y + r * Math.cos(_theta);

	    _theta = _theta + _dtheta * t;

	    double rtheta = ((_theta - Math.PI) % (2 * Math.PI));
	    if (rtheta < 0) {	// Note that % in java is remainder, not modulo.
		rtheta += 2*Math.PI;
	    }
	    _theta = rtheta - Math.PI;

	    // Update    
	    _x = xc + r * Math.sin(_theta);
	    _y = yc - r * Math.cos(_theta);
	    _dx = s * Math.cos(_theta);
	    _dy = s * Math.sin(_theta);

	} else {			// Straight motion. No change in theta.
	    _x = _x + _dx * t;
	    _y = _y + _dy * t;
	}

	clampPosition();
	clampVelocity();
    }

    // The following three methods (getVehicleLock, compareId,
    // reverseCompareId) is is needed when you try resource-hierarchy
    // solution for deadlock prevention

    // public ReentrantLock getVehicleLock() {
    // 	return this.mygvLock;
    // }

    // public int compareId(GroundVehicle gv) {
    // 	if (getVehicleID() < gv.getVehicleID())
    // 	    return -1;
    // 	else if (getVehicleID() > getVehicleID())
    // 	    return 1;
    // 	else
    // 	    return 0;
    // }

    // public int reverseCompareId(GroundVehicle gv) {
    // 	return -compareId(gv);
    // }    
}
