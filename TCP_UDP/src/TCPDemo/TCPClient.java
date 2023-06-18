package TCPDemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/** TCP client class - connection-oriented, i. e., connects the client to the server before sending the data (reliable protocol). */
public class TCPClient {
    public static void main(String[] args) throws Exception{
        /* Try to create a connection to the remote host (server) on port 9000.
         * The socket s port will be designated by the OS - getting a number between 1024 and 65535. */
        Socket s = new Socket("localhost", 9000);

        // Creates the socket information output string (to send socket s information to remote host).
        OutputStream out = s.getOutputStream();
        DataOutputStream send = new DataOutputStream(out);

        // Creates the socket information input string (to socket s receive information from remote host).
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader receive = new BufferedReader(in);

        // Creates a buffer to catch the user's keyboard input string.
        BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
        
        // Receives the user's keyboard input string.
        String text = inUser.readLine(); // BLOCKING METHOD

        // Sends the user string to the remote host.
        send.writeBytes(text + "\n");

        // Receives the remote host response.
        String response = receive.readLine(); //BLOCKING METHOD
        System.out.println("From server ("+s.getInetAddress()+"): " + response);

        /* BLOCKING METHOD: the client remains blocked until the user or the server sends the string. */

        // Close the channel (socket).
        s.close();
    }
}
