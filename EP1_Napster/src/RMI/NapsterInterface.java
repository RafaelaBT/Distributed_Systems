package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** NapsterInterface: interface which declares all the required methods to Napster. 
 * Extends Remote: the methods can be invoked from a non-local Java Virtual Machine (JVM).
*/
public interface NapsterInterface extends Remote {
    /**
     * Get the remote object NapsterFile by the fileName.
     * @param fileName - the file name.
     * @return a NapsterFile remote object.
     * @throws RemoteException
     */
    public NapsterFile getFile(String fileName) throws RemoteException;
}
