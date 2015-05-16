/**
 * UserControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class UserControllerTest {
	
	@Test
	public void testConstructor() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 0, 0);
        Simulator sim = new Simulator();
        DisplayServer ds = new DisplayServer();

        UserController uc = new UserController(sim, gv, ds);

        assertEquals(sim, uc.getSimulator());
        assertEquals(gv, uc.getUserVehicle());
	}
	
	/**
	 * Method: getControl()
	 */
	@Test
    public void testGetControl() throws Exception {
		double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 0, 0);
        Simulator sim = new Simulator();
        DisplayServer ds = new DisplayServer();

        UserController uc = new UserController(sim, gv, ds);

        assertNotNull(uc.getControl(0, 100));
	
    public static void main(String[] args) {
        JUnitCore.main(UserControllerTest.class.getName());
    }

}
