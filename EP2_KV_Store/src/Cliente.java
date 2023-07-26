import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import javafx.util.Pair;

public class Cliente {
    private final HashMap<String, Pair<String, Timestamp>> data = new HashMap<>();

    private Integer qtd = 3;
    private ArrayList<Pair<String, Integer>> servidores = new ArrayList<>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public String ipRead() throws IOException {
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

    public Integer portRead() throws IOException {
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

    private String putKV() {
        String key = null;
        try {
            System.out.println("---------------------- PUT -----------------------");

            System.out.print("Key: ");
            key = reader.readLine();

            System.out.print("Value: ");
            String value = reader.readLine();

            if (!data.containsKey(key)) {
                Timestamp timestamp = new Timestamp(0);
                Pair<String, Timestamp> pair = new Pair<>(value, timestamp);
                data.put(key, pair);
            }

        } catch (IOException e) {
            System.out.println("COULD NOT READ THE INPUT.");
            e.printStackTrace();
        }
        return key;
    }

    private void connect(String op) {
        Pair<String, Integer> server = randomSv();
        try {
            Socket socket = new Socket(server.getKey(), server.getValue());
            
            OutputStream out = socket.getOutputStream();
            DataOutputStream send = new DataOutputStream(out);

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader receive = new BufferedReader(in);

            Gson gson = new Gson();
            Mensagem message;
            Mensagem response;

            String key;

            if (op.compareTo("1")==0) {
                key = putKV();
                String value = data.get(key).getKey();

                message = new Mensagem("PUT", key, value);
                send.writeBytes(gson.toJson(message)+"\n");

                response = gson.fromJson(receive.readLine(), Mensagem.class);

                if (response.getType().compareTo("PUT_OK")==0) {
                    Timestamp timestamp = response.getTimestamp();
                    Pair<String, Timestamp> pair = new Pair<>(value, timestamp);
                    data.put(key, pair);
                    
                    System.out.println("----------------------");
                    System.out.println("PUT_OK key: "+key+" value: "+value+" timestamp "+timestamp+" realizada no servidor "+server.getKey()+":"+server.getValue()+".");
                    System.out.println("----------------------");
                }
            } else {
                System.out.println("---------------------- GET -----------------------");
                System.out.print("Key: ");
                key = reader.readLine();

                Timestamp timestamp = new Timestamp(0);

                if (data.containsKey(key)) {
                    timestamp = data.get(key).getValue();
                }

                message = new Mensagem("GET", key, timestamp);
                send.writeBytes(gson.toJson(message)+"\n");

                /*response = receive.readLine();
                System.out.println("----------------------");
                System.out.println("From server ("+socket.getInetAddress()+"): "+response);
                System.out.println("----------------------");*/
            }
            socket.close();
                    
        } catch (IOException e) {
            System.out.println("COULD NOT CREATE A SOCKET.");
            e.printStackTrace();
        }
    }

    private void menu() {
        System.out.println("---------------------- MENU ----------------------");
        String op = null;

        do { 
            System.out.println("1. PUT");
            System.out.println("2. GET");
            System.out.println("3. EXIT");
            System.out.print("Option: ");

            try {
                op = reader.readLine();
            } catch (IOException e) {
                System.out.println("COULD NOT READ THE INPUT.");
            }
            
            if (op.compareTo("1")==0 || op.compareTo("2")==0) {
                this.connect(op);
            } else if (op.compareTo("3")==0) {
                System.out.println("----------------------");
                System.out.println("Exiting...");
            } else {
                System.out.println("----------------------");
                System.out.println("Invalid option!");
            }
        } while (op.compareTo("3") != 0);
    }

    private void start() {
        try {
            String op;

            System.out.println("---------------------- MENU ----------------------");    
            do {
                System.out.println("1. INIT");
                System.out.print("Option: ");
                op = reader.readLine();

                if (op.compareTo("1")!=0) {
                    System.out.println("----------------------");
                    System.out.println("Invalid Option!");
                }

            } while (op.compareTo("1")!=0);

            this.receiveSvs();

            this.menu();

        } catch (IOException e) {
            System.out.println("COULD NOT READ THE INPUT.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Cliente client = new Cliente();
        client.start();
    }
}
