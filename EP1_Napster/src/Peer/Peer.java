package Peer;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import RMI.PeerRMI;
import RMI.ServerRMIInterface;

/**
 * Peer class. Responsible for running the peer.
 * Implements the Runnable interface to create Threads.
 */
public class Peer implements Runnable{
    // Peer attributes.
    private Socket sv;
    private static String path;
    private ServerRMIInterface peerRMI;

    /**
     * Generic constructor to get access to Peer methods.
     */
    private Peer() {

    }

    /**
     * Especific constructor to pass the server socket to the TCP Thread.
     * @param sv - the server socket.
     */
    private Peer(Socket sv) {
        this.sv = sv;
    }

    /**
     * Gets the peer informations (ip, port and directory path) from the user keyboard.
     * @param scanner - the scanner to read the keyboard input (Scanner)
     * @return the peer (PeerRMI)
     */
    private PeerRMI getInfo(Scanner scanner) {
        System.out.println("-> INFORMATIONS <-");

        System.out.print("IP Adress: ");
        String ip = scanner.nextLine();

        System.out.print("Port: ");
        String port = scanner.nextLine();

        System.out.print("Directory Path: ");
        path = scanner.nextLine();

        return new PeerRMI(ip, port);
    }

    /**
     * Run method responsible for send the file to the extern peer via TCP.
     */
    @Override
    public void run() {
        try {
            // Creates the channel to the input data stream to receive the searched filename.
            DataInputStream dataInputStream = new DataInputStream(sv.getInputStream());
            String fileName = dataInputStream.readUTF();

            // Creates the channel to the output data stream to send the file data.
            DataOutputStream dataOutputStream = new DataOutputStream(sv.getOutputStream());

            int bytes = 0;

            // Gets the file.
            File file = new File(path+"\\"+fileName);

            if (file.exists()) {
                // If the file exists, creates a channel to receive the file data.
                FileInputStream fileInputStream = new FileInputStream(file);

                // Sends the file size.
                dataOutputStream.writeLong(file.length());

                // Creates a 10KB buffer to receive the file data.
                byte[] packet = new byte[10*1024];

                while ((bytes = fileInputStream.read(packet)) != -1) {
                    /* The buffer receive 10KB maximum.
                    * While the buffer length is not -1 (no more data to send), sends the 10KB packets. */
                    dataOutputStream.write(packet, 0, bytes);
                    dataOutputStream.flush();
                }

                // Closes the file channel.
                fileInputStream.close();
            }
            
            // Closes the connection.
            sv.close();

        } catch(Exception e){
			e.printStackTrace();
		}
    }

