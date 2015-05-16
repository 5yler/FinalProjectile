/**
 * UserControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import static org.junit.Assert.fail;
import org.junit.Test;

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
	public void testGetControl() {
		
	}
	
    public static void main(String[] args) {
        JUnitCore.main(UserControllerTest.class.getName());
    }

}
