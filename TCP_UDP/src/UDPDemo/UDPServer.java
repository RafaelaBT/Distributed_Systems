package UDPDemo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/** UDP server class -  stabilishes a simple connectionless communication between the clients and the server. */
public class UDPServer {
    public static void main(String[] args) throws Exception{
        try (
            // Creates a connectionless channel on port 9876.
            DatagramSocket serverSocket = new DatagramSocket(9876);) {
            while (true) {
                // Creates a buffer to receive the client information.
                byte[] receiveBuffer = new byte[1024];

                // Creates a datagram to fill the buffer.
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                System.out.println("Waiting the packet.");

                // Receive the datagram from remote host.
                serverSocket.receive(receivePacket); // BLOCKING METHOD

                /* BLOCKING METHOD: the server remains blocked until receive the client packet. */

                // Gets the client datagram information.
                InetAddress iPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                String info = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                
                // Modify and print the client message.
                String modifiedText = info.toUpperCase();
                System.out.println("Received from "+iPAddress.getHostAddress()+": "+info);

                // Allocates a buffer to return the client message.
                byte[] sendData = new byte[info.length()];
                sendData = modifiedText.getBytes();

                // Creates a datagram to send the packet to the client.
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, iPAddress, port);
                
                // Returns to the client.
                serverSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
