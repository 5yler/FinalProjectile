/**
 * SimulatorTest
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class SimulatorTest {

	//TODO: update with changes

    /**
     * Method: Simulator()
     *
     * Constructs a Simulator Object and tests if the values returned by the
     * functions "getCurrentMSec()" and "getCurrentSec()" in the Simulator
     * class are 0 before method "run()" is called and the value returned by
     * "getDisplayClient()" is null.
     */
    @Test
    public void constructor() {

        Simulator sim = new Simulator();

        assertTrue(sim.getCurrentSec() == 0);
        assertTrue(sim.getCurrentMSec() == 0);
        assertNull(sim.getDisplayClient());
    }

    /**
     * Method: Simulator(DisplayClient dc)
     *
     * Constructs a Simulator Object and tests if the values returned by the
     * functions "getCurrentMSec()" and "getCurrentSec()" in the Simulator
     * class are 0 before method "run()" is called and the value returned by
     * "getDisplayClient()" is not null.
     */
    @Test
    public void constructor2() {

        DisplayClient dc = new DisplayClient("127.0.0.1");
        Simulator sim = new Simulator(dc);

        assertTrue(sim.getCurrentSec() == 0);
        assertTrue(sim.getCurrentMSec() == 0);
        assertNotNull(sim.getDisplayClient());
    }

    /**
     *
     * Method: getVehicle(int index)
     *
     * This method tests if the getVehicle method returns the correct GroundVehicles,
     * or throws IndexOutOfBoundsException for inappropriate input.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void testGetVehicle() throws Exception {

        Simulator sim = new Simulator();

        double[] pos = { 0, 0, 0 };
        GroundVehicle gv = new GroundVehicle(pos, 1, 0, 1);
        GroundVehicle gv2 = new GroundVehicle(pos, 1, 1, 1);

        // add GroundVehicles
        sim.addVehicle(gv);
        sim.addVehicle(gv2);

        // check if the vehicles get returned correctly
        assertEquals(gv, sim.getVehicle(0));
        assertEquals(gv2, sim.getVehicle(1));

        // check if getVehicle throws exception when inappropriate index used
        sim.getVehicle(2);
    }

    /**
     *
     * Method: addVehicle(GroundVehicle gv)
     *
     * This method tests if the "addVehicle()" method adds the
     * GroundVehicles to the list correctly.
     */
    @Test
    public void testAddVehicle() throws Exception {

        Simulator sim = new Simulator();

        double[] pos = { 0, 0, 0 };
        GroundVehicle gv = new GroundVehicle(pos, 1, 0, 1);
        GroundVehicle gv2 = new GroundVehicle(pos, 1, 1, 1);

        // add GroundVehicles
        sim.addVehicle(gv);
        sim.addVehicle(gv2);

        // check if the added vehicles get returned correctly
        assertEquals(gv, sim.getVehicle(0));
        assertEquals(gv2, sim.getVehicle(1));
    }

    /**
     *
     * Method: getControlQueueSize()
     *
     * Constructs a Simulator Object and tests if the value returned by the
     * function "getControlQueueSize()" in the Simulator class is 0 before method
     * "run()" is called.
     */
    @Test
    public void testStartingControlQueueSize() throws Exception {

        Simulator sim = new Simulator();
        assertEquals(0, sim.getControlQueueSize(), 1e-9);
    }

    /**
     *
     * Method: addVehicle(GroundVehicle gv)
     *
     * Constructs a Simulator Object and tests if the value returned by the
     * function "getControlQueueSize()" increments appropriately when
     * GroundVehicles are added to the simulator.
     */
    @Test
    public void testAddVehicleIncrementsControlQueueSize() throws Exception {

        Simulator sim = new Simulator();

        double[] pos = { 0, 0, 0 };
        GroundVehicle gv = new GroundVehicle(pos, 1, 0, 1);
        GroundVehicle gv2 = new GroundVehicle(pos, 1, 1, 1);

        sim.addVehicle(gv);
        assertEquals(1, sim.getControlQueueSize(), 1e-9);
        sim.addVehicle(gv2);
        assertEquals(2, sim.getControlQueueSize(), 1e-9);
    }

    /**
     *
     * Method: addVehicle(GroundVehicle gv)
     *
     * This method tests if vehicles added with the "addVehicle()"
     * method get assigned number IDs increasing in value.
     */
    @Test
    public void testAddVehicleIncreasingID() throws Exception {

        Simulator sim = new Simulator();

        double[] pos = { 0, 0, 0 };

        // add GroundVehicles
        int nVehicles = 5;
        for (int i = 0; i <= nVehicles; i++) {
            GroundVehicle gv = new GroundVehicle(pos, 1, 0, 1);
            sim.addVehicle(gv);

            // check if the number IDs of the vehicles correspond
            // to the order they were created in
            assertEquals(i+1, gv.getNumID());
        }
    }

    /**
     *
     * Method: getCurrentSec()
     *
     * Constructs a Simulator Object and tests if the value returned by the
     * function "getCurrentSec()" in the Simulator class is 0 before method
     * "run()" is called.
     */
    @Test
    public void testGetCurrentSec() throws Exception {

        Simulator sim = new Simulator();
        assertEquals(0, sim.getCurrentSec(), 1e-9);
    }

    /**
     *
     * Method: getCurrentMSec()
     *
     * Constructs a Simulator Object and tests if the value returned by the
     * function "getCurrentMSec()" in the Simulator class is 0 before method
     * "run()" is called.
     */
    @Test
    public void testGetCurrentMSec() throws Exception {

        Simulator sim = new Simulator();
        assertEquals(0, sim.getCurrentMSec(), 1e-9);
    }

    /**
     *
     * Method: advanceClock()
     *
     * Tests if the "advanceClock()" updates the simulator clock increasing it
     * by SIMULATOR_MSEC_INCREMENT milliseconds.
     */
    @Test
    public void testAdvanceClock() throws Exception {

        Simulator sim = new Simulator();
        sim.advanceClock();

        // test if clock is increased by appropriate increment when clock is advanced
        double actualTime = sim.getCurrentSec() + sim.getCurrentMSec()/1e3;
        double expectedTime = Simulator.SIMULATOR_MSEC_INCREMENT/1e3;
        assertEquals(expectedTime, actualTime, 1e-9);
    }

    /**
     *
     * Method: advanceClock()
     *
     * Test if variable sec is increased by 1 and msec = 0 when calling
     * "increaseTime()" makes the variable sec = 1
     */
    @Test
    public void testAdvanceClockFullSecond() throws Exception {

        Simulator sim = new Simulator();

        // define how many times clock needs to be advanced
        int nIncrements = 1000/Simulator.SIMULATOR_MSEC_INCREMENT;

        for (int i = 0; i < nIncrements; i++) {
            sim.advanceClock();
        }

        assertTrue(sim.getCurrentSec() == 1);
        assertTrue(sim.getCurrentMSec() == 0);
    }

    /**
     *
     * Method: randomStartingPosition()
     *
     * Test if method returns starting values within allowed intervals.
     */
    @Test
    public void testRandomStartingPosition() throws Exception {

        double minXY = 0;
        double maxXY = 100;
        double minTheta = -Math.PI; // inclusive
        double maxTheta = Math.PI;  // non-inclusive

        // generate 10 000 starting positions to make sure random starting values fall in correct ranges
        for (int i = 0; i < 10000; i++) {

            double[] startingPosition = Simulator.randomStartingPosition();
            double startX = startingPosition[0];
            double startY = startingPosition[1];
            double startTheta = startingPosition[2];

            // check if starting X and Y values fall between allowable min and max
            assertTrue(startX <= maxXY);
            assertTrue(startX >= minXY);
            assertTrue(startY <= maxXY);
            assertTrue(startY >= minXY);

            // check if starting theta values fall in allowed range
            assertTrue("Theta: "+startTheta,startTheta >= minTheta);
            assertTrue("Theta: "+startTheta,startTheta < maxTheta);
        }
    }

    public static void main(String[] args){

        JUnitCore.main(TestSimulator.class.getName());
    }

} 