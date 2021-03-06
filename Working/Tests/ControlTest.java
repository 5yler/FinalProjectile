/**
 * ControlTest
 * 16.35 FinalProjectile Game Final Project
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import org.junit.runner.JUnitCore;
import static org.junit.Assert.*;
import org.junit.Test;

public class ControlTest {

	@Test(expected=IllegalArgumentException.class)
	public void makeLowS(){
		// an invalid low s value should throw an exception
		new Control(0.5*GroundVehicle.MIN_VEL,.3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void makeHighS(){
		// an invalid high s value should throw an exception
		new Control(GroundVehicle.MAX_VEL+1,.3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void makeLowOmega(){
		// an invalid low omega value should throw an exception
		double omega = -Math.PI - 1;
		new Control(5,omega);
	}

	@Test(expected=IllegalArgumentException.class)
	public void makeHighOmega(){
		// an invalid high omega value should throw an exception
		double omega = Math.PI + 1;
		new Control(5,omega);
	}

	@Test(expected=IllegalArgumentException.class)
	public void makeHighBoundaryOmega(){
		// setting omega to higher, non-inclusive limit of interval
		// should throw an exception
		double omega = Math.PI;
		Control c = new Control(5,omega);
	}

	@Test
	public void CheckOutputs(){
		double s = 7.5d;
		double omega = .3d;
		Control c = new Control(s, omega);
		assertEquals(omega, c.getRotVel(), 1e-6);
		assertEquals(s, c.getSpeed(), 1e-6);
	}

	@Test
	public void makeLowBoundaryOmega(){
		// setting omega to lower, inclusive limit of interval
		double omega = -Math.PI;
		Control c = new Control(5,omega);
		assertEquals(-Math.PI,c.getRotVel(),1e-6);
	}

	public static void main(String[] args){
		JUnitCore.main(ControlTest.class.getName());
	}

}
