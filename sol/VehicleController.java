import java.util.*;
import java.lang.IllegalArgumentException;

public class VehicleController extends Thread
{
    private Simulator _s;
    protected GroundVehicle _v;
    
    private int _lastCheckedTime = 0;
    private int _lastCheckedMTime = 0;

    protected static int totalNumControllers = 0;
    protected int controllerID = 0;

    // Hard-coded constraints come from documentation. Min translation 
    // speed of the vehicle is 5 m/s, max translation speed is 10 m/s, 
    // max rotational speed is PI/4. The diameter of outer circle is 50.
    private double minTransSpeed = 5;
    private double maxTransSpeed = 10;
    private double maxRotSpeed = Math.PI/4;

    private int _numSides = 5;
    private double circumCircleRadius = 25.0;

    private boolean isTurning = false;
    private boolean controllerInitialized = false;

    private double turnDuration;
    private double edgeTravelDuration;

    private double timeOfManoeuverStart;

    private static double avoidWallDist = 15;
    
    public VehicleController(Simulator s, GroundVehicle v) throws IllegalArgumentException
    {
	if (s == null) {
	    throw new IllegalArgumentException("No simulator specified.");
	}
	if (v == null) {
	    throw new IllegalArgumentException("No vehicle specified.");
	}
	_s = s;
	_v = v;

	synchronized (VehicleController.class) {
	    controllerID = totalNumControllers;
	    totalNumControllers++;
	}
    }

    public void run()
    {
	int currentTime = 0;
	int currentMTime = 0;
	
	while(currentTime < 100.0) {

	    synchronized(_s) {
		currentTime = _s.getCurrentSec();
		currentMTime = _s.getCurrentMSec();

		while (_lastCheckedTime == currentTime && _lastCheckedMTime == currentMTime) {
		    try {
			// // DEBUG
			// System.out.printf("VC %d [%d,%d] waiting\n", controllerID, currentTime, currentMTime);
			// // DEBUG
			_s.wait(); // Wait for the simulator to notify
			currentTime = _s.getCurrentSec();
			currentMTime = _s.getCurrentMSec();
		    } catch (java.lang.InterruptedException e) {
			System.err.printf("Interrupted " + e);
			System.exit(0);
		    }
		}
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] proceeding\n", controllerID, currentTime, currentMTime);
		// // DEBUG
		_s.notifyAll();
	    }

	    // // DEBUG
	    // System.out.printf("VC %d [%d,%d] generating control\n", controllerID, currentTime, currentMTime);
	    // // DEBUG
	    
	    // Generate a new control
	    Control nextControl = this.getControl(currentTime, currentMTime);

	    if (nextControl != null) {
		_v.controlVehicle(nextControl); 
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] next control applied\n", controllerID, currentTime, currentMTime);
		// // DEBUG
	    }

	    //update the time of the last control
	    _lastCheckedTime = currentTime;
	    _lastCheckedMTime = currentMTime;

