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

//    private DisplayServer _ds;
    private ControlPanel cp;

    private double minTransSpeed = 0;
    private double maxTransSpeed = 10;
    private double maxRotSpeed = Math.PI / 4;

    private double _startSpeed = 7.5;
    private double _startOmega = 0;

//    private double _nextSpeed = 7.5;
//    private double _nextOmega = 0;

    private double dSpeed = 0.5;
    private double maxOmega = Math.PI/4;

    public UserController(Simulator s, GroundVehicle v){
	super(s, v);
        cp = new ControlPanel(this);
//        _ds = ds;

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
        double _nextSpeed = cp.getUserSpeed();
        double _nextOmega = cp.getUserOmega();
    System.out.println("s: "+_nextSpeed+" omega: "+_nextOmega);
        return clampControl(_nextSpeed, _nextOmega);
    }



    public class ControlPanel extends JPanel implements KeyListener {

        private UserController _uc;

        private double _nextSpeed = 7.5;
        private double _nextOmega = 0;
        private double dSpeed = 0.1;
        private double maxOmega = Math.PI/4;

        protected JFrame frame;
        protected NumberFormat format = new DecimalFormat("#####.##");
        protected String myHostname;

        // set display sizes
        private int minDisplayX = 500;
        private int minDisplayY = 50;
        private int preferredDisplayX = 500;
        private int preferredDisplayY = 50;


  /*
  protected Color[] my_colors = new Color[] {Color.black,Color.blue,Color.cyan,

					     Color.green, Color.magenta,
					     Color.orange, Color.pink,
					     Color.red, Color.yellow,
					     Color.darkGray};
  */


        public ControlPanel (UserController uc) {
            _uc = uc;


            startGraphics();
        }

        public void startGraphics()
        {
            JFrame.setDefaultLookAndFeelDecorated(true);

            frame = new JFrame("16.35 Display");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Container container = frame.getContentPane();
            //container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
            container.setLayout(new BorderLayout());

            setOpaque(true);
            setFocusable(true);
            setMinimumSize(new Dimension(minDisplayX, minDisplayY));
            setPreferredSize(new Dimension(preferredDisplayX, preferredDisplayY));
            addKeyListener(this);
            container.add(this,BorderLayout.WEST);
            setVisible(true);

            frame.pack();
            frame.setVisible(true);
        }



        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            _nextOmega = 0;
            {
                if (code == KeyEvent.VK_DOWN) {
                    _nextSpeed -= dSpeed;
                    System.out.println("DOWN");
                }
                if (code == KeyEvent.VK_UP) {
                    _nextSpeed += dSpeed;
                    System.out.println("UP");
                }
                if (code == KeyEvent.VK_LEFT) {
                    _nextOmega = maxOmega;
                    System.out.println("LEFT");

                }
                if (code == KeyEvent.VK_RIGHT) {
                    _nextOmega = -maxOmega;
                    System.out.println("RIGHT");

                }
            }
        }

        public double getUserSpeed() {
            System.out.println("getUserSpeed() = "+_nextSpeed);
            return _nextSpeed;
        }

        public double getUserOmega() {
            return _nextOmega;
        }

        public void keyReleased(KeyEvent e) { }

        public void keyTyped(KeyEvent e)
        {
            switch (e.getKeyChar()) {
                case 'q':
                case 'Q':
                    System.exit(0);
            }
            int code = e.getKeyCode();

            {
                if (code == KeyEvent.VK_DOWN) {
                    _nextSpeed -= dSpeed;
                    System.out.println("DOWN");
                }
                if (code == KeyEvent.VK_UP) {
                    _nextSpeed += dSpeed;
                    System.out.println("UP");
                }
                if (code == KeyEvent.VK_LEFT) {
                    _nextOmega = maxOmega;
                    System.out.println("LEFT");

                }
                if (code == KeyEvent.VK_RIGHT) {
                    _nextOmega = -maxOmega;
                    System.out.println("RIGHT");

                }
            }
        }






    }



}