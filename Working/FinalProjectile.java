import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * FinalProjectile
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

public class FinalProjectile {

    private static final boolean lead3  = true;    // set to true for multiple LeadingController test
    public static boolean multiplayer  = true;    // set to true for FC debug //TODO:req

    // debug print statement toggle
    public static final boolean debug_follower      = true;    // set to true for FC debug
    public static final boolean debug_projectiles   = false;    // set to true for projectile debug
    public static final boolean debug_keys          = false;    // set to true for key event debug
    public static final boolean debug_scores        = false;    // set to true for score display message debug
    public static final boolean debug_display_msgs  = false;    // set to true for projectile/vehicle display msg debug

    public static final int GAME_TIME = 200; // [s] time the game runs for //TODO: req

    /* MAIN METHOD */
    public static void main(String[] args) {

        //TODO:req
        if (args.length == 1) { // if two command line arguments are present
            if (args[0].equals("1")) {
                multiplayer = false;
                System.out.println("Single-player mode enabled.");
            } else if (args[0].equals("2")) {
                multiplayer = true;
                System.out.println("Multi-player mode enabled.");
            } else { // if the second argument is wrong
                System.err.println("Command line argument must be number of players.");
                System.err.println("To select single-player mode:");
                System.err.println("$ java FinalProjectile 1");
                System.err.println("To select multi-player mode:");
                System.err.println("$ java FinalProjectile 2");
                System.exit(-1);
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

            // construct user 1 GroundVehicle and UserController
            GroundVehicle uv1 = new GroundVehicle(pos1, 1, 0);
            sim.addVehicle(uv1);
            UserController uc1 = new UserController(sim, uv1, ds);
            sim.addUserController(uc1);




            //TODO: make all of this some sort of nice for loop in Simulator?
            if (lead3) {
                GroundVehicle lv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                GroundVehicle lv2 = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                GroundVehicle lv3 = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                LeadingController lc = new LeadingController(sim, lv);
                LeadingController lc2 = new LeadingController(sim, lv2);
                LeadingController lc3 = new LeadingController(sim, lv3);

                sim.addVehicle(lv);
                sim.addVehicle(lv2);
                sim.addVehicle(lv3);
                sim.addLeadingController(lc);
                sim.addLeadingController(lc2);
                sim.addLeadingController(lc3);

//                lc.addFollower(uv1);
//                lc.addFollower(lv2);
//                lc.addFollower(lv3);
//                lc2.addFollower(uv1);
//                lc2.addFollower(lv);
//                lc2.addFollower(lv3);
//                lc3.addFollower(uv1);
//                lc3.addFollower(lv);
//                lc3.addFollower(lv2);
                lv.start();
                lv2.start();
                lv3.start();
                lc.start();
                lc2.start();
                lc3.start();


                if(debug_follower) {
                    GroundVehicle fv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                    FollowingController fc = new FollowingController(sim, fv, uv1);
                    sim.addFollowingController(fc);
                    sim.addVehicle(fv);
                    fv.start();
                    fc.start();
                }

                if (multiplayer) {

                    GroundVehicle uv2 = new GroundVehicle(pos2, 1, 0);
                    sim.addVehicle(uv2);
                    UserController uc2 = new UserController(sim, uv2, ds);
                    sim.addUserController(uc2);

//                    lc.addFollower(uv2);
//                    lc2.addFollower(uv2);
//                    lc3.addFollower(uv2);

                    uv2.start();
                    uc2.start();
                }

            }



            uv1.start();
            uc1.start();
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


