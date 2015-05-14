/**
 * DisplayClient
 * 16.35 FinalProjectile Game Final Project
 * @author  16.35 Staff
 * @author  Syler Wagner        <syler@mit.edu>
 **/

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DisplayClient  {
  PrintWriter output; 
  protected NumberFormat format = new DecimalFormat("#####.##");

  private final boolean print = false;   // set to true for print statements

  public DisplayClient(String host) {
    InetAddress address;
    try {
      address = InetAddress.getByName(host);
      Socket server = new Socket(address, 5065);
      output = new PrintWriter(server.getOutputStream());
    }
    catch (UnknownHostException e) {
      System.err.println("I can't find a host called "+host+". Are you sure you got the name right?");
      System.err.println(e);
      System.exit(-1);
    }
    catch (IOException e) {
      System.err.println("I can't connect to the DisplayServer running on "+host+".\n");
      System.err.println("Did you remember to start the DisplayServer?");
      System.err.println(e);
      System.exit(-1);
    }
  }

  public static void printmsg(String s) {
    if (FinalProjectile.debug_display_msgs) {
      System.out.print(s);
    }
  }

  public void clear() {
    output.println("clear");
  }

  public void traceOn() {
    output.println("traceon");
  }

  public void traceOff() {
    output.println("traceoff");
  }

  //TODO: write requirements
  public void update(int userShots[], int userHits[], int numVehicles, double gvX[], double gvY[], double gvTheta[], double gvC[], int numProjectiles, double pX[], double pY[], double pC[])
  {
    StringBuffer message = new StringBuffer();
    // append vehicle positions
    message.append("vehicles");
    message.append(" ");
    message.append(numVehicles);
    message.append(" ");
    for (int i = 0; i < numVehicles; i++) {
      message.append(format.format(gvX[i])+" "+format.format(gvY[i])+" "+
		     format.format(gvTheta[i])+" "+format.format(gvC[i])+" ");
    }
    // append user scores
    message.append("score");
    message.append(" ");
    message.append(userShots[0] + " " + userShots[1] + " " + userHits[0] + " " + userHits[1] + " ");
    if (FinalProjectile.debug_scores) {
      System.out.println(userShots[0] + " " + userShots[1] + " " + userHits[0] + " " + userHits[1] + " DisplayClient.update()");
    }
    // append projectile positions
    message.append("projectiles");
    message.append(" ");
    message.append(numProjectiles);
    message.append(" ");
    for (int i = 0; i < numProjectiles; i++) {
      message.append(format.format(pX[i])+" "+format.format(pY[i])+" "+format.format(pC[i])+" ");
      printmsg("pC[i] = " + pC[i]);
    }

    if (print)
      System.out.println("Sent "+message);
    output.println(message);
    output.flush();
  }

  /*
  public static void main(String argv[]) throws IOException {
    if (argv.length == 0) {
      System.err.println("Usage: DisplayClient <hostname>\n"+
			 "where <hostname> is where DisplayServer is running");
      System.exit(-1);
    }
    String host = argv[0];

    DisplayClient server = new DisplayClient(host);
    double gvX[] = new double[2];
    double gvY[] = new double[2];
    double gvTheta[] = new double[2];
      
    for (int i = 0; i < 2; i++) {
      gvX[i] = Math.random()*100;
      gvY[i] = Math.random()*100;
      gvTheta[i] = Math.PI*i;
    }

    server.update(2, gvX, gvY, gvTheta);
    System.out.print("Press return to continue...");
    System.in.read();
    server.traceOn();
    gvX[0] = 10;
    gvY[0] = 10;
    gvX[1] = 30;
    gvY[1] = 30;
    server.update(2, gvX, gvY, gvTheta);
    gvX[0] = 90;
    gvY[0] = 10;
    gvX[1] = 70;
    gvY[1] = 30;
    server.update(2, gvX, gvY, gvTheta);
    gvX[0] = 90;
    gvY[0] = 90;
    gvX[1] = 70;
    gvY[1] = 70;
    server.update(2, gvX, gvY, gvTheta);
    gvX[0] = 10;
    gvY[0] = 90;
    gvX[1] = 30;
    gvY[1] = 70;
    server.update(2, gvX, gvY, gvTheta);
    gvX[0] = 10;
    gvY[0] = 10;
    gvX[1] = 30;
    gvY[1] = 30;
    server.update(2, gvX, gvY, gvTheta);
    System.out.print("Press return to continue...");
    System.in.read();
    server.traceOff();
    server.clear();

    for (int i = 0; i < 2; i++) {
      gvX[i] = Math.random()*100;
      gvY[i] = Math.random()*100;
      gvTheta[i] = Math.PI*i;
    }

    server.update(2, gvX, gvY, gvTheta);
    System.out.print("Press return to exit...");
    System.in.read();
  }
  */
}
