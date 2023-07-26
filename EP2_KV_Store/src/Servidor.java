import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import RMI.PeerRMI;

public class Servidor implements Runnable{
    private final String ip;
    private final Integer port;

    private Socket socket;
    private Servidor lead;
    private ArrayList<Servidor> servidores = new ArrayList<>();

    private final ConcurrentMap<String, ArrayList<PeerRMI>> data = new ConcurrentHashMap<>();
    
    public Servidor() throws IOException {
        this.ip = ipRead();
        this.port = portRead();
    }

    public Servidor (String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    private Servidor(Servidor server, Socket socket) {
        this.ip = server.ip;
        this.port = server.port;
        this.lead = server.lead;
        this.socket = socket;
        this.servidores = server.servidores;
    }

    public String ipRead() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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

    private void setLead(Servidor lead) {
        this.lead = lead;
    }

    private void add(Servidor nServer) {
        if (this.equals(lead)) {
            Boolean check = true;
            for (Servidor servidor : servidores) {
                check = !nServer.equals(servidor);
            }
            if (check) {
                servidores.add(nServer);
            }
        }
    }

    private void connect() {
        if (!this.equals(lead)) {
            String con = "CONNECT";
            do {
                try {
                    Socket leadSocket = new Socket(lead.ip, lead.port);

                    InputStreamReader in = new InputStreamReader(leadSocket.getInputStream());
                    BufferedReader receive = new BufferedReader(in);

                    OutputStream out = leadSocket.getOutputStream();
                    DataOutputStream send = new DataOutputStream(out);

                    Gson gson = new Gson();

                    Mensagem message = new Mensagem("ADD", ip, port);
                    String json = gson.toJson(message);

                    send.writeBytes(json+"\n");

                    Mensagem response = gson.fromJson(receive.readLine(), Mensagem.class);
                    con = response.getType();
                
                    in.close();
                    out.close();
                    leadSocket.close();

                } catch (IOException e) {
                    //ignore
                }
            } while (con.compareTo("CONNECT_OK")!=0);
        }
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("-------------------- FEEDBACK --------------------");
            System.out.println("Waiting for connection...\n");

            while (true) {
                Servidor nServer = new Servidor(this, serverSocket.accept());

                InetAddress ipClient = nServer.socket.getInetAddress();
                Integer portClient = nServer.socket.getPort();
                System.out.println("+ Client "+ipClient+" connected successfully at port "+portClient+".\n");
                            
                new Thread(nServer).start();
            }

        } catch (Exception e) {
            System.out.println("COULD NOT CREATE A SOCKET.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            InetAddress ipClient = this.socket.getInetAddress();
            Integer portClient = this.socket.getPort();
            
            InputStreamReader in = new InputStreamReader(this.socket.getInputStream());
            BufferedReader receive = new BufferedReader(in);

            OutputStream out = this.socket.getOutputStream();
            DataOutputStream send = new DataOutputStream(out);

            Gson gson = new Gson();

            Mensagem message = gson.fromJson(receive.readLine(), Mensagem.class);
            String type = message.getType();
            System.out.println("Message received from "+ipClient+" at port "+portClient+": "+type+"\n");

            String response;

            switch (type) {
                case "PUT":
                    //implements
                    System.out.println("Key: "+message.getKey()+". Value: "+message.getValue());

                    response = gson.toJson(new Mensagem("PUT_OK", new Timestamp(0))); //teste
                    send.writeBytes(response+"\n");
                    break;

                case "GET":
                    //implements
                    System.out.println("Key: "+message.getKey()+". Timestamp: "+message.getTimestamp());
                    break;

                case "ADD":
                    this.add(new Servidor(message.getIp(), message.getPort()));

                    response = gson.toJson(new Mensagem("CONNECT_OK"));
                    send.writeBytes(response+"\n");

                    break;
                
                case "REPLICATION":
                    break;
                
                case "REPLICATION_OK":
                    break;
                
                default:
                    // Ignore
                    break;
            }
            
            in.close();
            out.close();

            this.socket.close();

            System.out.println("- Client "+ipClient+" disconnected successfully at port "+portClient+".\n");
        } catch (IOException e) {
            System.out.println("COULD NOT RECEIVE THE CLIENT DATA.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Servidor that = (Servidor) obj;
        return ip.equals(that.ip) && port.equals(that.port);
    }

    public static void main(String[] args) {
        try {
            System.out.println("--------------------- SERVER ---------------------");
            Servidor server = new Servidor();
            System.out.println("------------------- LEAD SERVER ------------------");
            server.setLead(new Servidor());

            server.connect();

            server.startServer();

        } catch (IOException e) {
            System.out.println("COULD NOT CREATE A SERVER.");
            e.printStackTrace();
        }
    }
}