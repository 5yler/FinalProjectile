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

    /* MAIN METHOD */
    public static void main(String[] args) {


        double[] pos = {10, 10, 0};

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

            GroundVehicle gv = new GroundVehicle(pos, 1, 0);


            Thread gvThread = new Thread(gv);
            // construct a single Simulator
            Simulator sim = new Simulator(dc);
            sim.addVehicle(gv);
            Thread simThread = new Thread(sim);

            // construct a single instance of the CircleController class
            UserController uc = new UserController(sim, gv, d);
            sim.addUserController(uc);
//            d.addUserController(uc);
//            uc.addDisplayServer(d);
            Thread ucThread = new Thread(uc);

            //TODO: make all of this some sort of nice for loop in Simulator?
            if (lead3) {
                GroundVehicle lv = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                GroundVehicle lv2 = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                GroundVehicle lv3 = new GroundVehicle(sim.randomStartingPosition(), sim.randomDoubleInRange(0, 10), sim.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));
                LeadingController lc = new LeadingController(sim, lv);
                LeadingController lc2 = new LeadingController(sim, lv2);
                LeadingController lc3 = new LeadingController(sim, lv3);
//                FollowingController fc = null;
//                FollowingController fc2 = null;
//                FollowingController fc3 = null;
                sim.addVehicle(lv);
                sim.addVehicle(lv2);
                sim.addVehicle(lv3);
                sim.addLeadingController(lc);
                sim.addLeadingController(lc2);
                sim.addLeadingController(lc3);
//                sim.addFollowingController(fc);
//                sim.addFollowingController(fc2);
//                sim.addFollowingController(fc3);
                
//                Thread lvThread = new Thread(lv);
//                Thread lv2Thread = new Thread(lv2);
//                Thread lv3Thread = new Thread(lv3);
//                Thread lcThread = new Thread(lc);
//                Thread lc2Thread = new Thread(lc2);
//                Thread lc3Thread = new Thread(lc3);

                lc.addFollower(gv);
                lc.addFollower(lv2);
                lc.addFollower(lv3);
                lc2.addFollower(gv);
                lc2.addFollower(lv);
                lc2.addFollower(lv3);
                lc3.addFollower(gv);
                lc3.addFollower(lv);
                lc3.addFollower(lv2);
                lv.start();
                lv2.start();
                lv3.start();
                lc.start();
                lc2.start();
                lc3.start();
            }

            gv.start();
            uc.start();
            sim.start();


            do {
                Socket client = s.accept();
                d.addClient(client);
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


