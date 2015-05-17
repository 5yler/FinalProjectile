/**
 * FollowingControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class FollowingControllerTest {

	@Test
	public void testContructor() {
        double[] startPosition = {50, 30, 0};
        double[] startPosition2 = {30, 10, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        GroundVehicle gv2 = new GroundVehicle(startPosition2, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        FollowingController fc = new FollowingController(sim, gv, gv2);

        assertEquals(sim, fc.getSimulator());
        assertEquals(gv, fc.getGroundVehicle());
        assertEquals(gv2, fc.getPrey());
	}


	/**
	 * Method: getControl()
	 */
	@Test
    public void testGetControl() throws Exception {
        double[] startPosition = {50, 30, 0};
        double[] startPosition2 = {30, 10, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        GroundVehicle gv2 = new GroundVehicle(startPosition2, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        FollowingController fc = new FollowingController(sim, gv, gv2);

        assertNotNull(fc.getControl(0, 100));


        // test speed when preyDistance > FOLLOWING_DISTANCE
        
        Control newControl = fc.getControl(1,0);
        
        double speed = newControl.getSpeed();
        
        assertEquals(speed,FollowingController.FOLLOWING_MAX_VEL,1E-6);

        // test speed when preyDistance < FOLLOWING_DISTANCE
        
        double followingDistance = 50 + FOLLOWING_DISTANCE/2;
        double[] startPosition3 = {followingDistance, 30, 0};
        GroundVehicle gv3 = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        
        FollowingController fc2 = new FollowingController(sim, gv, gv3);
        
        Control newControl2 = fc2.getControl(1,0);
        
        double speed2 = newControl2.getSpeed();
        
        assertEquals(speed2, 5.0, 1E-6);


    }
	
    public static void main(String[] args) {
        JUnitCore.main(FollowingControllerTest.class.getName());
    }

}
