package Servidor;

//import java.io.BufferedReader;
//import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.ServerSocket;
//import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;

import RMI.Napster;
import RMI.NapsterInterface;

/** Servidor: class which receives the peer required connection and connects the peer. */
public class Servidor {
    public static void main(String[] args) throws IOException, AlreadyBoundException, ServerNotActiveException {
        // [ --- REGISTRY --- ]

        // Creates the registry on port 1099.
        LocateRegistry.createRegistry(1099);

        // Gets the registry.
        Registry reg = LocateRegistry.getRegistry();

        System.out.println("RUNNING SERVER...");


        // [ --- REMOTE OBJECT REGISTER --- ]

        // Creates a remote object;
        NapsterInterface file = new Napster();
        
        // Binds the remote object to a name to register it in the registry.
        reg.bind("rmi://127.0.0.1/file.jpg", file);

        /*
        try (
        // Creates a receptive socket at port 9000 to listen the peer.
        ServerSocket serverSocket = new ServerSocket(9000)
        ) {
            while (true) {
                System.out.println("WAITING FOR CONNECTION AT PORT 9000...");


                // [ --- TCP CONNECTION --- ]

                // Creates a new socket to connect the peer.
                Socket s = serverSocket.accept(); // BLOCKING METHOD

                System.out.println("\nCONNECTION ESTABILISHED FROM " + s.getInetAddress() + ".");


                // --> Start Thread here! <--


                // --> Thread Run method starts here. <--


                // [ --- TCP RECEIVE DATA --- ]

                // Creates the buffer to receive data input string from the peer.
                BufferedReader receive = new BufferedReader(new InputStreamReader(s.getInputStream()));

                // Receives the peer message.
                String message = receive.readLine(); // BLOCKING METHOD

                System.out.println("FROM PEER " + s.getInetAddress() + ": " + message);


                // BLOCKING METHOD: the server remains blocked until the user sends the message.


                // [ --- TCP SEND DATA --- ]

                // Creates the buffer to send the data output string to the peer.
                DataOutputStream send = new DataOutputStream(s.getOutputStream());

                // Returns a message to the peer.
                send.writeBytes("MESSAGE RECEIVED SUCCESSFULLY!");


                // [ --- TCP DISCONNECTION  --- ]

                System.out.println("DISCONNECTING...\n");
                
                // Close the channel.
                s.close();
            }
        }
        */
    }

}
