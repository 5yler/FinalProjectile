import java.util.*;
import java.lang.IllegalArgumentException;

public class FollowingController extends VehicleController
{
    private GroundVehicle leadergv;
    
    public FollowingController(Simulator s,  GroundVehicle followingVehicle, GroundVehicle targetVehicle)
	throws IllegalArgumentException
    {	
	super(s,followingVehicle);
	if(targetVehicle ==null){
	    throw new IllegalArgumentException("Null vehicle Reference");
	}
	this.leadergv = targetVehicle;
    }

    public Control getControl(int sec, int msec) {
	// // DEBUG
	// System.out.printf("VC %d [%d,%d] get following control, vehicle: %d\n",
	// 		  controllerID, sec, msec, _v.getVehicleID());
	// // DEBUG
	double desiredOmega;
	double[] leaderPos;
	double[] myPos;

	leaderPos = leadergv.getPosition();
	myPos = this._v.getPosition();

	//heading of the leading vehicle in global reference

	double xDiff = leaderPos[0] - myPos[0];
	double yDiff = leaderPos[1] - myPos[1];
	double desiredTheta = Math.atan(yDiff / xDiff);

	if (xDiff < 0) {
	    desiredTheta += Math.PI;
	}

	double gain = 5;

	//needed change in angle 
	desiredTheta = normalizeAngle(desiredTheta);
	desiredOmega = normalizeAngle(desiredTheta - myPos[2]);

	desiredOmega *= gain;

	if (desiredOmega > Math.PI/4) {
	    desiredOmega = Math.PI/4;
	}
	if (desiredOmega < -Math.PI/4) {
	    desiredOmega = -Math.PI/4;
	}

	double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	double desiredSpeed = distance;
	if (desiredSpeed > 10) {
	    desiredSpeed = 10;
	}
	if (desiredSpeed < 5) {
	    desiredSpeed = 5;
	}

	Control c = new Control(desiredSpeed, desiredOmega);
	return c;
    }

       
}