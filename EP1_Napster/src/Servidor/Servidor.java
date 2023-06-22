package Servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.ServerRMIImplements;
import RMI.ServerRMIInterface;

public class Servidor {

    public static void main(String[] args) throws RemoteException {
        System.out.println("INITIALIZING SERVER...\n");

        System.out.println("INSERTS THE SERVER INFORMATION BELOW.");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("IP Adress: ");
            String ip = scanner.nextLine();

            System.out.println("\nRegistry Port: ");
            int regPort = scanner.nextInt();

            LocateRegistry.createRegistry(regPort);

            ServerRMIInterface svStub = new ServerRMIImplements();

            String regName = "rmi://NapsterRMI";

            Registry reg = LocateRegistry.getRegistry(ip, regPort, null);

            reg.bind(regName, svStub);

            System.out.println("\nRUNNING SERVER...");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
