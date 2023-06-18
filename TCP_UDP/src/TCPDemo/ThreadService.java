package TCPDemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/** Thread to service the clients. */
public class ThreadService extends Thread{
    // ThreadService atributes.
    private Socket node;

    /**
     * ThreadService constructor.
     * @param node - the client.
     */
    public ThreadService(Socket node){
        this.node = node;
    }

    /**
     * Thread execution method.
     * 
    */
    @Override
    public void run() {  
        try {
            // Creates a socket information input string (to receive client information).
            InputStreamReader in = new InputStreamReader(node.getInputStream());
            BufferedReader receive = new BufferedReader(in);

            // Creates a socket information output string (to send the server information to the client).
            OutputStream out = node.getOutputStream();
            DataOutputStream send = new DataOutputStream(out);

            // Receives the client string.
            String text = receive.readLine(); // BLOCKING METHOD

            /* BLOCKING METHOD: the server remains blocked until the client or the server sends the string. */

            System.out.println("Message: "+text+" from "+node.getInetAddress());
                
            // Returns the transformed string to the client.
            send.writeBytes(text.toUpperCase() + "\n");

            // Close the channel (socket).
            node.close();
        } catch (IOException e) {
            System.out.println("Another thread is not supported.\n Exception "+e.getStackTrace());
        }
    }
}