    /**
     * Gets the peer video files.
     * @return the list of files (String [])
     */
    private String[] getFiles() {
        // Gets the peer directory.
        File dir = new File(path);

        // List of accepted file extensions.
        ArrayList<String> suffixes = new ArrayList<>(Arrays.asList("mp4", "m4v", "mov", "qt", "avi", "flv", "wmv", "asf", "mpeg", "mpg", "vob", "mkv",    "asf", "rm", "rmvb", "vob", "ts", "dat"));
        
        // Creates a list of files that match the extensions.
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                // Gets the file extension.
                String extension = name.substring(name.lastIndexOf(".")+1);

                // Returns true if the extension is in the list of extension.
                return suffixes.contains(extension);
            }
        });

        // Returns the peer's list of files.
        return files;
    }

    /**
     * Gets the peer files and requires to join the server via RMI.
     * If the peer successfully joined, the peer information is printed on the console.
     * Returns the string "JOIN_OK".
     * @param p - this object class to access the getFiles method (Peer)
     * @param peer - the peer (PeerRMI)
     * @return "JOIN OK" (String)
     * @throws RemoteException
     */
    public String peerJoin(Peer p, PeerRMI peer) throws RemoteException {
        // Gets the list of files.
        String[] files = p.getFiles();

        // Calls the RMI join method.
        String join = peerRMI.join(files, peer);

        if (join.compareTo("JOIN_OK") == 0) {
            // If the peer successfully joined, prints the peer information.
            System.out.println("Sou peer "+peer.ip+":"+peer.port+" com arquivos:");
            if (files != null) {
                for (String file : files) {
                    System.out.println("- "+file+" ");
                }
            }
        }
        // Returns "JOIN_OK"
        return join;
    }

    /**
     * Searches (via RMI) the list of peers wich contains the file.
     * Print the list of peers on the console and return the list.
     * @param fileName - the filename to be searched (String)
     * @param peer - the peer (PeerRMI)
     * @return the list of peers (ArrayList)
     * @throws RemoteException
     */
    public ArrayList<PeerRMI> peerSearch(String fileName,  PeerRMI peer) throws RemoteException {
        // Calls the RMI search method.
        ArrayList<PeerRMI> list = peerRMI.search(fileName, peer);

        // Prints the list of peers if the list is not null.
        System.out.println("Peers com os arquivo solicitado:");
        if (list != null) {
            for (PeerRMI i : list) {
                System.out.println("- "+i.ip+":"+i.port);
            }
        }

        // Returns the list of peers.
        return list;
    }

    /**
     * Downloads the fragmented file from a server via TCP, receiving 10KB packets.
     * @param ip - server ip (String)
     * @param port - server port (int)
     * @param fileName - filename (String)
     * @throws UnknownHostException
     * @throws IOException
     */
    private void download(String ip, int port, String fileName) throws UnknownHostException, IOException {
        // Try to create a connection to the server.
        Socket s = new Socket(ip, port);

        int bytes = 0;

        // Creates a channel to stream a data output and send the filename to the server.
        DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
        dataOutputStream.writeUTF(fileName);

        // Creates a channel to receive the file size.
        DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
        long size = dataInputStream.readLong();

        // Creates a channel to receive de file data.
        FileOutputStream fileOutputStream = new FileOutputStream(path+"\\"+fileName);

        // Creates a 10KB buffer to receive the data packets.
        byte[] packet = new byte[10*1024];

        while (size > 0 && (bytes = dataInputStream.read(packet, 0, (int)Math.min(packet.length, size))) != -1) {
            // Receives the 10KB file packets while has not received all the file data and the buffer size is not -1 (no more data to receive).
            fileOutputStream.write(packet, 0, bytes);
            size -= bytes;
        }

        // Closes the file data ouput channel.
        fileOutputStream.close();

        // Closes the socket.
        s.close();
    }

    public static void main(String[] args) throws Exception {
        // Creates the Scanner to read the keyboard input.
        Scanner scanner = new Scanner(System.in);

        // Creates the Peer object to get access to the methods.
        Peer p = new Peer();

        // Gets the peer information via keyboard.
        PeerRMI peer = p.getInfo(scanner);

        // Creates and starts a new Thread to run the TCP peer server.
        Thread t = new Thread(new Runnable() {
            public void run() {
                // Creates the peer server socket.
                try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(peer.port))) {
                    while (true) {
                        // Creates a socket if a extern peer connects
                        Socket sv = serverSocket.accept();

                        // Starts a thread to send a file to the extern peer
                        Peer p = new Peer(sv);
                        new Thread(p).start();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        // Gets the RMI server registry.
        Registry reg = LocateRegistry.getRegistry();
        p.peerRMI = (ServerRMIInterface) reg.lookup("rmi://NapsterRMI");

        // Declares variables to validate the user input in the loop.
        String join = "";
        String fileName = "";
        ArrayList<PeerRMI> list = null;
        
        while (true) {
            // Gets the choosen menu option.
            System.out.print("\n-> MENU <-\n1. JOIN\n2. SEARCH (JOIN FIRST)\n3. DOWNLOAD (JOIN FIRST)\n4. EXIT\nOPTION: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n-> OPTION: JOIN <-");
                    
                    // Calls the peerJoin method.
                    join = p.peerJoin(p, peer);
                    break;
                        
                case "2":
                    if (join.compareTo("JOIN_OK") == 0) {
                        System.out.println("\n-> OPTION: SEARCH <-");
                        System.out.print("File: ");
                        
                        /*  If the peer joined to the server
                        * Gets the filename to be searched and calls the peerSearch method. */
                        fileName = scanner.nextLine();
                        list = p.peerSearch(fileName, peer);
                    }
                    else {
                        // If the peer has not joined the server.
                        System.out.println("\n-> JOIN THE SERVER FIRST <-");
                    }
                    break;
                    
                case "3":
                    if (join.compareTo("JOIN_OK") == 0) {
                        if (list != null) {
                            System.out.println("\n-> OPTION: DOWNLOAD <-");

                            /* If the peer joined the server and a peer has the file
                            * Gets the choosen peer information. */
                            System.out.print("IP Address: ");
                            String ipServer = scanner.nextLine();
                            System.out.print("Port: ");
                            String portServer = scanner.nextLine();

                            // Creates a PeerRMI object with the choosen peer information
                            PeerRMI peerSv = new PeerRMI(ipServer, portServer);

                            // Gets the list of files size.
                            int size = list.size();
                            while(size > 0) {
                                size --;

                                // Loops through the list of files checking if the chosen peer is in the list of peers.
                                if (list.get(size).equals(peerSv)) {

                                    // Calls the download method.
                                    int port = Integer.valueOf(peerSv.port);
                                    p.download(peerSv.ip, port, fileName);

                                    // Updates the file's list of peers.
                                    if (p.peerRMI.update(fileName, peer).compareTo("UPDATE_OK") == 0) {
                                        System.out.println("Arquivo "+fileName+" baixado com sucesso na pasta "+path+".");
                                    } 

                                    // Exit the loop.
                                    size = -1;
                                }
                            }

                            // If the loop didn't find the chosen peer. 
                            if (size == 0) {
                                System.out.println("\n-> THE CHOSEN PEER DOESN'T EXISTS OR DOESN'T HAVE THE FILE <-");
                            }
                        }
                        else {
                            // If the file was not searched.
                            System.out.println("\n-> SEARCH A EXIST FILE FIRST <-");
                        }
                    }
                    else {
                        // If the peer has not joined the server.
                        System.out.println("\n-> JOIN THE SERVER FIRST <-");
                    }

                    break;
                case "4":
                    // Exit the program.
                    System.exit(0);
                    break;
                default:
                    // If the choosen menu option doesn't exist.
                    System.out.println("\n-> INVALID OPTION <-");
                    break;
            }
        }
    }
}