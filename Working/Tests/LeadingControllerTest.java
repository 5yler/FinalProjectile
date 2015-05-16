/**
 * LeadingControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import static org.junit.Assert.*;
import org.junit.Test;

public class LeadingControllerTest {

	@Test
	public void testContstructor() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
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
		
	}
	
	/**
	 * Method: tooCloseToWAlls()
	 */
	@Test
	public void testTooCloseToWalls(){
		
	}
	
	/**
	 * Method: getControl()
	 */
	@Test
	public void testGetControl(){
		
	}
	
    public static void main(String[] args) {
        JUnitCore.main(LeadingControllerTest.class.getName());
    }

}
