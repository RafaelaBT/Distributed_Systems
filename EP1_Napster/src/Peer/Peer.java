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

public class Peer implements Runnable{
    private Socket sv;
    private static String path;
    private ServerRMIInterface peerRMI;

    private Peer() {

    }

    private Peer(Socket sv) {
        this.sv = sv;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(sv.getInputStream());
            String fileName = dataInputStream.readUTF();

            DataOutputStream dataOutputStream = new DataOutputStream(sv.getOutputStream());

            int bytes = 0;
            File file = new File(path+"\\"+fileName);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);

                dataOutputStream.writeLong(file.length());
                byte[] buffer = new byte[10*1024];
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytes);
                    dataOutputStream.flush();
                }

                fileInputStream.close();
            }
            
            sv.close();

        } catch(Exception e){
			e.printStackTrace();
		}
    }

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

    private String[] getFiles() {
        File dir = new File(path);
        ArrayList<String> suffixes = new ArrayList<>(Arrays.asList("mp4", "m4v", "mov", "qt", "avi", "flv", "wmv", "asf", "mpeg", "mpg", "vob", "mkv",    "asf", "rm", "rmvb", "vob", "ts", "dat"));
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                String extension = name.substring(name.lastIndexOf(".")+1);
                return suffixes.contains(extension);
            }
        });
        return files;
    }

    private void download(String ip, int port, String fileName) throws UnknownHostException, IOException {
        Socket s = new Socket(ip, port);

        int bytes = 0;

        DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
        dataOutputStream.writeUTF(fileName);

        DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
        long size = dataInputStream.readLong();

        FileOutputStream fileOutputStream = new FileOutputStream(path+"\\"+fileName);
        byte[] buffer = new byte[10*1024];

        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }

        fileOutputStream.close();
        s.close();
    }

    public String peerJoin(Peer p, PeerRMI peer) throws RemoteException {
        String[] files = p.getFiles();
        String join = peerRMI.join(files, peer);
        if (join.compareTo("JOIN_OK") == 0) {
            System.out.println("Sou peer "+peer.ip+":"+peer.port+" com arquivos ");
            for (String file : files) {
                System.out.println("- "+file+" ");
            }
        }
        return join;
    }

    public ArrayList<PeerRMI> peerSearch(String fileName, ArrayList<PeerRMI> list, PeerRMI peer) throws RemoteException {
        list = peerRMI.search(fileName, peer);
        System.out.println("Peers com os arquivo solicitado:");
        if (list != null) {
            for (PeerRMI i : list) {
                System.out.println("- "+i.ip+":"+i.port);
            }
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        Peer p = new Peer();
        PeerRMI peer = p.getInfo(scanner);

        Thread t = new Thread(new Runnable() {
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(peer.port))) {
                    while (true) {
                        Socket sv = serverSocket.accept();
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

        Registry reg = LocateRegistry.getRegistry();
        p.peerRMI = (ServerRMIInterface) reg.lookup("rmi://NapsterRMI");

        String join = "";
        String fileName = "";
        ArrayList<PeerRMI> list = null;
        
        while (true) {
            System.out.print("\n-> MENU <-\n1. JOIN\n2. SEARCH (JOIN FIRST)\n3. DOWNLOAD (JOIN FIRST)\n4. EXIT\nOPTION: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n-> OPTION: JOIN <-");
                    join = p.peerJoin(p, peer);
                    break;
                        
                case "2":
                    if (join.compareTo("JOIN_OK") == 0) {
                        System.out.println("\n-> OPTION: SEARCH <-");
                        System.out.print("File: ");
                        
                        fileName = scanner.nextLine();
                        list = p.peerSearch(fileName, list, peer);
                    }
                    else {
                        System.out.println("\n-> JOIN THE SERVER FIRST <-");
                    }
                    break;
                    
                case "3":
                    if (join.compareTo("JOIN_OK") == 0) {
                        if (list != null) {
                            System.out.println("\n-> OPTION: DOWNLOAD <-");
                            System.out.print("IP Address: ");
                            String ipServer = scanner.nextLine();
                            System.out.print("Port: ");
                            String portServer = scanner.nextLine();

                            PeerRMI peerSv = new PeerRMI(ipServer, portServer);

                            int size = list.size();
                            while(size > 0) {
                                size --;
                                if (list.get(size).equals(peerSv)) {
                                    int port = Integer.valueOf(peerSv.port);
                                    p.download(peerSv.ip, port, fileName);

                                    if (p.peerRMI.update(fileName, peer).compareTo("UPDATE_OK") == 0) {
                                        System.out.println("Arquivo "+fileName+" baixado com sucesso na pasta "+path+".");
                                    } 

                                    size = 0;
                                }
                            }
                        }
                        else {
                            System.out.println("\n-> SEARCH A EXIST FILE FIRST <-");
                        }
                    }
                    else {
                        System.out.println("\n-> JOIN THE SERVER FIRST <-");
                    }

                    break;
                case "4":
                    System.exit(0);
                    break;
                default:
                    System.out.println("\n-> INVALID OPTION <-");
                    break;
            }
        }
    }
}