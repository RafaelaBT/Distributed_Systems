import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Server class. */
public class Servidor implements Runnable{
    // Server attributes
    private final InetAddress ip;
    private final Integer port;
    private Servidor lead;
    private Socket socket;
    
    public Servidor() throws IOException {
        this.ip = ipRead();
        this.port = portRead();
    }

    private InetAddress ipRead() throws IOException {
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

        return InetAddress.getByName(ip);
    }

    private Integer portRead() throws IOException {
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
    // https://acervolima.com/diferenca-entre-a-classe-scanner-e-bufferreader-em-java/

    /**
     * Check the server IPv4 adress.
     * @param ip
     * @return Boolean
     */
    public Boolean isIpv4Valid(String ip) {
        String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    // https://www.baeldung.com/java-validate-ipv4-address

    /**
     * Check the TCP server port number.
     * @param port
     * @return Boolean
     */
    public Boolean isPortValid(Integer port) {
        return port >= 0 && port <= 65535;
    }
    // https://simplesolution.dev/java-check-a-valid-tcp-port-number/#:~:text=PortUtils.java%20public%20class%20PortUtils%20%7B%20%2F%2A%2A%20%2A%20This,%3E%3D%200%20%26%26%20portNumber%20%3C%3D%2065535%3B%20%7D%20%7D

    public void setLead(Servidor lead) {
        this.lead = lead;
    }

    private void connection() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("-------------------- FEEDBACK --------------------");
            System.out.println("Waiting for connection...\n");

            while (true) {
                socket = serverSocket.accept();

                InetAddress ipClient = socket.getInetAddress();
                Integer portClient = socket.getPort();
                System.out.println("+ Client "+ipClient+" connected successfully at port "+portClient+".\n");
                            
                new Thread(this).start();
            }
        } catch (Exception e) {
            System.out.println("COULD NOT CREATE A SOCKET.");
            e.printStackTrace();
        }
    }

    // https://www.javatpoint.com/why-does-bufferedreader-throw-ioexception-in-java
    public static void main(String[] args) {
        try {
            System.out.println("--------------------- SERVER ---------------------");
            Servidor server = new Servidor();
            System.out.println("------------------- LEAD SERVER ------------------");
            server.setLead(new Servidor());

            new Thread( new Runnable() {
                @Override
                public void run() {
                    server.connection();
                }
            }).start();

        } catch (IOException e) {
            System.out.println("COULD NOT CREATE A SERVER.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            InetAddress ipClient = socket.getInetAddress();
            Integer portClient = socket.getPort();
            
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader receive = new BufferedReader(in);

            OutputStream out = socket.getOutputStream();
            DataOutputStream send = new DataOutputStream(out);

            String message;

            do {
                message = receive.readLine();
                if (message.compareTo("1")==0) {
                    if (this.equals(lead)) {
                        System.out.println("I'm the leader.");
                    }
                    send.writeBytes("OK. Put.\n");
                    System.out.println("Message received from "+ipClient+" at port "+portClient+": "+message+"\n");
                } 
                else if (message.compareTo("2")==0) {
                    send.writeBytes("OK. Get.\n");
                    System.out.println("Message received from "+ipClient+" at port "+portClient+": "+message+"\n");
                }
                else {
                    send.writeBytes("Disconnecting...\n");
                }
            } while (message.compareTo("3")!=0);
            
            in.close();
            out.close();

            socket.close();
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
}