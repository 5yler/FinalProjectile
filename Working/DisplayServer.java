/**
 * DisplayServer
 * 16.35 FinalProjectile Game Final Project
 * @author  16.35 Staff
 * @author  Syler Wagner        <syler@mit.edu>
 **/

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

public class DisplayServer extends JPanel implements KeyListener {

  public boolean OVER = false;

  private double SPEED_INCREMENT = 1;
  private double[] userSpeed = {5*GroundVehicle.MIN_VEL, 5*GroundVehicle.MIN_VEL};
  private double[] userOmega = {0, 0};
  private boolean[] projectileGenerated = {false, false};
  private static Random rand = new Random();

  private static int historySkip = 5;
  private static final long serialVersionUID = 1l;

  // ground vehicles
  protected double gvX[], gvY[], gvTheta[];
  protected int numVehicles = 0;
  protected int gvC[]; // vehicle color indexes in COLORS array

  // projectiles
  protected double pX[], pY[];
  protected int numProjectiles = 0;
  protected int pC[]; // projectile color indexes in COLORS array

  // scores
  protected int shots1, hits1, kills1; // user 1
  protected int shots2, hits2, kills2; // user 2
  protected NumberFormat scoreFormat = new DecimalFormat("###.#");

  // keyboard help
  private boolean HELP = true;

  protected int maxNumVehicles = 20;
  protected int shapeX[], shapeY[];
  protected JFrame frame;
  protected NumberFormat format = new DecimalFormat("#####.##");
  protected String myHostname;
  protected Color[] vehicleColors;
  protected Color[] pathColors;

  /* CUSTOM SETTINGS */
  public static final int DISPLAY_X = 800; // display window x pixels
  public static final int DISPLAY_Y = 500; // display window y pixels
  public static final Color DISPLAY_BACKGROUND_COLOR = Color.black; // display background color
  public static final int LINE_Y_PIX = 12; // pixel height of text line
  public static final int LINE_X_PIX_OFFSET = 10; // pixel offset along x from edge of screen
  public static final int LINE_Y_PIX_OFFSET = 3; // pixel offset along x from edge of screen


  public static final Color[] RED = {new Color(255, 21, 60), // red
          new Color(55 + 20, 20, 20)}; // red
  public static final Color[] BLUE = {new Color(69, 127, 255), new Color(35, 40, 85)};
  public static final Color[] ORANGE = {new Color(232, 117, 31), new Color(75, 45, 40)};
  public static final Color[] YELLOW = {new Color(255, 209, 21), new Color(65, 60, 0)};
  public static final Color[] PURPLE = {new Color(160, 67, 232), new Color(50, 0, 50)};
  //  public static final Color[] WHITE = {new Color(215,215,215),new Color(55,55,55)};
  public static final Color[] WHITE = {new Color(49, 57, 135), new Color(20, 25, 45)};


  public static final Color[] PROJECTILE_COLOR = WHITE; // projectile color
  public static final Color[] USER1_COLOR = RED; // red // user vehicle color
  public static final Color[] USER2_COLOR = ORANGE; // purple // user vehicle color
  public static final Color[] LEADING_COLOR = BLUE; // blue // leading vehicle color
  public static final Color[] FOLLOWING_COLOR = WHITE; // orangedisplay background color

  public static final Color[] USER_SCORES = {new Color(145, 31, 20), // red
          new Color(150, 75, 35)}; // red

  public static final Color[][] COLORS = new Color[][]{
          PROJECTILE_COLOR, // 0
          USER1_COLOR,      // 1
          USER2_COLOR,      // 2
          LEADING_COLOR,    // 3
          FOLLOWING_COLOR,  // 4
  };


  public static final int SLEEP_TIME = 0; // delay between timesteps when drawing vehicle trajectories
  // 10-100 is a reasonable number

  private boolean print = false;   // set to true for print statements
  private boolean fullscreen = false; // set to true for full-screen

  // set display sizes
  private int minDisplayX = DISPLAY_X;
  private int minDisplayY = DISPLAY_Y;
  private int preferredDisplayX = DISPLAY_X;
  private int preferredDisplayY = DISPLAY_Y;

