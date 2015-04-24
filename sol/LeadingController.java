import java.util.ArrayList;

public class LeadingController extends VehicleController {

    private ArrayList<GroundVehicle> gvList;

    public LeadingController(Simulator s, GroundVehicle v) {
	super(s, v);
	gvList = new ArrayList<GroundVehicle>();
    }

    /**
     * Adds a GroundVehicle to the list of chasers considered by this
     * groundVehicle.
     * 
     * @param v
     */
    public void addFollower(GroundVehicle v) {
	gvList.add(v);
    }

    /**
     * Returns a control negating the output for the FollowingControler. Added
     * special controls when the GroundVehicle is close to the walls.
     */
    public Control getControl(int sec, int msec) {
	GroundVehicle closestVehicle = this.getClosestVehicle();

	//if no closest vehicle return null
	if(closestVehicle == null)
	    return null;

	double desiredOmega = 0;
	double[] chaserPos;
	double[] myPos;

	chaserPos = closestVehicle.getPosition(); /* Shared Resource */

	myPos = _v.getPosition(); /* Shared Resource */

	/*Attempt to get more than one lock - uncomment below to see exception thrown*/

	/*
	GroundVehicle leadVehicle = _v;
	System.out.printf("---Attempted---\n");
	synchronized(closestVehicle){
	    synchronized(leadVehicle){//should throw exception 
		System.out.printf("---BothLock---\n");
		closestVehicle.getPosition();
		leadVehicle.getPosition();
	    }
	}
	*/

	double xDiff = chaserPos[0] - myPos[0];
	double yDiff = chaserPos[1] - myPos[1];
	double targetTheta = Math.atan2(yDiff, xDiff);
	
	// desiredTheta = normalizeAngle(desiredTheta);
	desiredOmega = normalizeAngle(targetTheta - myPos[2] + Math.PI);

	double gain = 5;
	desiredOmega *= gain;
	if (desiredOmega > Math.PI/4) {
	    desiredOmega = Math.PI/4;
	}
	if (desiredOmega < -Math.PI/4) {
	    desiredOmega = -Math.PI/4;
	}

	double desiredSpeed = 10;

	// Wall cases and corner cases.
	Control a = avoidWalls(myPos);
	if (a != null)
	    return a;

	Control c = new Control(desiredSpeed, desiredOmega);
	return c;
    }

    /**
     * @return the closest GroundVehicle to the leader
     */
    public GroundVehicle getClosestVehicle() {

	double[] leaderPosition = _v.getPosition();
	double closestDistance = Double.MAX_VALUE;
	GroundVehicle closestVehicle = null;
	for (GroundVehicle v : gvList) {
	    double[] followerPosition = v.getPosition();
	    double xDistance = leaderPosition[0] - followerPosition[0];
	    double yDistance = leaderPosition[1] - followerPosition[1];
	    double totalDistance = Math.hypot(xDistance, yDistance);
	    if (totalDistance < closestDistance) {
		closestDistance = totalDistance;
		closestVehicle = v;
	    }
	}
	assert (closestVehicle != null);
	return closestVehicle;
    }
}
