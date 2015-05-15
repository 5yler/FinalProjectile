/**
 * ControlTest
 * 16.35 FinalProjectile Game Final Project
 **/

import static org.junit.Assert.*;
import org.junit.Test;

public class ControlTest {
	  @Test(expected=IllegalArgumentException.class)
	    public void makeLowS(){
	    //Put an invalid low s value in. Should throw an exception
	    new Control(0,.3);
	  }
		
	  @Test(expected=IllegalArgumentException.class)
	    public void makeHighS(){
	    //Put an invalid high s value in. Should throw an exception
	    new Control(50,.3);
	  }
	  
	  @Test(expected=IllegalArgumentException.class)
	    public void makeLowTheta(){
	    //Put an invalid low theta value in. Should throw an exception
	    new Control(7.5,-10);
	  }
	  
	  @Test(expected=IllegalArgumentException.class)
	    public void makeHighTheta(){
	    //Put an invalid high theta value in. Should throw an exception
	    new Control(7.5,10);
	  }
	  
	  @Test
	    public void CheckOutputs(){
	    double s = 7.5d;
	    double omega = .3d;
	    Control test = new Control(s, omega);
	    Assert.assertEquals(omega, test.getRotVel(), 1e-6);
	    Assert.assertEquals(s, test.getSpeed(), 1e-6);
	  }
	  
	  public static void main(String[] args){
	    JUnitCore.main(TestControl.class.getName());
	  }

}