  private int NUM_CIRCLES = 3;


  /*
  protected Color[] my_colors = new Color[] {Color.black,Color.blue,Color.cyan,

					     Color.green, Color.magenta, 
					     Color.orange, Color.pink,
					     Color.red, Color.yellow,
					     Color.darkGray};
  */


  public class History {
    History() {
      myX = new double[100000];
      myY = new double[100000];
      myNumPoints = 0;
      loopHistory = 0;
      trueHistoryLength = 0;
    }

    public double[] myX;
    public double[] myY;
    int myNumPoints;
    int trueHistoryLength;
    int loopHistory;
  }

  History[] histories;
  boolean trace = false;

  public synchronized void clear() {
    if (histories != null) {
      for (int i = 0; i < histories.length; i++) {
        histories[i].myNumPoints = 0;
        histories[i].loopHistory = 0;
        histories[i].trueHistoryLength = 0;
      }
    }
  }

  public synchronized void resetHistories(int numVehicles) {
    histories = new History[numVehicles];
    for (int i = 0; i < numVehicles; i++)
      histories[i] = new History();
  }

  public class MessageListener extends Thread {
    public BufferedReader my_client;
    public DisplayServer my_display;

    public MessageListener(Socket client, DisplayServer display) {
      my_display = display;
      try {
        //System.out.println("Default size: " + client.getReceiveBufferSize());
        my_client = new BufferedReader
                (new InputStreamReader(client.getInputStream()));
      } catch (IOException e) {
        System.err.println("Very weird IOException in creating the BufferedReader");
        System.err.println(e);
        System.exit(-1);
      }
    }

