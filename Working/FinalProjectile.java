/**
 * FinalProjectile
 * 16.35 FinalProjectile Game Final Project
 * @author  Syler Wagner        <syler@mit.edu>
 * @author  Caitlin Wheatley    <caitkw@mit.edu>
 **/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FinalProjectile {

    public static boolean MULTIPLAYER;      // multiplaye mode
    public static int NUM_VEHICLES;         // number of non-user vehicles

    public static final int GAME_TIME = 200;          // [s] time the game runs for /
    public static final int GAME_OVER_TIMEOUT = 10;   // [s] time the display remains after game over

    // [ms] time between successive updates for various threads
    public static final int SIMULATOR_MS = 50;
    public static final int VEHICLE_MS = 50;
    public static final int CONTROLLER_MS = 100;
    public static final int PROJECTILE_MS = 20;

    // debug print statement toggle
    public static final boolean debug_user          = false;    // set to true for user controller debug
    public static final boolean debug_projectiles   = false;    // set to true for projectile debug
    public static final boolean debug_keys          = false;    // set to true for key event debug
    public static final boolean debug_scores        = false;    // set to true for score display message debug
    public static final boolean debug_display_msgs  = false;    // set to true for projectile/vehicle display msg debug

    /* MAIN METHOD */
    public static void main(String[] args) {

        NUM_VEHICLES = 10;

        if (args.length >= 1) { // if two command line arguments are present
            if (args[0].equals("1")) {
                MULTIPLAYER = false;
                System.out.println("Single-player mode enabled.");
            } else if (args[0].equals("2")) {
                MULTIPLAYER = true;
                System.out.println("Multi-player mode enabled.");
            } else { // if the second argument is wrong
                System.err.println("First command line argument must be number of players.");
                System.err.println("To select single-player mode:");
                System.err.println("$ java FinalProjectile 1");
                System.err.println("To select multi-player mode:");
                System.err.println("$ java FinalProjectile 2");
                System.exit(-1);
            }
            if (args.length == 2) {
                try {
                    // try to parse the first argument as as integer
                    if (Integer.parseInt(args[1]) == 0) {
                        System.err.println("Number of vehicles must be greater than zero.");
                        System.exit(-1);
                    } else {
                        NUM_VEHICLES = Integer.parseInt(args[1]);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("First argument must be an integer.");
                    System.exit(-1);
                }
            } else if (args.length > 2) {
                System.err.println("Number of command line arguments must be less than or equal to 2.");
                System.err.println("Example usage:");
                System.err.println("$ java FinalProjectile <num players> <num non-player vehicles>");

            }
        } else {
            MULTIPLAYER = false;
            System.out.println("Single player mode enabled.");
        }

        double[] pos1 = {10, 10, 0};
        double[] pos2 = {Simulator.SIM_X - 10, Simulator.SIM_Y - 10, Math.PI};

        try {
            ServerSocket s = new ServerSocket(5065);
            s.setReuseAddress(true);
            if (!s.isBound()) {
                System.exit(-1);
            }
            String address = GeneralInetAddress.getLocalHost().getHostAddress();

            // create local DisplayServer and DisplayClient
            DisplayServer ds = new DisplayServer(address);
            DisplayClient dc = new DisplayClient(address);

            // construct a single Simulator
            Simulator sim = new Simulator(dc);

            // construct user 1 GroundVehicle and UserController
            GroundVehicle uv1 = new GroundVehicle(pos1, 1, 0);
            sim.addVehicle(uv1);
            UserController uc1 = new UserController(sim, uv1, ds);
            sim.addUserController(uc1);

            // create non-user vehicles
            for (int i = 0; i < NUM_VEHICLES; i++) {
                GroundVehicle gv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                VehicleController vc = new LeadingController(sim, gv);
                sim.addVehicle(gv);
                gv.start();
                vc.start();
            }

            uv1.start();
            uc1.start();

            if (MULTIPLAYER) {

                GroundVehicle uv2 = new GroundVehicle(pos2, 1, 0);
                sim.addVehicle(uv2);
                UserController uc2 = new UserController(sim, uv2, ds);
                sim.addUserController(uc2);

                uv2.start();
                uc2.start();
            }

            sim.start();

            do {
                Socket client = s.accept();
                ds.addClient(client);
            } while (true);
        } catch (IOException e) {
            System.err.println("I couldn't create a new socket.\n" +
                    "You probably are already running DisplayServer.\n");
            System.err.println(e);
            System.exit(-1);
        }
    }
}


