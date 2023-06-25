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
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import RMI.PeerRMI;
import RMI.ServerRMIInterface;

public class Peer implements Runnable{
    Socket sv;

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
            FileInputStream fileInputStream = new FileInputStream(file);

            dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4*1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }

            fileInputStream.close();
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

    public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
        Peer p = new Peer();

        Scanner scanner = new Scanner(System.in);
        PeerRMI peer = p.getInfo(scanner);
        String[] files = p.getFiles(peer.path);

        Registry reg = LocateRegistry.getRegistry();
        String svName = "rmi://NapsterRMI";
        ServerRMIInterface peerRMI = (ServerRMIInterface) reg.lookup(svName);

        peerRMI.peerInfo(peer.ip, peer.port, peer.path);

        System.out.print("\n-> MENU <-\n1. JOIN\n2. SEARCH (JOIN FIRST)\n3. DOWNLOAD\nOPTION: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.println("\n-> OPTION: JOIN <-");
                String result = peerRMI.join(files);
                if (result.compareTo("JOIN_OK") == 0) {
                    System.out.println("Sou peer "+peer.ip+":"+peer.port+" com arquivos ");
                    for (String fileName : files) {
                        System.out.println("- "+fileName+" ");
                    }
                }
                break;
                        
            case "2":
                System.out.println("\n-> OPTION: SEARCH <-");
                System.out.print("File: ");
                String fileName = scanner.nextLine();
                ArrayList<PeerRMI> list = peerRMI.search(fileName);
                System.out.println("Peers com os arquivo solicitado:");
                if (list != null) {
                    for (PeerRMI i : list) {
                        System.out.println("- "+i.ip+":"+i.port);
                    }
                }
                break;

            case "3":
                System.out.println("\n-> OPTION: DOWNLOAD <-");
                String file = scanner.nextLine();
                PeerRMI peerd = peerRMI.search(file).get(0);

                String ipServer = peerd.ip;
                int portServer = Integer.valueOf(peerd.port);

                Socket s = new Socket(ipServer, portServer);

                int bytes = 0;

                DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
                dataOutputStream.writeUTF(peerd.path+"\\"+file);

                InputStream in = s.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(in);
                System.out.println("In");

                System.out.println(peer.path+file);
                FileOutputStream fileOutputStream = new FileOutputStream(peer.path+"\\"+file);
                long size = dataInputStream.readLong();
                System.out.println("fileout");

                byte[] buffer = new byte[4*1024];

                System.out.println("Antes do while");
                while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size 
                ))) != -1) {
                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes;
                }
                s.close();

                peerRMI.update(file);
                break;
                    
            default:
                System.out.println("\n-> INVALID OPTION <-");
                break;
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(peer.port))) {
            while (true) {
                Socket sv = serverSocket.accept(); //BLOCKING
                p = new Peer(sv);
                new Thread(p).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}