package Peer;

//import java.io.BufferedReader;
//import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.NapsterFile;
import RMI.NapsterInterface;

/** Peer:  class which required the connection from the peer to the server. */
public class Peer {
    public static void main(String[] args) throws UnknownHostException, IOException, NotBoundException {
        /* --- REMOTE OBJECT --- */

        // Gets the registry.
        Registry reg = LocateRegistry.getRegistry();

        // Looks up the remote object name in the registry and casts (converts) the reference.
        NapsterInterface reference = (NapsterInterface) reg.lookup("rmi://127.0.0.1/file.jpg");

        // Gets the serializable object from the remote object.
        NapsterFile file = reference.getFile("file.jpeg");

        System.out.println("File name: " + file.fileName + "\n");

        /*
        // [ --- TCP CONNECTION --- ]

        // Requires a connection to the server at port 9000.
        Socket s = new Socket("localhost", 9000);
        

        // [ --- TCP SEND DATA --- ]

        // Creates the buffer to send the data output string to the server.
        DataOutputStream send = new DataOutputStream(s.getOutputStream());

        // Creates a buffer to capture the user input string.
        BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("WELCOME TO NASPTER!");

        System.out.print("Write your message: ");

        // Receives the user input string.
        String message = inUser.readLine(); // BLOCKING METHOD

        // Sends the user message to the server.
        send.writeBytes(message + "\n");

        System.out.println("\nMESSAGE SENT SUCCESSFULLY!");

        System.out.println("\nWAITING FOR THE SERVER RESPONSE...");


        // [ --- TCP RECEIVE DATA --- ]

        // Creates the buffer to receive the data input string from the server.
        BufferedReader receive = new BufferedReader(new InputStreamReader(s.getInputStream()));

        // Receives the server response.
        String response = receive.readLine(); // BOCKING METHOD

        System.out.println("\n" + response);
        
        // BLOCKING METHOD: the peer remains blocked until the user or the server sends the message.
        

        // [ --- TCP DISCONNECTION --- ]

        System.out.println("\nDISCONNECTING...");

        // Close the channel.
        s.close();
        */
    }
}
