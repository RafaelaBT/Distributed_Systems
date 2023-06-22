package Peer;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import RMI.ServerRMIInterface;

public class Peer {

    public static void main(String[] args) throws Exception {
        System.out.println("INITIALIZING PEER...\n");

        System.out.println("INSERTS THE PEER INFORMATION BELOW.");

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("IP Adress: ");
            String ip = scanner.nextLine();

            System.out.println("\nPort: ");
            int port = scanner.nextInt();

            System.out.println("\nDirectory Path: ");
            String path = scanner.nextLine();

            File dir = new File(path);
            String[] files = dir.list();

            Registry reg = LocateRegistry.getRegistry();
            String svName = "rmi://NapsterRMI";
            ServerRMIInterface peerRMI = (ServerRMIInterface) reg.lookup(svName);

            String join = peerRMI.join(ip, port, path, files);
            if (join.compareTo("JOIN_OK") == 0) {
                System.out.print("\nSou peer "+ip+":"+port+" com arquivos");
                for (String fileName : files) {
                    System.out.print(" "+fileName);
                }
                System.out.println(".");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