    public void run() {
      try {
        while (true) {
          String message = my_client.readLine();
          if (message == null) {
            System.out.println("EOF reached!");
            return; //EOF reached
          }

          StringTokenizer st = new StringTokenizer(message);
          //System.out.println("Received: " + message);
          String tok = st.nextToken();
          if (tok.equals("clear")) {
            my_display.clear();
          } else if (tok.equals("traceon")) {
            synchronized (my_display) {
              my_display.trace = true;
            }
          } else if (tok.equals("traceoff")) {
            synchronized (my_display) {
              my_display.trace = false;
            }
          } else if (tok.equals("close")) {
            return;
          } else {
            synchronized (my_display) {
              if (tok.equals("over")) {
                my_display.OVER = true;
                tok = st.nextToken();
              }
              outerif:
              if (tok.equals("vehicles")) {
                tok = st.nextToken();
                if (tok.equals("score")) {
                  break outerif;
                }
                if (my_display.numVehicles != Integer.parseInt(tok)) {
                  my_display.numVehicles = Integer.parseInt(tok);
                  my_display.gvX = new double[my_display.numVehicles];
                  my_display.gvY = new double[my_display.numVehicles];
                  my_display.gvTheta = new double[my_display.numVehicles];
                  my_display.gvC = new int[my_display.numVehicles];
                  my_display.resetHistories(numVehicles);
                }
                outerloop:
                for (int i = 0; i < my_display.numVehicles; i++) {
                  if (tok.equals("score")) {
                    break outerloop;
                  }
                  tok = st.nextToken();
                  my_display.gvX[i] = Double.parseDouble(tok);
                  tok = st.nextToken();
                  my_display.gvY[i] = Double.parseDouble(tok);
                  tok = st.nextToken();
                  my_display.gvTheta[i] = Double.parseDouble(tok);
                  tok = st.nextToken();
                  my_display.gvC[i] = (int) Double.parseDouble(tok);
                  if (trace) {
                    if (histories[i].trueHistoryLength % historySkip == 0) {
                      int n;
                      if (histories[i].myNumPoints == histories[i].myX.length) {
                        n = 0;
                        histories[i].myNumPoints = 0;
                        histories[i].loopHistory = 1;
                      } else {
                        n = histories[i].myNumPoints;
                        histories[i].myNumPoints++;
                      }
                      histories[i].myX[n] = my_display.gvX[i];
                      histories[i].myY[n] = my_display.gvY[i];
                    }
                    histories[i].trueHistoryLength++;
                  } // end if (trace)
                } // end for (int i = 0; i < my_display.numVehicles; i++)
              }
              // end for (int i = 0; i < my_display.numProjectiles; i++)
              tok = st.nextToken();
              if (tok.equals("score")) {
                tok = st.nextToken();
                my_display.shots1 = Integer.parseInt(tok);
                tok = st.nextToken();
                my_display.shots2 = Integer.parseInt(tok);
                tok = st.nextToken();
                my_display.hits1 = Integer.parseInt(tok);
                tok = st.nextToken();
                my_display.hits2 = Integer.parseInt(tok);
                tok = st.nextToken();
                my_display.kills1 = Integer.parseInt(tok);
                tok = st.nextToken();
                my_display.kills2 = Integer.parseInt(tok);
              }

              // end if (tok.equals("vehicles"))
//              if (tok.equals("projectiles")) {
              tok = st.nextToken();
              if (tok.equals("projectiles")) {
                tok = st.nextToken();
                if (my_display.numProjectiles != Integer.parseInt(tok)) {
                  my_display.numProjectiles = Integer.parseInt(tok);
                  my_display.pX = new double[my_display.numProjectiles];
                  my_display.pY = new double[my_display.numProjectiles];
                  my_display.pC = new int[my_display.numProjectiles];
                }
                projectileif:
                for (int i = 0; i < my_display.numProjectiles; i++) {
                  tok = st.nextToken();
                  my_display.pX[i] = Double.parseDouble(tok);
                  tok = st.nextToken();
                  my_display.pY[i] = Double.parseDouble(tok);
                  tok = st.nextToken();
                  my_display.pC[i] = (int) Double.parseDouble(tok);

                }
              }
//              } // end if (tok.equals("vehicles"))
            } // End synchronized (my_display)
          }
          my_display.repaint();
          try {
            sleep(SLEEP_TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
      }
      return;
    }
  }

  public DisplayServer(String hostname) {
    myHostname = hostname;
    shapeX = new int[9];
    shapeY = new int[9];

    // This is just the UAV shape centred at the origin.
    // If you wanted to draw a more realistic UAV, you would modify this
    // polygon. 

    shapeX[0] = 10;
    shapeY[0] = 0;
    shapeX[1] = 0;
    shapeY[1] = -5;
    shapeX[2] = 0;
    shapeY[2] = -2;
    shapeX[3] = -8;
    shapeY[3] = -2;
    shapeX[4] = -10;
    shapeY[4] = -4;
    shapeX[5] = -10;
    shapeY[5] = 4;
    shapeX[6] = -8;
    shapeY[6] = 2;
    shapeX[7] = 0;
    shapeY[7] = 2;
    shapeX[8] = 0;
    shapeY[8] = 5;

    // generate array of random colors for up to 20 different vehicles
    vehicleColors = new Color[maxNumVehicles];
    pathColors = new Color[maxNumVehicles];


    // preset colors
    vehicleColors[0] = new Color(255, 21, 60); // red
    pathColors[0] = new Color(55 + 20, 20, 20); // red
    vehicleColors[1] = new Color(69, 127, 255); // blue
    pathColors[1] = new Color(35, 40, 85);
    vehicleColors[2] = new Color(232, 117, 31); // orange
    pathColors[2] = new Color(75, 45, 40);
    vehicleColors[3] = new Color(255, 209, 21); // yellow
    pathColors[3] = new Color(65, 60, 0);
    vehicleColors[4] = new Color(160, 67, 232); // purple
    pathColors[4] = new Color(50, 0, 50);


    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        startGraphics();
      }
    });
  }

