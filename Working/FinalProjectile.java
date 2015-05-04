import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * FinalProjectile
 * 16.35 Assignment #4 Pre-Deliverable
 * @author Syler Wagner [syler@mit.edu]
 **/

public class FinalProjectile {

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
            uc.addDisplayServer(d);
            Thread ucThread = new Thread(uc);

            gvThread.start();
            ucThread.start();
            simThread.start();

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

            gvThread.start();
            ucThread.start();
            simThread.start();

        } else { // if wrong number of arguments
            System.err.println("Allowed format:");
            System.err.println("$ java FinalProjectile <IP>");
            System.exit(-1);
        }
    }*/
    }
}


