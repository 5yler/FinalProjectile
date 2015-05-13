import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import java.lang.IllegalArgumentException;

public class UserController extends VehicleController implements KeyListener {

    private double _startSpeed = 7.5;
    private double _startOmega = 0;

    private double _nextSpeed = 7.5;
    private double _nextOmega = 0;

    private double dSpeed = 0.1;
    private double dOmega = 0.1;

    public UserController(Simulator s, GroundVehicle v){
	super(s,v);	
		addKeyListener(this);
        setFocusable(true);
    }


    /**
     * Clamps speed and omega to allowable ranges and returns control with
     * bounded values of s and omega.
     * @param s forward speed
     * @param omega angular velocity
     * @return control with clamped linear and angular velocity values
     */
    private Control clampControl(double s, double omega) {

        double clampedSpeed;
        double clampedOmega;

        // clamp speed if it is above 10 or below 5
        if (s > maxTransSpeed)
            clampedSpeed = maxTransSpeed;
        else if (s < minTransSpeed)
            clampedSpeed = minTransSpeed;
        else
            clampedSpeed = s;

        // clamp angular velocity if it is above the allowed range
        clampedOmega = Math.min(Math.max(omega, -Math.PI/4), Math.PI/4);

        // create a control with the clamped s and omega values
        Control clampedControl = new Control(clampedSpeed, clampedOmega);
        return clampedControl;
    }


    public Control getControl(int sec, int msec) {
	return clampControl(_nextSpeed, _nextOmega)
    }




  public void keyPressed(KeyEvent e) { 
   int code = e.getKeyCode();

        {
            if (code == KeyEvent.VK_DOWN) {
                _nextSpeed -= dSpeed;
            }
            if (code == KeyEvent.VK_UP) {
                _nextSpeed += dSpeed;
            }
            if (code == KeyEvent.VK_LEFT) {
            	_nextOmega -= dOmega;
            }
            if (code == KeyEvent.VK_RIGHT) {
                _nextOmega += dOmega;
            }
  }
}



  public void keyReleased(KeyEvent e) { }

  public void keyTyped(KeyEvent e)
  {
    switch (e.getKeyChar()) {
    case 'q':
    case 'Q':
      System.exit(0);
    }
  }


}