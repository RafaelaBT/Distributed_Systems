package TCPDemo;

import java.net.ServerSocket;
import java.net.Socket;

/** TCP server class -  stabilishes connection between mutiples clients and the server (concurrent). */
public class TCPServer {
    public static void main(String[] args) throws Exception{
        try (// Creates a receptive sockect to listen and connect clients to port 9000.
            ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true) {
                System.out.println("Waiting for connection.");

                /* Creates a new socket to wait the client connect.
                 * The socket will have a port designated by the OS - getting a number between 1024 and 65535.
                 */
                Socket node = serverSocket.accept(); // BLOCKING METHOD
                
                System.out.println("Connection accepted.");

                /* BLOCKING METHOD: the server remains blocked until a client connects. */
                
                // Thread to service the client.
                ThreadService thread = new ThreadService(node);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
