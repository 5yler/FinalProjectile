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

    private static final boolean lead3  = true;    // set to true for multiple LeadingController test
    public static boolean MULTIPLAYER;    // multiplaye mode

    // debug print statement toggle
    public static final boolean debug_follower      = true;    // set to true for FC debug
    public static final boolean debug_user          = true;    // set to true for FC debug
    public static final boolean debug_projectiles   = false;    // set to true for projectile debug
    public static final boolean debug_keys          = false;    // set to true for key event debug
    public static final boolean debug_scores        = false;    // set to true for score display message debug
    public static final boolean debug_display_msgs  = false;    // set to true for projectile/vehicle display msg debug


    // [ms] time between successive updates for various threads
    public static final int SIMULATOR_MS = 50;  //TODO: req
    public static final int VEHICLE_MS = 50;  //TODO: req
    public static final int CONTROLLER_MS = 100;  //TODO: req
    public static final int PROJECTILE_MS = 20;  //TODO: req

    // colors
    public static final int USER1_COLOR = 1;
    public static final int USER2_COLOR = 2;
    public static final int LEADING_COLOR = 3;
    public static final int FOLLOWING_COLOR = 4;

    public static final int GAME_TIME = 200; // [s] time the game runs for //TODO: req
    //TODO: make it stop when all non-user vehicles disappear

    public static int NUM_VEHICLES;

    /* MAIN METHOD */
    public static void main(String[] args) {

        NUM_VEHICLES = 10;

        //TODO:req
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

//            System.out.println("User 1 controls: move with [^]UP [v]DOWN [<]LEFT [>]RIGHT keys, shoot with [.]SPACE");
        if (MULTIPLAYER) {
//            System.out.println("User 2 controls: move with [^]W  [v]S    [<]A    [>]D     keys, shoot with [.]SHIFT");
        }

        // sleep for two seconds so user can read controls
        try {
            Thread.sleep(5*100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        double[] pos1 = {10, 10, 0};
        double[] pos2 = {Simulator.SIM_X-10, Simulator.SIM_Y-10, Math.PI};

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
            for (int i = 1; i < NUM_VEHICLES; i++) {
                GroundVehicle gv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                VehicleController vc = new LeadingController(sim, gv);
                sim.addVehicle(gv);
                gv.start();
                vc.start();
            }

            if(debug_follower) {
                GroundVehicle fv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                FollowingController fc = new FollowingController(sim, fv, uv1);
                sim.addFollowingController(fc);
                sim.addVehicle(fv);
                fv.start();
                fc.start();
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

//            DisplayServer ds = new DisplayServer(host);
        // create DisplayClient
/*
            double[] pos = {10, 10, 0};

            // construct a single GroundVehicle
//            GroundVehicle gv = new GroundVehicle(
//                    Simulator.randomStartingPosition(),
//                    Simulator.randomDoubleInRange(0, 10),
//                    Simulator.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));

            GroundVehicle gv = new GroundVehicle(pos, 1, 0);


            Thread gvThread = new Thread(gv);

            // construct a single Simulator
            Simulator sim = new Simulator(dc);
            sim.addVehicle(gv);
            Thread simThread = new Thread(sim);

            // construct a single instance of the CircleController class
            UserController uc = new UserController(sim,gv);
            Thread ucThread = new Thread(uc);

            gv.start();
            uc.start();
            sim.start();

        } else { // if wrong number of arguments
            System.err.println("Allowed format:");
            System.err.println("$ java FinalProjectile <IP>");
            System.exit(-1);
        }
    }*/
    }
}


