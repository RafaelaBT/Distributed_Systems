package Servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.PeerRMIImplements;
import RMI.PeerRMIInterface;

public class Servidor {

    public static void main(String[] args) throws RemoteException {
        System.out.println("INITIALIZING SERVER...\n");

        System.out.println("INSERTS THE SERVER INFORMATION BELOW.");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("IP Adress: ");
            String ip = scanner.nextLine();

            System.out.println("\nRegistry Port: ");
            int regPort = scanner.nextInt();

            PeerRMIInterface serverRMI = new PeerRMIImplements();
        
            LocateRegistry.createRegistry(regPort);

            Registry reg = LocateRegistry.getRegistry(ip, regPort, null);

            reg.bind("rmi://"+ip+"/serverRMI", serverRMI);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
