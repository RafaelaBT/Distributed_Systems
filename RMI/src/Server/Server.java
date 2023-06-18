package Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMIDemo.ServiceHour;
import RMIDemo.ServiceHourImplements;

/** Server class: contains the service hour - creates and register the remote object serviceHour.*/
public class Server {
    public static void main(String[] args) throws Exception{
        // Instance/creates the remote object serviceHour.
        ServiceHour serviceHour = new ServiceHourImplements();

        // Creates the registry on port 1099.
        LocateRegistry.createRegistry(1099);

        // Gets the registry.
        Registry reg = LocateRegistry.getRegistry();

        // Binds the remote object serverHour to a name to register it in the registry.
        reg.bind("rmi://127.0.0.1/serviceHour", serviceHour);

        System.out.println("Running server.");
    }
}
