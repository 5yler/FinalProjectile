// Syler's test pad


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Scratchpad
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

public class Scratch {

    private static final boolean lead  = true;    // set to true for multiple LeadingController test
    public static boolean multiplayer  = false;    // set to true for FC debug //TODO:req

    // debug print statement toggle
    public static final boolean debug_follower      = true;    // set to true for FC debug
    public static final boolean debug_projectiles   = false;    // set to true for FC debug
    public static final boolean debug_keys          = false;    // set to true for FC debug
    public static final boolean debug_display_msgs  = false;    // set to true for FC debug


    /* MAIN METHOD */
    public static void main(String[] args) {

        int nVehicles = 10;

        //TODO:req
        if (args.length >= 1) { // if two command line arguments are present
            if (args[0].equals("1")) {
                multiplayer = false;
                System.out.println("Single-player mode enabled.");
            } else if (args[0].equals("2")) {
                multiplayer = true;
                System.out.println("Multi-player mode enabled.");
            } else { // if the second argument is wrong
                System.err.println("First command line argument must be number of players.");
                System.err.println("To select single-player mode:");
                System.err.println("$ java Scratch 1");
                System.err.println("To select multi-player mode:");
                System.err.println("$ java Scratch 2");
                System.exit(-1);
            }
            if (args.length == 2) {
                try {
                    // try to parse the first argument as as integer
                    if (Integer.parseInt(args[1]) == 0) {
                        System.err.println("Number of vehicles must be greater than zero.");
                        System.exit(-1);
                    } else {
                        nVehicles = Integer.parseInt(args[1]);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("2nd argument must be an integer.");
                    System.exit(-1);
                }
            }
            } else {
            multiplayer = false;
            System.out.println("Single player mode enabled.");
        }

        System.out.println("User 1 controls: move with [^]UP [v]DOWN [<]LEFT [>]RIGHT keys, shoot with [.]SPACE");
        if (multiplayer) {
            System.out.println("User 2 controls: move with [^]W  [v]S    [<]A    [>]D     keys, shoot with [.]SHIFT");
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
//
//            // construct user 1 GroundVehicle and UserController
//            GroundVehicle uv1 = new GroundVehicle(pos1, 1, 0);
//            sim.addVehicle(uv1);
//            UserController uc1 = new UserController(sim, uv1, ds);
//            sim.addUserController(uc1);
//
//
//

            //TODO: make all of this some sort of nice for loop in Simulator?
            if (debug_follower) { // have vehicles follow LeadingController

                GroundVehicle lv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                LeadingController lc = new LeadingController(sim, lv);
                sim.addVehicle(lv);
                lv.start();
                lc.start();
                // create n ground vehicles at random positions and velocities
                for (int i = 1; i < nVehicles; i++) {

                    GroundVehicle gv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                    VehicleController vc = new FollowingController(sim, gv, lv);
                    sim.addVehicle(gv);
                    gv.start();
                    vc.start();
                }
            }
//
//
//            if (multiplayer) {
//
//                GroundVehicle uv2 = new GroundVehicle(pos2, 1, 0);
//                sim.addVehicle(uv2);
//                UserController uc2 = new UserController(sim, uv2, ds);
//                sim.addUserController(uc2);
//
////                    lc.addFollower(uv2);
////                    lc2.addFollower(uv2);
////                    lc3.addFollower(uv2);
//
//                uv2.start();
//                uc2.start();
//            }
//
//
//
//
//
//            uv1.start();
//            uc1.start();
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



