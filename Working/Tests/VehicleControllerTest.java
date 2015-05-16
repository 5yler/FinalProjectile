/**
 * VehicleConrollerTest
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.Assert.assertEquals;

public class VehicleControllerTest extends TestCase {

	//TODO: update with changes

    /**
     * Method: VehicleController(Simulator s, GroundVehicle v) constructor
     */
    @Test
    public void testConstructor() {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        assertEquals(sim, vc.getSimulator());
        assertEquals(gv, vc.getGroundVehicle());
    }

    /**
     *
     * Method: VehicleController(Simulator s, GroundVehicle v) constructor
     *
     * This method tests if newly created VehicleControllers get assigned
     * number IDs increasing in value.
     */
    @Test
    public void testIncreasingID() throws Exception {

        Simulator sim = new Simulator();
        int nVehicles = 5;
        double[] pos = { 0, 0, 0 };

        int expectedID = VehicleController.getControllerCount();

        // create 5 ground vehicles
        for (int i = 0; i < nVehicles; i++) {
            GroundVehicle gv = new GroundVehicle(pos, 1, 0, 1);
            VehicleController vc;
            if (i == 0) {
                // the first vehicle should use a RandomController
                vc = new RandomController(sim, gv);
            } else {
                // the other vehicles should use FollowingController to follow the first vehicle
                GroundVehicle target = sim.getVehicle(0);
                vc = new FollowingController(sim, gv, target);
            }
            sim.addVehicle(gv);
            gv.start();
            vc.start();

            // check if the number IDs of the VehicleControllers
            // correspond to the order they were created in
            assertEquals(expectedID, vc.getID());
            expectedID++;
        }
    }


    /**
     * Method: getControllerCount()
     *
     * Test if static variable controllerCount increases when new
     * VehicleControllers are created.
     */
    @Test
    public void testControllerCount() {

        int controllerCountBefore = VehicleController.getControllerCount();

        // create controller
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        // check if controllerCount increased to 1
        int expectedControllerCount = controllerCountBefore + 1;
        int controllerCount = VehicleController.getControllerCount();
        assertEquals(expectedControllerCount, controllerCount);

        VehicleController vc2 = new VehicleController(sim, gv);

        // check if controllerCount increased to 2
        expectedControllerCount++;
        controllerCount = VehicleController.getControllerCount();
        assertEquals(expectedControllerCount, controllerCount);

        VehicleController vc3 = new VehicleController(sim, gv);

        // check if controllerCount increased to 3
        expectedControllerCount++;
        controllerCount = VehicleController.getControllerCount();
        assertEquals(expectedControllerCount, controllerCount);

    }


    /**
     * Method: setNumSides(int n)
     * Method: getNumSides()
     *
     * Test get/set _numSides for valid input
     */
    @Test
    public void testGetSetNumSidesValid() throws Exception {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        // default value is 5
        assertEquals(5, vc.getNumSides());
        vc.setNumSides(4);
        assertEquals(4, vc.getNumSides());

        // test at legal bounds
        vc.setNumSides(3);
        assertEquals(3, vc.getNumSides());
        vc.setNumSides(10);
        assertEquals(10, vc.getNumSides());
    }

    /**
     * Method: setNumSides(int n)
     * Method: getNumSides()
     *
     * Test get/set _numSides for invalid input
     */
    @Test
    public void testGetSetNumSidesInvalid() throws Exception {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        assertEquals(5, vc.getNumSides());
        // _numSides does not change for invalid inputs
        vc.setNumSides(2);
        assertEquals(5, vc.getNumSides());
        vc.setNumSides(11);
        assertEquals(5, vc.getNumSides());
    }

    /**
     * Method: getControl(int sec, int msec)
     */
    @Test
    public void testGetControl() throws Exception {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        assertNotNull(vc.getControl(0, 100));

        //TODO: test if proper controls get returned when turning or not?
    }

    /**
     * Method: run()
     */
    @Test
    public void testRun() throws Exception {
        //TODO: test if IllegalStateException gets thrown if Simulator _vehicleControlQueue < 0
        //TODO: test if Simulator _vehicleControlQueue gets decremented
    }


    /**
     * Method: initializeController()
     */
    @Test
    public void testInitializeController() throws Exception {
        double[] startPosition = {50, 30, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4); // star-shaped
        Simulator sim = new Simulator();

        VehicleController vc = new VehicleController(sim, gv);

        Boolean controllerInitialized = (Boolean) getField( vc, "controllerInitialized" );
        Boolean isTurning = (Boolean) getField( vc, "isTurning" );
        Double turnDuration	= (Double) getField( vc, "turnDuration" );
        System.out.print(' '+turnDuration);
        Double edgeTravelDuration	= (Double) getField( vc, "edgeTravelDuration" );
        Double timeOfManoeuverStart	= (Double) getField( vc, "timeOfManoeuverStart" );

        Boolean expectIsTurning = false;
        Boolean expectInitialized = false;

        // check if these boolean expressions are false before controller is initialized
        assertEquals(expectIsTurning, isTurning);
        assertEquals(expectInitialized, controllerInitialized);

        // check if fields are initialized to zero
        assertEquals(0, turnDuration, 1e-6);
        assertEquals(0, edgeTravelDuration, 1e-6);
        assertEquals(0, timeOfManoeuverStart, 1e-6);


        Object[] params = {};
        executeMethod(vc, "initializeController", params);

        controllerInitialized = (Boolean) getField( vc, "controllerInitialized" );
        isTurning = (Boolean) getField( vc, "isTurning" );
        turnDuration	= (Double) getField( vc, "turnDuration" );
        edgeTravelDuration	= (Double) getField( vc, "edgeTravelDuration" );
        timeOfManoeuverStart	= (Double) getField( vc, "timeOfManoeuverStart" );


        Boolean expectIsTurningAfter = true;
        Boolean expectInitializedAfter = true;

        assertEquals(expectIsTurningAfter, isTurning);
        assertEquals(expectInitializedAfter, controllerInitialized);

        // check if fields are set to nonzero values after
        // initializeController() is called
        assertTrue(turnDuration > 0);
        assertTrue(edgeTravelDuration > 0);
        assertTrue(timeOfManoeuverStart < 0);


    }

    /**
     * Gets the field value from an instance, even if the field is private or
     * protected.
     */
    private Object getField(Object instance, String fieldName) throws Exception {
        Class cl = instance.getClass();
        Field field = cl.getDeclaredField(fieldName);

        // make field accessible if private
        field.setAccessible(true);

        // return field value in instance
        return field.get(instance);
    }

    /**
     * Executes a method on an object instance. The method name is specified
     * with a string and the method parameters are passed as an array of Objects.
     * The return value of the method is returned even if the method is private
     * or protected.
     */
    private Object executeMethod(Object instance, String methodName, Object[] methodParams) throws Exception {
        Class c = instance.getClass();

        // get Class types of all method parameters
        Class[] paramTypes = new Class[methodParams.length];

        for (int i = 0; i < methodParams.length; i++) {
            paramTypes[i] = methodParams[i].getClass();
        }

        Method method = c.getDeclaredMethod(methodName, paramTypes);

        // set the method to be accessible
        method.setAccessible(true);

        return method.invoke(instance, methodParams);
    }

    public static void main(String[] args) {
        JUnitCore.main(VehicleControllerTest.class.getName());
    }
}
