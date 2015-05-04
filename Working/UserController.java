import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

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

public class UserController extends VehicleController {

    private DisplayServer _ds;
//    private ControlPanel cp;

    private double minTransSpeed = 0;
    private double maxTransSpeed = 10;
    private double maxRotSpeed = Math.PI / 4;

    private double _startSpeed = 7.5;
    private double _startOmega = 0;

//    private double _nextSpeed = 7.5;
//    private double _nextOmega = 0;

    private double dSpeed = 0.5;
    private double maxOmega = Math.PI/4;

    public UserController(Simulator s, GroundVehicle v, DisplayServer ds){
	super(s, v);
        _ds = ds;


    }

    public void addDisplayServer(DisplayServer ds) {
        _ds = ds;
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
        double _nextSpeed = _ds.getUserSpeed();
        double _nextOmega = _ds.getUserOmega();
    System.out.println("s: "+_nextSpeed+" omega: "+_nextOmega);
        return clampControl(_nextSpeed, _nextOmega);
    }








}