  public void startGraphics() {
    JFrame.setDefaultLookAndFeelDecorated(true);

    frame = new JFrame("I'm Feeling Lucky!");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container container = frame.getContentPane();
    //container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
    container.setLayout(new BorderLayout());

    setOpaque(true);
    setFocusable(true);
    setMinimumSize(new Dimension(minDisplayX, minDisplayY));
    setPreferredSize(new Dimension(preferredDisplayX, preferredDisplayY));
    addKeyListener(this);
    container.add(this, BorderLayout.WEST);
    setVisible(true);

    frame.pack();
    frame.setVisible(true);

    /** make display full-screen **/
    if (fullscreen) {
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
  }

  public static void print(String s) {
    if (FinalProjectile.debug_keys) {
      System.out.print(s);
    }
  }

  public static void printmsg(String s) {
    if (FinalProjectile.debug_display_msgs) {
      System.out.print(s);
    }
  }

  /**
   * @param e
   */
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    {
      // USER 1
      // forward velocity control
      if (code == KeyEvent.VK_DOWN) {
        decreaseSpeed(0);
        print("User 1: DOWN");
      }
      if (code == KeyEvent.VK_UP) {
        increaseSpeed(0);
        print("User 1: UP");
      }
      // turning
      if (code == KeyEvent.VK_LEFT) {
        turnLeft(0);
        print("User 1: LEFT");
      }
      if (code == KeyEvent.VK_RIGHT) {
        turnRight(0);
        print("User 1: RIGHT");
      }
      // generate projectiles
      if (code == KeyEvent.VK_SPACE) {
        toggleProjectile(true, 0);
      }
      // USER 2
      // forward velocity control
      if (code == KeyEvent.VK_S) {
        decreaseSpeed(1);
        print("User 2: DOWN");
      }
      if (code == KeyEvent.VK_W) {
        increaseSpeed(1);
        print("User 2: UP");
      }
      // turning
      if (code == KeyEvent.VK_A) {
        turnLeft(1);
        print("User 2: LEFT");
      }
      if (code == KeyEvent.VK_D) {
        turnRight(1);
        print("User 2: RIGHT");
      }
      // generate projectiles
      if (code == KeyEvent.VK_SHIFT) {
        toggleProjectile(true, 1);
      }
      if (code == KeyEvent.VK_H) {
        HELP = !HELP;
      }
    }
  }

