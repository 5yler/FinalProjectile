/**
 * RtTest
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

public class RtTest {

    /* MAIN METHOD */
    public static void main(String[] args) {

        String host;

        if (args.length == 2) { // if one command line argument is present

            // get IP address from first command line argument
            host = args[1];


//            DisplayServer ds = new DisplayServer(host);
            // create DisplayClient
            DisplayClient dc = new DisplayClient(host);

            double[] pos = {10, 10, 0};

            // construct a single GroundVehicle
//            GroundVehicle gv = new GroundVehicle(
//                    Simulator.randomStartingPosition(),
//                    Simulator.randomDoubleInRange(0, 10),
//                    Simulator.randomDoubleInRange(-Math.PI / 4, Math.PI / 4));

            GroundVehicle gv = new GroundVehicle(pos, 0, 0);


            Thread gvThread = new Thread(gv);

            // construct a single Simulator
            Simulator sim = new Simulator(dc);
            sim.addVehicle(gv);
            Thread simThread = new Thread(sim);

            // construct a single instance of the CircleController class
            UserController uc = new UserController(sim,gv);
            Thread ucThread = new Thread(uc);

            gvThread.start();
            ucThread.start();
            simThread.start();

        } else { // if wrong number of arguments
            System.err.println("Allowed format:");
            System.err.println("$ jamaicavm RtTest -realtime <IP>");
            System.exit(-1);
        }
    }
}



