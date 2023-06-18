package Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMIDemo.Hour;
import RMIDemo.ServiceHour;

/** Client class: require the service hour information - looks for a remote object in the server's registry. */
public class Client {
    public static void main(String[] args) throws Exception{
        // Gets the registry.
        Registry reg = LocateRegistry.getRegistry();

        // Looks up the remote object name in the registry and casts (converts) the reference to type ServiceHour.
        ServiceHour clientServiceHour = (ServiceHour) reg.lookup("rmi://127.0.0.1/serviceHour");

        // Gets the serializable object Hour from the remote object clientserviceHour.
        Hour hour = clientServiceHour.getHour("Client1");

        System.out.println("Client name: " + hour.clientName + "\nServer timestamp: " + hour.timestamp + "\n");
    }
}
