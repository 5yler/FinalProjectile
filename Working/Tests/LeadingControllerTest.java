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
	 * Method: tooCloseToWalls()
	 */
	@Test
	public void testTooCloseToWallsX(){
		
        // test at low x values
		
		double startX = LeadingController.DANGER_ZONE/2;
		double[] startPosition = {startX, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);
        
        boolean closeToWall = lc.tooCloseToWalls(gv.getPosition());
        
        assertEquals(closeToWall,true);


        // test at high x values
        
        double startX2 = Simulator.SIM_X - LeadingController.DANGER_ZONE/2;
        double[] startPosition = {startX2, 30, 0};
        GroundVehicle gv2 = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim2 = new Simulator();

        LeadingController lc2 = new LeadingController(sim2, gv2);
        boolean closeToWall2 = lc2.tooCloseToWalls(gv2.getPosition());
        
        assertEquals(closeToWall2,true);


    }

    /**
     * Method: tooCloseToWalls()
     */
    @Test
    public void testTooCloseToWallsY(){

        // test at low y values
    	
		double startY = LeadingController.DANGER_ZONE/2;
		double[] startPosition = {startY, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);
        
        boolean closeToWall = lc.tooCloseToWalls(gv.getPosition());
        
        assertEquals(closeToWall,true);

        // test at high y values
        
        double startY2 = Simulator.SIM_Y - LeadingController.DANGER_ZONE/2;
        double[] startPosition = {startY2, 30, 0};
        GroundVehicle gv2 = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim2 = new Simulator();

        LeadingController lc2 = new LeadingController(sim2, gv2);
        boolean closeToWall2 = lc2.tooCloseToWalls(gv2.getPosition());
        
        assertEquals(closeToWall2,true);
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
