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

public class ControlPanel extends JPanel implements KeyListener {

    private static double _nextSpeed = 7.5;
    private static double _nextOmega = 0;
    private double dSpeed = 0.1;
    private double dOmega = 0.1;

    protected JFrame frame;
    protected NumberFormat format = new DecimalFormat("#####.##");
    protected String myHostname;

    // set display sizes
    private int minDisplayX = 500;
    private int minDisplayY = 500;
    private int preferredDisplayX = 500;
    private int preferredDisplayY = 500;


  /*
  protected Color[] my_colors = new Color[] {Color.black,Color.blue,Color.cyan,

					     Color.green, Color.magenta, 
					     Color.orange, Color.pink,
					     Color.red, Color.yellow,
					     Color.darkGray};
  */


    public ControlPanel (String hostname) {
        myHostname = hostname;


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startGraphics();
            }
        });
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
                _nextOmega -= dOmega;
                System.out.println("LEFT");

            }
            if (code == KeyEvent.VK_RIGHT) {
                _nextOmega += dOmega;
                System.out.println("RIGHT");

            }
        }
    }

    public static double getUserSpeed() {
        System.out.println("getUserSpeed() = "+_nextSpeed);
        return _nextSpeed;
    }

    public static double getUserOmega() {
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
                _nextOmega -= dOmega;
                System.out.println("LEFT");

            }
            if (code == KeyEvent.VK_RIGHT) {
                _nextOmega += dOmega;
                System.out.println("RIGHT");

            }
        }
    }






}
