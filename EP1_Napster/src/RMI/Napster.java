package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** Napter: class which implements NapsterInterface methods.
 * Extends UnicastRemoteObject: can export a remote object and obtain a stub that communicates to the object using the peer-to-peer communication (unicast).
 * Stub: client rotine wich appear as a server and look for the stub server when lauched.
*/
public class Napster extends UnicastRemoteObject implements NapsterInterface {

    public Napster() throws RemoteException {
        super();
    }

    @Override
    public NapsterFile getFile(String fileName) throws RemoteException {
        NapsterFile file = new NapsterFile(fileName);
        return file;
    }
    
}
