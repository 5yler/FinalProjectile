/**
 * ProjectileTest
 * 16.35 FinalProjectile Game Final Project
 **/

import static org.junit.Assert.*;
import org.junit.Test;

public class ProjectileTest {

	/**
	 * Test Constructor
	 */
	@Test
	public void testConstructor() {

		double [] pose = {1, 2, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = pose;
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dt);

		DisplayServer ds = new DisplayServer();
		DisplayClient dc = new DisplayClient();
		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, v, ds);

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
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dt);

		DisplayServer ds = new DisplayServer();
		DisplayClient dc = new DisplayClient();
		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, v, ds);

		Projectile p = new Projectile(shooterPos, sim, uc);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTooFewArgumentsInConstructor() {
		// Too few arguments in pose constructor 
		double [] pose = {1, 2, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = {3};
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dt);

		DisplayServer ds = new DisplayServer();
		DisplayClient dc = new DisplayClient();
		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, v, ds);

		Projectile p = new Projectile(shooterPos, sim, uc);
	}


	/**
	 * Method: shoot(int sec, int msec)
	 */
	public void testShoot(){
		double [] pose = {1, 1, 0};
		double dx = 5, dy = 0, dtheta = 0;
		double [] shooterPos = pose;
		GroundVehicle gv = new GroundVehicle(pose, dx, dy, dt);

		DisplayServer ds = new DisplayServer();
		DisplayClient dc = new DisplayClient();
		Simulator sim = new Simulator();
		UserController uc = new UserController(sim, v, ds);

		Projectile p = new Projectile(shooterPos, sim, uc);
		
		p.shoot(1,0);
		
		double [] newPose = p.getPosition();
        assertEquals(121, newPose[0], 1e-6);
        assertEquals(0, newPose[1], 1e-6);
        assertEquals(0, newPose[2], 1e-6);
		
		
	}


	public static void main(String[] args){

		JUnitCore.main(Projectile.class.getName());
	}

}
