package Servidor;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.ServerRMIImplements;
import RMI.ServerRMIInterface;

public class Servidor {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("-> SERVER INFORMATIONS <-");

        System.out.print("IP Adress: ");
        String ip = scanner.nextLine();

        System.out.print("Registry Port: ");
        int regPort = scanner.nextInt();

        scanner.close();

        LocateRegistry.createRegistry(regPort);

        Registry reg = LocateRegistry.getRegistry(ip, regPort, null);
        String regName = "rmi://NapsterRMI";
        ServerRMIInterface svStub = new ServerRMIImplements();

        reg.bind(regName, svStub);

        System.out.println("\nRUNNING SERVER...");
    }
}
