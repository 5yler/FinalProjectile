/**
 * ProjectileTest
 * 16.35 FinalProjectile Game Final Project
 **/

import org.junit.Test;
import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;

public class ProjectileTest {

	/**
	 * Test Constructor
	 */
	@Test
	public void testConstructor() {

		double [] pose = {1, 2, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = pose;
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPos, sim, uc);

		double [] newPose = p.getPosition();
		assertEquals(shooterPos[0], newPose[0], 1e-6);
		assertEquals(shooterPos[1], newPose[1], 1e-6);
		assertEquals(shooterPos[2], newPose[2], 1e-6);

		assertEquals(sim, p.getSimulator());
		assertEquals(uc, p.getUserController());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTooManyArgumentsInConstructor() {
		// Too many arguments in pose constructor 
		double [] pose = {1, 2, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = {3, 4, 5, 6};
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPos, sim, uc);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTooFewArgumentsInConstructor() {
		// Too few arguments in pose constructor 
		double [] pose = {1, 2, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = {3};
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPos, sim, uc);
	}


	/**
	 * Method: shoot(int sec, int msec)
	 *
	 * Tests if projectile moves appropriate amount along x when shot
	 */
	@Test
	public void testShootX(){

		// create vehicle facing along positive x direction
		double [] shooterPosition = {1, 1, 0};
		double dx = 5, dy = 0, dtheta = 0;
		GroundVehicle gv = new GroundVehicle(shooterPosition, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPosition, sim, uc);

		// check if projectile moves appropriate amount after one second
		p.shoot(1, 0);
		
		double [] newPose = p.getPosition();
        assertEquals(shooterPosition[0]+Projectile.PROJECTILE_SPEED, newPose[0], 1e-6);
        assertEquals(shooterPosition[1], newPose[1], 1e-6);
        assertEquals(shooterPosition[2], newPose[2], 1e-6);
	}

	/**
	 * Method: shoot(int sec, int msec)
	 *
	 * Tests if projectile moves appropriate amount along y when shot
	 */
	@Test
	public void testShootY(){

		// create vehicle facing along positive y direction
		double [] shooterPosition = {1, 1, Math.PI/2};
		double dx = 5, dy = 0, dtheta = 0;
		GroundVehicle gv = new GroundVehicle(shooterPosition, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPosition, sim, uc);

		// check if projectile moves appropriate amount after one second
		p.shoot(1, 0);

		double [] newPose = p.getPosition();
		assertEquals(shooterPosition[0], newPose[0], 1e-6);
		assertEquals(shooterPosition[1]+Projectile.PROJECTILE_SPEED, newPose[1], 1e-6);
		assertEquals(shooterPosition[2], newPose[2], 1e-6);
	}

	/**
	 * Method: shoot(int sec, int msec)
	 *
	 * Tests if projectile moves appropriate amount along
	 * both x and y when shot at an angle
	 */
	@Test
	public void testShootXandY(){

		// create vehicle facing along positive y direction
		double shooterAngle = Math.PI/4;
		double [] shooterPosition = {1, 1, shooterAngle};
		double dx = 5, dy = 0, dtheta = 0;
		GroundVehicle gv = new GroundVehicle(shooterPosition, dx, dy, dtheta);

		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, gv);

		Projectile p = new Projectile(shooterPosition, sim, uc);

		// check if projectile moves appropriate amount after one second
		p.shoot(1, 0);

		double expectedX = shooterPosition[0]+Math.cos(shooterAngle)*Projectile.PROJECTILE_SPEED;
		double expectedY = shooterPosition[1]+Math.sin(shooterAngle)*Projectile.PROJECTILE_SPEED;

		double [] newPose = p.getPosition();
		assertEquals(expectedX, newPose[0], 1e-6);
		assertEquals(expectedY, newPose[1], 1e-6);
		assertEquals(shooterAngle, newPose[2], 1e-6);
	}


	public static void main(String[] args){

		JUnitCore.main(ProjectileTest.class.getName());
	}

}
