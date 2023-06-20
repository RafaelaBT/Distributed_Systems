package Peer;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.PeerRMIInterface;

public class Peer {

    public static void main(String[] args) throws Exception {
        System.out.println("INITIALIZING PEER...\n");

        System.out.println("INSERTS THE PEER INFORMATION BELOW.");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("IP Adress: ");
            String ip = scanner.nextLine();

            System.out.println("\nDirectory Path: ");
            String path = scanner.nextLine();

            System.out.println("\nPort: ");
            int port = scanner.nextInt();

            File dir = new File(path);

            String[] peerFiles = dir.list();

            Registry reg = LocateRegistry.getRegistry();

            PeerRMIInterface peerRMI = (PeerRMIInterface) reg.lookup("rmi://127.0.0.1/serverRMI");

            String peer = peerRMI.join(ip, port, path, peerFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
