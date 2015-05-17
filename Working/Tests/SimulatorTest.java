/**
 * SimulatorTest
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;


public class SimulatorTest {

	//TODO: update with changes

    /**
     * Method: Simulator()
     *
     * Constructs a Simulator Object and tests if the value of STARTUP_TIME
     * in the Simulator class is 0 before method "run()" is called and the
     * value returned by "getDisplayClient()" is null.
     */
    @Test
    public void constructor() {

        Simulator sim = new Simulator();

        // test DisplayClient reference
        assertNull(sim.getDisplayClient());

        // test STARTUP_TIME value
        assertEquals(0,sim.STARTUP_TIME);
    }

    /**
     * Method: Simulator(DisplayClient dc)
     *
     * Constructs a Simulator Object and tests if the value of STARTUP_TIME
     * in the Simulator class is 0 before method "run()" is called and the
     * value returned by "getDisplayClient()" is not null.
     */
    @Test
    public void constructor2() {

        try {
            ServerSocket s = new ServerSocket(5065);
            s.setReuseAddress(true);
            if (!s.isBound()) {System.exit(-1);}
            String address = GeneralInetAddress.getLocalHost().getHostAddress();


            // create local DisplayServer and DisplayClient
            DisplayServer ds = new DisplayServer(address);
            DisplayClient dc = new DisplayClient(address);
            Simulator sim = new Simulator(dc);

            // test DisplayClient reference
            assertNotNull(sim.getDisplayClient());
            assertEquals(dc, sim.getDisplayClient());

            // test STARTUP_TIME value
            assertEquals(0, sim.STARTUP_TIME);


        } catch (IOException e) {
            System.err.println("IOException in SimulatorTest!");
            e.printStackTrace();
        }
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

        double[] pos = { 0.0, 0.0, 0.0 };
        GroundVehicle gv = new GroundVehicle(pos, 1.0, 0.0);
        GroundVehicle gv2 = new GroundVehicle(pos, 1.0, 1.0);

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

        double[] pos = { 0.0, 0.0, 0.0 };
        GroundVehicle gv = new GroundVehicle(pos, 1.0, 0.0);
        GroundVehicle gv2 = new GroundVehicle(pos, 1.0, 1.0);

        // add GroundVehicles
        sim.addVehicle(gv);
        sim.addVehicle(gv2);

        // check if the added vehicles get returned correctly
        assertEquals(gv, sim.getVehicle(0));
        assertEquals(gv2, sim.getVehicle(1));
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

        double[] pos = { 0.0, 0.0, 0.0 };

        // add GroundVehicles
        int nVehicles = 5;
        for (int i = 0; i <= nVehicles; i++) {
            GroundVehicle gv = new GroundVehicle(pos, 1.0, 0.0);
            sim.addVehicle(gv);

            // check if the number IDs of the vehicles correspond
            // to the order they were created in
            assertEquals(i+1, gv.getNumID());
        }
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
    
    /**
     * Method: addUserController()
     */
    @Test
    public void testAddUserController(){
        Simulator sim = new Simulator();

        double[] pos = { 0, 0, 0 };
        GroundVehicle gv = new GroundVehicle(pos, 1.0, 0.0);
        
        DisplayServer ds = new DisplayServer("127.0.0.1");
        UserController uc = new UserController(sim, gv, ds);
        
        sim.addUserController(uc);
        
        assertEquals(uc, sim.getUserController(1));

    }
    
      
    /**
     * Method: switchVehicles()
     */
    @Test
    public void testSwitchVehicles(){
        double[] startPosition = {50, 30, 0};
        double[] startPosition2 = {30, 10, 0};
        GroundVehicle gv = new GroundVehicle(startPosition, 5.0, Math.PI / 4);
        Simulator sim = new Simulator();

        LeadingController lc = new LeadingController(sim, gv);
        
        GroundVehicle target = new GroundVehicle(startPosition2, 5.0, Math.PI / 4);
        
        sim.switchVehicles(lc,target);
        
        assertNull(lc.getGroundVehicle());
    }
    
    /**
     * Method: checkWithinDistance()
     */
    @Test(expected=IllegalArgumentException.class)
        public void testCheckWithinDistance() throws Exception {
    	double[] pos1 = {0,0,0};
    	double[] pos2 = {0,2,0};

        // check for objects within distance
    	boolean isWithinDistance = Simulator.checkWithinDistance(pos1,pos2,3);
    	assertEquals(isWithinDistance,true);

        // check for objects not within distance
        double[] pos3 = {4,4,0};
        isWithinDistance = Simulator.checkWithinDistance(pos1,pos3,3);
        assertEquals(isWithinDistance,false);

        // check illegal theshold distance argument throws exception
        isWithinDistance = Simulator.checkWithinDistance(pos1,pos3,0);
    }



    /**
     * Method: distance()
     */
    @Test
    public void testDistance(){
    	double[] pos1 = {0,0,0};
    	double[] pos2 = {0,2,0};
    	
    	double dist = Simulator.distance(pos1,pos2);
    	
    	assertEquals(dist,2,1E-6);
    }
    
    /**
     * Method: removeOffscreenProjectile()
     */
    @Test
    public void testRemoveOffscreenProjectile(){
    	
    }
    
    /**
     * Method: projectileShotVehicle()
     */
    @Test
    public void testProjectileShotVehicle(){
    	
    	// test distance > HIT_DISTANCE
    	double[] projectilePos = {50 50 0};
    	double vehicleX = projectilePos[0] + Projectile.HIT_DISTANCE*2;
    	double [] vehiclePos = {vehicleX 50 0};
    	Simulator sim = new Simulator();
    	
    	boolean shotVehicle = sim.projectileShotVehicle(projectilePos,vehiclePos);
    	
    	assertEquals(shotVehicle,false);
    	
    	// test distance < HIT_DISTANCE
    	double[] projectilePos = {50 50 0};
    	double vehicleX = projectilePos[0] + Projectile.HIT_DISTANCE/2;
    	double [] vehiclePos = {vehicleX 50 0};
    	Simulator sim = new Simulator();
    	
    	boolean shotVehicle = sim.projectileShotVehicle(projectilePos,vehiclePos);
    	
    	assertEquals(shotVehicle,false);
    	
    }
    
    /**
     * Method: changeShotVehicle()
     */
    @Test
    public void testChangeShotVehicle(){
    	
    }
    
    /**
     * Method: projectileOffscreen()
     */
    @Test
    public void testProjectileOffscreen(){

        // test on-screen projectile position
    	double[] projectilePos = {1.0, 1.0, 0};
    	boolean isOffscreen = Simulator.projectileOffScreen(projectilePos);
    	assertEquals(isOffscreen, false);

        // test offscreen projectile position along x
        double[] invalidPos1 = {-1.0, 1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(invalidPos1);
        assertEquals(isOffscreen, true);

        double[] invalidPos2 = {Simulator.SIM_X+1.0, 1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(invalidPos2);
        assertEquals(isOffscreen, true);

        // test offscreen projectile position along y
        double[] invalidPos3 = {1.0, -1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(invalidPos3);
        assertEquals(isOffscreen, true);

        double[] invalidPos4 = {1.0, Simulator.SIM_Y+1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(invalidPos4);
        assertEquals(isOffscreen, true);

        // test boundaries along x
        double[] boundPos1 = {0.0, 1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(boundPos1);
        assertEquals(isOffscreen, false);

        double[] boundPos2 = {Simulator.SIM_X, 1.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(boundPos2);
        assertEquals(isOffscreen, false);

        // test boundaries along y
        double[] boundPos3 = {1.0, 0.0, 0.0};
        isOffscreen = Simulator.projectileOffScreen(boundPos3);
        assertEquals(isOffscreen, false);

        double[] boundPos4 = {1.0, Simulator.SIM_Y, 0.0};
        isOffscreen = Simulator.projectileOffScreen(boundPos4);
        assertEquals(isOffscreen, false);

    }

    public static void main(String[] args){

        JUnitCore.main(SimulatorTest.class.getName());
    }

} 
