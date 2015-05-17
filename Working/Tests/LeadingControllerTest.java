/**
 * LeadingControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class LeadingControllerTest {

	@Test
	public void testContstructor() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);

        assertEquals(sim, lc.getSimulator());
        assertEquals(gv, lc.getGroundVehicle());
	}
	
	/**
	 * Method: getClosestFollower()
	 */
	@Test
	public void testGetClosestFollower(){
		double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);

        double[] pos2 = {50, 40, 0};
        double[] pos3 = {100, 100, 0};
        GroundVehicle gv2 = new GroundVehicle(pos2, 5.0, Math.PI / 4);
        GroundVehicle gv3 = new GroundVehicle(pos3, 5.0, Math.PI / 4);
        sim.addVehicle(gv2);
        sim.addVehicle(gv3);
        
        GroundVehicle closest = lc.getClosestFollower();
        
        assertEquals(closest,gv2);
	}

	
	/**
	 * Method: tooCloseToWAlls()
	 */
	@Test
	public void testTooCloseToWalls(){
		double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);
	}
	
	/**
	 * Method: getControl()
	 */
	@Test
    public void testGetControl() throws Exception {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);

        assertNotNull(lc.getControl(0, 100));
    }
	
    public static void main(String[] args) {
        JUnitCore.main(LeadingControllerTest.class.getName());
    }

}
