package Peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import RMI.PeerRMI;
import RMI.ServerRMIInterface;

public class Peer implements Runnable{
    private Socket sv;

    private Peer() {

    }

    private Peer(Socket sv) {
        this.sv = sv;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(sv.getInputStream());
            String path = dataInputStream.readUTF();

            DataOutputStream dataOutputStream = new DataOutputStream(sv.getOutputStream());

            int bytes = 0;
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);

                dataOutputStream.writeLong(file.length());
                byte[] buffer = new byte[60*1024];
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytes);
                    dataOutputStream.flush();
                }

                fileInputStream.close();
            }
            else {
                throw new Exception("THE FILE DOESN'T EXIST.");
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
        String path = scanner.nextLine();

        return new PeerRMI(ip, port, path);
    }

    private String[] getFiles(String path) {
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

    private void download(String ip, int port, String serverPath, String peerPath, String fileName) throws UnknownHostException, IOException {
        Socket s = new Socket(ip, Integer.valueOf(port));

        int bytes = 0;

        DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
        dataOutputStream.writeUTF(serverPath+"\\"+fileName);

        InputStream in = s.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(in);

        FileOutputStream fileOutputStream = new FileOutputStream(peerPath+"\\"+fileName);
        long size = dataInputStream.readLong();

        byte[] buffer = new byte[4*1024];

        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }

        fileOutputStream.close();
        s.close();
    }

    public static void main(String[] args) throws Exception {
        Peer p = new Peer();
        Scanner scanner = new Scanner(System.in);
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
        String svName = "rmi://NapsterRMI";
        ServerRMIInterface peerRMI = (ServerRMIInterface) reg.lookup(svName);

        String join = "";
        String fileName = "";
        ArrayList<PeerRMI> list = null;
        
        while (true) {
            System.out.print("\n-> MENU <-\n1. JOIN\n2. SEARCH (JOIN FIRST)\n3. DOWNLOAD (JOIN FIRST)\n4. EXIT\nOPTION: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n-> OPTION: JOIN <-");
                    String[] files = p.getFiles(peer.path);
                    join = peerRMI.join(peer.ip, peer.port, peer.path, files);
                    if (join.compareTo("JOIN_OK") == 0) {
                        System.out.println("Sou peer "+peer.ip+":"+peer.port+" com arquivos ");
                        for (String i : files) {
                            System.out.println("- "+i+" ");
                        }
                    }
                    break;
                        
                case "2":
                    if (join.compareTo("JOIN_OK") == 0) {
                        System.out.println("\n-> OPTION: SEARCH <-");
                        System.out.print("File: ");
                        fileName = scanner.nextLine();
                        list = peerRMI.search(fileName);
                        System.out.println("Peers com os arquivo solicitado:");
                        if (list != null) {
                            for (PeerRMI i : list) {
                                System.out.println("- "+i.ip+":"+i.port);
                            }
                        }
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

                            for (PeerRMI i : list) {
                                if ((i.ip.compareTo(ipServer) == 0) && (i.port.compareTo(portServer) == 0)) {
                                    int port = Integer.valueOf(i.port);
                                    p.download(i.ip, port, i.path, peer.path, fileName);

                                    String path = i.path+"\\"+fileName;
                                    if (peerRMI.update(fileName).compareTo("UPDATE_OK") == 0) {
                                        System.out.println("Arquivo "+fileName+" baixado com sucesso na pasta "+path+".");
                                    }   
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