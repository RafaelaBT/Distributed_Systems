package UDPDemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** UDP client class - connects the client to the server using a simple connectionless communication (unreliable protocol). */
public class UDPClient {
    public static void main(String[] args) throws Exception {
        // Get the IP adress from remote host (server).
        InetAddress IPAddress = InetAddress.getByName("localhost");

        /* Connectionless comunication channel.
         * The socket s port will be designated by the OS - getting a number between 1024 and 65535. */
        DatagramSocket clientSocket = new DatagramSocket();

        // Creates a buffer to send the client information.
        BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
        String text = inUser.readLine(); // BLOCKING METHOD

        // Allocates buffers.
        byte[] sendData = new byte[text.length()];
        sendData = text.getBytes();

        // Creates a datagram with the port and the address of the remote host (server).
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

        // Sends the datagram to remote host.
        clientSocket.send(sendPacket);

        // Allocates a buffer to receive the server information.
        byte[] receiveData = new byte[1024];

        // Creates a datagram to fill the buffer.
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        // Receive the datagram from remote host.
        clientSocket.receive(receivePacket); // BLOCKING METHOD

        /* BLOCKING METHOD: the client remains blocked until the user or the server sends the string. */

        // Get the datagram information.
        String info = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        //Offset: packet fragment offset.

        System.out.println("Received from "+IPAddress+": "+info);

        // Close the channel.
        clientSocket.close();
    }
}
