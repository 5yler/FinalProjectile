/**
 * UserControllerTest
 * 16.35 FinalProjectile Game Final Project
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.*;

public class UserControllerTest {

    /**
     * Constructor: UserController(Simulator sim, GroundVehicle v)
     *
     * Constructs a UserController object and tests if the
     * associated Simulator and GroundVehicle references
     * are initialized properly.
     */
	@Test
	public void testConstructor() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 0, 0);
        Simulator sim = new Simulator();

        UserController uc = new UserController(sim, gv);

        assertEquals(sim, uc.getSimulator());
        assertEquals(gv, uc.getUserVehicle());
	}

    /**
     * Constructor: UserController(Simulator sim, GroundVehicle v)
     *
     * Constructs two UserController objects and tests if
     * they are assigned unique integer user IDs in increasing value
     */
    @Test
    public void testUserID() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv1 = new GroundVehicle(startPosition, 0, 0);
        GroundVehicle gv2 = new GroundVehicle(startPosition, 0, 0);
        Simulator sim = new Simulator();

        int expectedID1 = UserController.getUserControllerCount();
        int expectedID2 = expectedID1 + 1;

        System.out.println("expectedID1: " + expectedID1);
        System.out.println("expectedID2: " + expectedID2);

        UserController uc1 = new UserController(sim, gv1);

        System.out.println("uc1.getUserID(): " + uc1.getUserID());
        assertEquals(expectedID1, uc1.getUserID());

        UserController uc2 = new UserController(sim, gv2);
        System.out.println("uc2.getUserID(): " + uc2.getUserID());

        assertEquals(expectedID2, uc2.getUserID());
    }



    /**
     * Constructor: UserController(Simulator sim, GroundVehicle v, DisplayServer ds)
     *
     * Constructs a UserController object and tests if the
     * associated Simulator, GroundVehicle and DisplayServer
     * references are initialized properly.
     */
    @Test
    public void testConstructor2() {

        try {
            ServerSocket s = new ServerSocket(5065);
            s.setReuseAddress(true);
            if (!s.isBound()) {System.exit(-1);}
            String address = GeneralInetAddress.getLocalHost().getHostAddress();


            // create local DisplayServer and DisplayClient
            DisplayServer ds = new DisplayServer(address);
            DisplayClient dc = new DisplayClient(address);

            // create Simulator
            Simulator sim = new Simulator(dc);

            // create vehicle
            double[] startPosition = {50, 30, 0};
            GroundVehicle gv = new GroundVehicle(startPosition, 0, 0);

            UserController uc = new UserController(sim, gv, ds);

            assertEquals(sim, uc.getSimulator());
            assertEquals(gv, uc.getUserVehicle());
            assertEquals(ds, uc.getDisplayServer());

        } catch (IOException e) {
            System.err.println("IOException in UserControllerTest!");
            e.printStackTrace();
        }
    }

	/**
	 * Method: getControl()
	 */
	@Test
    public void testGetControl() throws Exception {

        try {
            ServerSocket s = new ServerSocket(5066);
            s.setReuseAddress(true);
            if (!s.isBound()) {System.exit(-1);}
            String address = GeneralInetAddress.getLocalHost().getHostAddress();


            // create local DisplayServer and DisplayClient
            DisplayServer ds = new DisplayServer(address);
            DisplayClient dc = new DisplayClient(address);

            // create Simulator
            Simulator sim = new Simulator(dc);

            // create vehicle
            double[] startPosition = {50, 30, 0};
            GroundVehicle gv = new GroundVehicle(startPosition, 0, 0);

            UserController uc = new UserController(sim, gv, ds);

            Control userControl = uc.getControl(1, 0);

            assertNotNull(userControl);

            // test if control omega is 0
            assertEquals(0, userControl.getRotVel(), 1e-6);

            // test if control speed is equal to initial user vehicle velocity
            assertEquals(5*GroundVehicle.MIN_VEL, userControl.getSpeed(), 1e-6);


        } catch (IOException e) {
            System.err.println("IOException in UserControllerTest!");
            e.printStackTrace();
        }
    }
	
    public static void main(String[] args) {
        JUnitCore.main(UserControllerTest.class.getName());
    }

}
