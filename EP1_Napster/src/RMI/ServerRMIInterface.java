package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Declares all the required methods to be implemented by a RMI server.
 * Extends the Remote interface - all the methods can be invoked from a non-local virtual machine (JVM).
 */
public interface ServerRMIInterface extends Remote{
    /**
     * Connects the peer to the server passing the peer informations as parameters. Returns the string "JOIN_OK" if the peer joined successfully, or throws a RemoteException on failure.
     * @param files - peer files (String[])
     * @param peer - peer informations (PeerRMI)
     * @return "JOIN_OK" (String)
     * @throws RemoteException
     */
    public String join(String[] files, PeerRMI peer) throws RemoteException;

    /**
     * Searches for peers who own the file passed as a parameter. Returns a empty list or a list with the peers who own the file. Throws a RemoteException on failure.
     * @param fileName - file name (String)
     * @param peer - peer informations (PeerRMI)
     * @return - peer list (ArrayList)
     * @throws Exception
     */
    public ArrayList<PeerRMI> search(String fileName, PeerRMI peer) throws RemoteException;

    /**
     * Updates the peer list with the peer who downloaded the file passed as a parameter. Returns the string "UPDATE_OK" if it successfully updated the list or throws a RemoteException on failure.
     * @param fileName - file name (String)
     * @param peer - peer informations (PeerRMI)
     * @return "UPDATE_OK" (String)
     * @throws RemoteException
     */
    public String update(String fileName, PeerRMI peer) throws RemoteException;
}