	    synchronized(_s){
		if(_s.numControlToUpdate == 0 ) {
		    //this should not already be zero - something is wrong
		    System.err.println("ERROR: No of controllers to update already 0.\n");
		    System.exit(-1);
		}
		// // DEBUG
		// System.out.printf("VC %d [%d,%d] decrementing numControlToUpdate\n", controllerID, currentTime, currentMTime);
		// // DEBUG
		_s.numControlToUpdate--;
		_s.notifyAll();
	    }
	}
    }

    private void initializeController()
    {
	/* The bulk of this method is to determine how long to spend turning at
	 * each corner of the polygon, and how long to spend driving along each
	 * edge. We calculate turnDuration and edgeTravelDuration, and then use
	 * these inside getControl to decide when to switch from turning to
	 * travelling straight and back again. */ 

	/* Firstly, we know we need to turn the vehicle by PI - the internal angle
	 * of the polygon */

	double interiorAngle = Math.PI*(_numSides-2)/_numSides;
	double turningAngle = Math.PI - interiorAngle;

	/* And we're going to turn the vehicle along the circumference of the
	 * smallest circle we can make. */ 

	double minTurningRadius = minTransSpeed / maxRotSpeed;

	/* The distance we have to travel along that smallest circle is a function
	 * of the angle and the radius, and is an arc along that small circle. */
	double arcLength = turningAngle * minTurningRadius;

	/* We can work out how long each turn will take based on the arcLength and
	 * how fast we are travelling. Of course, we could also work it out based
	 * on the angle and our maximum angular velocity. */

	turnDuration = arcLength / minTransSpeed;

	// Edge length of n-polygon
	double polyEdge = 2 * circumCircleRadius * Math.cos(interiorAngle / 2);
	// Subtract by chord length spent for turns
	double edgeLength = polyEdge - 2 * (minTurningRadius * Math.tan(turningAngle/2));

	/* And we now have the amount of time to spend travelling straight along
	 * each edge. */
	edgeTravelDuration = edgeLength/maxTransSpeed;

	/* Also in method, we initialize the controller state. It's a little ugly,
	 * but we'll start as if we're half-way through a turn, and tangent to the
	 * outer circle. This makes it easy to put the vehicle on a legal part of
	 * the polyon, rather than having to drive to it. */ 
    
	isTurning = true;
	timeOfManoeuverStart = -turnDuration/2.0; 
	
	controllerInitialized = true;
    }


    public Control getControl(int sec, int msec)
    {
	double controlTime = sec+msec*1E-3;
		
	Control nextControl = null;

	if (!controllerInitialized) 
	    initializeController();

	if (isTurning) {
	    if (controlTime - timeOfManoeuverStart < turnDuration)
		nextControl = new Control(minTransSpeed, maxRotSpeed);
	    else {
		isTurning = false;
		timeOfManoeuverStart = controlTime;
		nextControl = new Control(maxTransSpeed, 0);
	    } 
	} else {
	    if (controlTime - timeOfManoeuverStart < edgeTravelDuration)
		nextControl = new Control(maxTransSpeed, 0);
	    else {
		isTurning = true;
		timeOfManoeuverStart = controlTime;
		nextControl = new Control(minTransSpeed, maxRotSpeed);
	    } 
	}

	return nextControl;
    }

    public int setNumSides(int n) {
	if (n >= 3 && n <= 10) {
	    _numSides = n;
	}
	return _numSides;
    }

    //Normalize angle 
    protected static double normalizeAngle(double theta)
    {
	double rtheta = ((theta - Math.PI) % (2 * Math.PI));			
	if (rtheta < 0) {	// Note that % in java is remainder, not modulo.
	    rtheta += 2*Math.PI;
	}
	rtheta -= Math.PI;

    	return rtheta;
    }

    protected Control avoidWalls(double[] pos) {
	if (pos[0] > 100 - avoidWallDist && pos[1] > 100 - avoidWallDist) {
	    if (pos[2] > -3 * Math.PI / 4.0) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[0] > 100 - avoidWallDist && pos[1] < 0 + avoidWallDist) {
	    if (pos[2] > 3 * Math.PI / 4.0) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[0] < 0 + avoidWallDist && pos[1] > 100 - avoidWallDist) {
	    if (pos[2] > -Math.PI / 4.0) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[0] < 0 + avoidWallDist && pos[1] < 0 + avoidWallDist) {
	    if (pos[2] > Math.PI / 4.0) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[0] > 100 - avoidWallDist) {
	    if (pos[2] > 0) {
		return new Control(5, Math.PI/4);
	    } else {
		return new Control(5, -Math.PI/4);
	    }
	}
	if (pos[0] < 0 + avoidWallDist) {
	    if (pos[2] > 0) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[1] < 0 + avoidWallDist) {
	    if (pos[2] > Math.PI / 2) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}

	if (pos[1] > 100- avoidWallDist) {
	    if (pos[2] > -Math.PI / 2) {
		return new Control(5, -Math.PI/4);
	    } else {
		return new Control(5, Math.PI/4);
	    }
	}
	return null;
    }

}