  /**
   * Handle the key-released event from the text field.
   */
  public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();
    // USER 1
    // reset rotational velocity
    if (code == KeyEvent.VK_LEFT) {
      stopTurning(0);
    }
    if (code == KeyEvent.VK_RIGHT) {
      stopTurning(0);
    }
    // stop generating projectile
    if (code == KeyEvent.VK_SPACE) {
      toggleProjectile(false, 0);
    }
    // USER 2
    // reset rotational velocity
    if (code == KeyEvent.VK_A) {
      stopTurning(1);
    }
    if (code == KeyEvent.VK_D) {
      stopTurning(1);
    }
    // stop generating projectile
    if (code == KeyEvent.VK_SHIFT) {
      toggleProjectile(false, 1);
    }
  }


  public void increaseSpeed(int UserID) {

    // increment user speed
    userSpeed[UserID] += SPEED_INCREMENT;

    if (userSpeed[UserID] > GroundVehicle.MAX_VEL) {
      // clamp if speed too high
      userSpeed[UserID] = GroundVehicle.MAX_VEL;
    }
  }

  public void decreaseSpeed(int UserID) {

    // decrease user speed
    userSpeed[UserID] -= SPEED_INCREMENT;

    if (userSpeed[UserID] < GroundVehicle.MIN_VEL) {
      // clamp if speed too low
      userSpeed[UserID] = GroundVehicle.MIN_VEL;
    }
  }

  public void turnLeft(int UserID) {
    userOmega[UserID] = GroundVehicle.MAX_OMEGA;
  }

  public void turnRight(int UserID) {
    userOmega[UserID] = -GroundVehicle.MAX_OMEGA;
  }

  public void stopTurning(int UserID) {
    userOmega[UserID] = 0;
  }


  public void toggleProjectile(boolean generated, int UserID) {
    projectileGenerated[UserID] = generated;
  }


  public double getUserSpeed(int UserID) {
    if (print) {
      System.out.println("getUserSpeed() = " + userSpeed + " getUserOmega() = " + userOmega);
    }
    return userSpeed[UserID];
  }

  public double getUserOmega(int UserID) {
    return userOmega[UserID];
  }

  public boolean getProjectileGenerated(int UserID) {
    return projectileGenerated[UserID];
  }


  /**
   * Listens to key press events and modifies settings accordingly.
   *
   * @param e
   */
  public void keyTyped(KeyEvent e) {
    switch (e.getKeyChar()) {
      case 'Q':
      case 'q':
        System.exit(0);
      case 'F': // print out dimensions of window
      case 'f':
        Dimension size = frame.getBounds().getSize();
        System.out.println(size);
      case 'P': // switch debug statements on/off
      case 'p':
        print ^= true; // flip value of print boolean
    }
    int code = e.getKeyCode();


//    {
//      if (code == KeyEvent.VK_DOWN) {
//        userSpeed -= SPEED_INCREMENT;
//        System.out.println("DOWN");
//      }
//      if (code == KeyEvent.VK_UP) {
//        userSpeed += SPEED_INCREMENT;
//        System.out.println("UP");
//      }
//      if (code == KeyEvent.VK_LEFT) {
//        userOmega -= dOmega;
//        System.out.println("LEFT");
//
//      }
//      if (code == KeyEvent.VK_RIGHT) {
//        userOmega += dOmega;
//        System.out.println("RIGHT");
//
//      }
//    }
  }

  /**
   * @return array with random RGB color pair (dark color and light color)
   */
  public Color[] randomColorPair() {
    int r = rand.nextInt(155); // 255 will result in colors that can't be detected on a white background
    int g = rand.nextInt(155);
    int b = rand.nextInt(155);
    Color darkColor = new Color(r, g, b);
    Color lightColor = new Color(r + 100, g + 100, b + 100);
    Color[] colorPair = new Color[2];
    colorPair[0] = darkColor;
    colorPair[1] = lightColor;
    return colorPair;
  }

  /**
   * @return random dark RGB color
   */
  public static Color randomDarkColor() {
    int r = rand.nextInt(155); // 255 will result in colors that can't be detected on a white background
    int g = rand.nextInt(155);
    int b = rand.nextInt(155);
    return new Color(r, g, b);
  }

  /**
   * @return random light RGB color
   */
  public static Color randomLightColor() {
    int r = rand.nextInt(205) + 50;
    int g = rand.nextInt(205) + 50;
    int b = rand.nextInt(155) + 100;
    return new Color(r, g, b);
  }

  /**
   * @return random grey RGB color
   */
  public static Color randomCircleColor() {
    int r = rand.nextInt(50);
    int g = rand.nextInt(50);
    int b = rand.nextInt(50);
    return new Color(r, g, b);
  }

  protected synchronized void drawVehicles(Graphics g) {
    g.setColor(Color.black);

//
//    // generate random colors
//    for (int i=0; i<numVehicles; i++) {
//      Color[] colorPair = randomColorPair();
//      vehicleColors[i] = colorPair[0];
//      pathColors[i] = colorPair[1];
//    }

    // This chunk of code just translate and rotates the shape.

    for (int j = 0; j < numVehicles; j++) {
      /*
      // set colors to jth index of vehicleColors
      if (j < vehicleColors.length){
        g.setColor(vehicleColors[j]);

      }else{
        g.setColor(vehicleColors[vehicleColors.length-1]);
      }*/

      // set color matching index in COLORS array

      g.setColor(COLORS[gvC[j]][0]);

      int drawX[] = new int[9];
      int drawY[] = new int[9];

      for (int i = 0; i < 9; i++) {
        // We scale the x and y by 5, since the bounds on X and Y are 100x100
        // but our windows is 500x500.

        double x = gvX[j] * 5;
        double y = gvY[j] * 5;
        double th = gvTheta[j];
        drawX[i] = (int) (x + Math.cos(th) * shapeX[i] + Math.sin(th) * shapeY[i]);
        drawY[i] = (int) (y + Math.sin(th) * shapeX[i] - Math.cos(th) * shapeY[i]);
        drawY[i] = DISPLAY_Y - drawY[i]; /** MODDED TO ACCOUNT FOR VARIABLE DISPLAY SIZE **/
      }
      g.drawPolygon(drawX, drawY, 9);
    }
  }

  /**
   * Draws projectiles.
   *
   * @param g
   */
  protected synchronized void drawProjectiles(Graphics g) {
    // set color to projectile color
    for (int j = 0; j < numProjectiles; j++) {

      // set color to user color with matching index in COLORS array
      printmsg("pC[j] = " + pC[j]);
      g.setColor(COLORS[pC[j]][0]);

      // cast projectile positions to be integers
      int x = (int) pX[j];
      int y = (int) pY[j];

      // correct for simulation y direction being different from display
      y = Simulator.SIM_Y - y; /** MODDED TO ACCOUNT FOR VARIABLE DISPLAY SIZE **/

      // draw projectile as circle of radius 1
      drawCircle(g, x, y, 1);
    }
  }

  protected synchronized void drawHistories(Graphics g) {
    g.setColor(Color.black);

    // This chunk of code just translate and rotates the shape.

    for (int j = 0; j < numVehicles; j++) {
      /* old color code
      if (j < pathColors.length){
        g.setColor(pathColors[j]);

      }else{
        g.setColor(pathColors[pathColors.length-1]);
      }*/

      // set color
      g.setColor(COLORS[gvC[j]][1]);


      int drawX[];
      int drawY[];
      if (histories[j].loopHistory == 0) {
        drawX = new int[histories[j].myNumPoints];
        drawY = new int[histories[j].myNumPoints];
      } else {

        drawX = new int[histories[j].myX.length];
        drawY = new int[histories[j].myY.length];
      }
      for (int i = 0; i < drawX.length; i++) {
        // We scale the x and y by 5, since the bounds on X and Y are 100x100
        // but our windows is 500x500.

        double x = histories[j].myX[i] * 5;
        double y = histories[j].myY[i] * 5;
        drawX[i] = (int) (x);
        drawY[i] = DISPLAY_Y - (int) y; /** MODDED TO ACCOUNT FOR VARIABLE DISPLAY SIZE **/
      }
      g.drawPolygon(drawX, drawY, drawX.length);
    }
  }

  /**
   * Draws circle based on x, y, and radius given in simulation coordinate system.
   *
   * @param g  graphics object
   * @param Xc x location of circle center
   * @param Yc y location of circle center
   * @param R  circle radius
   */
  public static void drawCircle(Graphics g, int Xc, int Yc, int R) {

    int diameter = 2 * R;

    // shift x and y by circle radius to center it
    g.fillOval((Xc - R) * 5, (Yc - R) * 5, 2 * R, 2 * R);

  }

  /**
   * Draws random circle in simulation.
   *
   * @param g graphics object
   */
  public static void drawRandomCircle(Graphics g) {


    int diameter = (int) Simulator.randomDoubleInRange(0, Math.min(Simulator.SIM_X, Simulator.SIM_Y));
    int R = diameter / 2;
    int Xc = (int) Simulator.randomDoubleInRange(0, Simulator.SIM_X);
    int Yc = (int) Simulator.randomDoubleInRange(0, Simulator.SIM_Y);

    // shift x and y by circle radius to center it
    g.fillOval((Xc - R) * 5, (Yc - R) * 5, diameter * 5, diameter * 5);

  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g); //paints the background and image

    Rectangle bounds = this.getBounds();

    // set distplay background
    g.setColor(DISPLAY_BACKGROUND_COLOR);
    g.fillRect(0, 0, bounds.width, bounds.height);


    // draw circles
    for (int i = 0; i < NUM_CIRCLES; i++) {
      g.setColor(randomCircleColor());
      drawRandomCircle(g);
    }

    g.setColor(randomLightColor());
    g.drawString("Display running in the 90's", LINE_X_PIX_OFFSET, LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    g.drawString("on " + myHostname, LINE_X_PIX_OFFSET, 2 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    if (trace)
      drawHistories(g);
    drawVehicles(g);
    drawProjectiles(g);
    drawScores(g);
    drawHelp(g);
  }

  /**
   * Draws projectiles.
   *
   * @param g
   */
  protected synchronized void drawScores(Graphics g) {

    if (!OVER) {
      g.setColor(USER1_COLOR[0]);
      g.setFont(new Font("default", Font.BOLD, 14));
      g.drawString("User 1", LINE_X_PIX_OFFSET, 4 * LINE_Y_PIX);
      g.setFont(new Font("default", Font.PLAIN, 14));

      g.setColor(USER_SCORES[0]);
      g.drawString("Shots: " + shots1, LINE_X_PIX_OFFSET, 5 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("Hits: " + hits1, LINE_X_PIX_OFFSET, 6 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("Kills: " + kills1, LINE_X_PIX_OFFSET, 7 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      String accuracy1;
      if (shots1 == 0) {
        accuracy1 = "NaN";
      } else {
        double acc1 = 100.0 * hits1 / shots1;
        accuracy1 = scoreFormat.format(acc1);
      }
      g.drawString("Accuracy: " + accuracy1 + "%", LINE_X_PIX_OFFSET, 8 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);

      if (FinalProjectile.MULTIPLAYER) {

        g.setColor(USER2_COLOR[0]);
        g.setFont(new Font("default", Font.BOLD, 14));
        g.drawString("User 2", LINE_X_PIX_OFFSET, 10 * LINE_Y_PIX);
        g.setColor(USER_SCORES[1]);
        g.setFont(new Font("default", Font.PLAIN, 14));
        g.drawString("Shots: " + shots2, LINE_X_PIX_OFFSET, 11 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("Hits: " + hits2, LINE_X_PIX_OFFSET, 12 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("Kills: " + kills2, LINE_X_PIX_OFFSET, 13 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        String accuracy2;
        if (shots2 == 0) {
          accuracy2 = "NaN";
        } else {
          double acc2 = 100.0 * hits2 / shots2;
          accuracy2 = scoreFormat.format(acc2);
        }
        g.drawString("Accuracy: " + accuracy2 + "%", LINE_X_PIX_OFFSET, 14 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      }
      if (FinalProjectile.debug_scores) {
        System.out.println(shots1 + " " + shots2 + " " + hits1 + " " + hits2 + " DisplayServer.drawScores()");
      }
    }

    if (OVER) {
      gameOver(g);
    }
  }

  protected synchronized void gameOver(Graphics g) {

    g.setColor(randomLightColor());
    g.setFont(new Font("default", Font.PLAIN, 100));
    g.drawString("GAME OVER", LINE_X_PIX_OFFSET, DISPLAY_Y - LINE_Y_PIX_OFFSET);

    g.setColor(USER1_COLOR[0]);
//    g.setFont(new Font("default", Font.BOLD, 14));
    g.setFont(new Font("default", Font.BOLD, 14));
    g.drawString("User 1", LINE_X_PIX_OFFSET, 4 * LINE_Y_PIX);
//    g.setFont(new Font("default", Font.PLAIN, 14));
    g.setFont(new Font("default", Font.PLAIN, 14));

    g.setColor(USER_SCORES[0]);
    g.drawString("Shots: " + shots1, LINE_X_PIX_OFFSET, 5 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    g.drawString("Hits: " + hits1, LINE_X_PIX_OFFSET, 6 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    g.drawString("Kills: " + kills1, LINE_X_PIX_OFFSET, 7 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    String accuracy1;
    if (shots1 == 0) {
      accuracy1 = "NaN";
    } else {
      double acc1 = 100.0 * hits1 / shots1;
      accuracy1 = scoreFormat.format(acc1);
    }
    g.drawString("Accuracy: " + accuracy1 + "%", LINE_X_PIX_OFFSET, 8 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);

    if (FinalProjectile.MULTIPLAYER) {

      g.setColor(USER2_COLOR[0]);
      g.setFont(new Font("default", Font.BOLD, 14));
      g.drawString("User 2", LINE_X_PIX_OFFSET, 10 * LINE_Y_PIX);
      g.setColor(USER_SCORES[1]);
      g.setFont(new Font("default", Font.PLAIN, 14));
      g.drawString("Shots: " + shots2, LINE_X_PIX_OFFSET, 11 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("Hits: " + hits2, LINE_X_PIX_OFFSET, 12 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("Kills: " + kills2, LINE_X_PIX_OFFSET, 13 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      String accuracy2;
      if (shots2 == 0) {
        accuracy2 = "NaN";
      } else {
        double acc2 = 100.0 * hits2 / shots2;
        accuracy2 = scoreFormat.format(acc2);
      }
      g.drawString("Accuracy: " + accuracy2 + "%", LINE_X_PIX_OFFSET, 14 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
    }
    if (FinalProjectile.debug_scores) {
      System.out.println(shots1 + " " + shots2 + " " + hits1 + " " + hits2 + " DisplayServer.drawScores()");
    }
  }

  /**
   * Draws control key help text.
   *
   * @param g
   */
  protected synchronized void drawHelp(Graphics g) {
    if (HELP) {
      g.setFont(new Font("default", Font.BOLD, 14));
      g.setColor(randomLightColor());
//      g.drawString("User 1 controls: move with [^]UP [v]DOWN [<]LEFT [>]RIGHT keys, shoot with [.]SPACE", LINE_X_PIX_OFFSET, LINE_Y_PIX_OFFSET);
      g.drawString("User 1 Controls", LINE_X_PIX_OFFSET + 110, 4 * LINE_Y_PIX);
      g.setFont(new Font("monospaced", Font.PLAIN, 12));
      g.drawString("[^] speed up", LINE_X_PIX_OFFSET + 110, 5 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("[v] slow down", LINE_X_PIX_OFFSET+110, 6 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("[<] turn left", LINE_X_PIX_OFFSET+110, 7 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("[>] turn right", LINE_X_PIX_OFFSET+110, 8 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("[SPACE] shoot", LINE_X_PIX_OFFSET+230, 5 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
      g.drawString("[H] toggle help menu", LINE_X_PIX_OFFSET+230, 4 * LINE_Y_PIX);


      if (FinalProjectile.MULTIPLAYER) {
        g.setFont(new Font("default", Font.BOLD, 14));
        g.drawString("User 2 Controls", LINE_X_PIX_OFFSET + 110, 10 * LINE_Y_PIX);
        g.setFont(new Font("monospaced", Font.PLAIN, 12));
        g.drawString("[W] speed up", LINE_X_PIX_OFFSET+110, 11 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("[S] slow down", LINE_X_PIX_OFFSET+110, 12 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("[A] turn left", LINE_X_PIX_OFFSET+110, 13 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("[D] turn right", LINE_X_PIX_OFFSET+110, 14 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);
        g.drawString("[SHIFT] shoot", LINE_X_PIX_OFFSET+230, 11 * LINE_Y_PIX + LINE_Y_PIX_OFFSET);

      }
    }
  }

  protected void addClient(Socket client) {
    MessageListener l = new MessageListener(client, this);
    l.start();
  }
/*
  public static void main(String [] argv) {
    double[] pos = {10, 10, 0};

    // construct a single GroundVehicle
//            GroundVehicle gv = new GroundVehicle(
//                    Simulator.randomStartingPosition(),
//                    Simulator.randomDoubleInRange(0, 10),
//                    Simulator.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));

    GroundVehicle gv = new GroundVehicle(pos, 1, 0);


    Thread gvThread = new Thread(gv);

    try {
      ServerSocket s = new ServerSocket(5065);
      s.setReuseAddress(true);
      if (!s.isBound()) {
        System.exit(-1);
      }
      String address = GeneralInetAddress.getLocalHost().getHostAddress();

      DisplayServer d = new DisplayServer(address);
      // create DisplayClient
      DisplayClient dc = new DisplayClient(address);

      // construct a single Simulator
      Simulator sim = new Simulator(dc);
      sim.addVehicle(gv);
      Thread simThread = new Thread(sim);

      // construct a single instance of the CircleController class
      UserController uc = new UserController(sim,gv);
      uc.addDisplayServer(dc);
      Thread ucThread = new Thread(uc);

      gvThread.start();
      ucThread.start();
      simThread.start();

      do {
        Socket client = s.accept();
        d.addClient(client);
      } while (true);
    }
    catch (IOException e) {
      System.err.println("I couldn't create a new socket.\n"+
              "You probably are already running DisplayServer.\n");
      System.err.println(e);
      System.exit(-1);
    }



  }
  */

}
