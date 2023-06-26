package Servidor;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.ServerRMIImplements;
import RMI.ServerRMIInterface;

/**
 * RMI server class. Resposible for running the server.
 */
public class Servidor {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        // Note: All souts are for inform the user when to input data.
        // Reference: http://www.beginwithjava.com/java/inputoutput/reading-keyboard-input.html

        // Declares (open) the new scanner object with the standard input device System.in to read the user keyboard input.
        Scanner scanner = new Scanner(System.in);

        System.out.println("-> SERVER INFORMATIONS <-");

        // Gets the server ip address.
        System.out.print("IP Adress: ");
        String ip = scanner.nextLine();

        // Gets the server port.
        System.out.print("Registry Port: ");
        int regPort = scanner.nextInt();

        // Close the scanner object.
        scanner.close();

        // Creates the server registry to contains the references (stubs) to the remote objects (with the peers informations).
        LocateRegistry.createRegistry(regPort);

        // Gets the registry created.
        Registry reg = LocateRegistry.getRegistry(ip, regPort, null);

        // Binds the registry name to the server stub.
        String regName = "rmi://NapsterRMI";
        ServerRMIInterface svStub = new ServerRMIImplements();
        reg.bind(regName, svStub);

        // Prints "RUNNING SERVER" to inform the user when the server is running
        System.out.println("\nRUNNING SERVER...");

    }
}
