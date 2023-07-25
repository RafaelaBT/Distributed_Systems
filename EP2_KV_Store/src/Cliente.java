import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

public class Cliente {
    private Integer qtd = 3;
    private ArrayList<Pair<String, Integer>> servidores = new ArrayList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private String ipRead() throws IOException {
        String ip;
        Boolean valid;
        do {
            System.out.print("IPv4: ");
            ip = reader.readLine();

            valid = isIpv4Valid(ip);

            if (!valid) {
                System.out.println("----------------------");
                System.out.println("Invalid IPv4!");
            }
        } while (!valid);

        return ip;
    }

    private Integer portRead() throws IOException {
        Integer port;
        Boolean valid;

        System.out.println("----------------------");

        do {
            System.out.print("Port: ");
            port = Integer.parseInt(reader.readLine());

            valid = isPortValid(port);

            if (!valid) {
                System.out.println("----------------------");
                System.out.println("Invalid Port!");
            }
            
        } while (!valid);

        return port;
    }

    public Boolean isIpv4Valid(String ip) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    public Boolean isPortValid(Integer port) {
        return port >= 0 && port <= 65535;
    }

    private void receiveSvs() {
        for (int i = 0; i < qtd; i++) {
            try {
                System.out.println("--------------------- SERVER "+(i+1)+" -------------------");
                Pair<String, Integer> server = new Pair<>(ipRead(), portRead());
                servidores.add(server);
            } catch (Exception e) {
                System.out.println("COULD NOT CREATE A SERVER.");
                e.printStackTrace();
            }
        }
    }

    private Pair<String, Integer> randomSv() {
        Random rand = new Random();
        int n = rand.nextInt(qtd);
        return servidores.get(n);
    }

    private void connection() {
        Pair<String, Integer> server = randomSv();
        try {
            Socket socket = new Socket(server.getKey(), server.getValue());
            
            OutputStream out = socket.getOutputStream();
            DataOutputStream send = new DataOutputStream(out);

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader receive = new BufferedReader(in);
            
            String choice;
            String response;
            
            System.out.println("---------------------- MENU ----------------------");      
            do {
                System.out.println("1. Put");
                System.out.println("2. Get");
                System.out.println("3. Exit");
                System.out.print("Option: ");
                choice = reader.readLine();

                switch (choice) {
                    case "1":
                        send.writeBytes(choice+"\n");
                        response = receive.readLine();
                        System.out.println("----------------------");
                        System.out.println("From server ("+socket.getInetAddress()+"): "+response);
                        System.out.println("----------------------");
                        break;

                    case "2":
                        send.writeBytes(choice+"\n");
                        response = receive.readLine();
                        System.out.println("----------------------");
                        System.out.println("From server ("+socket.getInetAddress()+"): "+response);
                        System.out.println("----------------------");
                        break;

                    case "3":
                        send.writeBytes(choice+"\n");
                        response = receive.readLine();
                        System.out.println("----------------------");
                        System.out.println(response);
                        break;

                    default:
                        System.out.println("----------------------");
                        System.out.println("Invalid option!");
                        break;
                }
                
            } while (choice.compareTo("3") != 0);
            
            socket.close();

        } catch (IOException e) {
            System.out.println("COULD NOT CREATE A SOCKET.");
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            String choice;

            System.out.println("---------------------- MENU ----------------------");    
            do {
                System.out.println("1. INIT");
                System.out.print("Option: ");
                choice = reader.readLine();

                if (choice.compareTo("1")!=0) {
                    System.out.println("----------------------");
                    System.out.println("Invalid Option!");
                }

            } while (choice.compareTo("1")!=0);

            this.receiveSvs();

            this.connection();

        } catch (IOException e) {
            System.out.println("COULD NOT RECEIVE THE INPUT.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Cliente client = new Cliente();
        client.start();
    }
}
