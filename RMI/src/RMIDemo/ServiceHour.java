package RMIDemo;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** ServicoHora interface: declares the required ServiceHour methods.
 * Extends Remote, i. e., the methods can be invoked from a non-local virtual machine (JVM).
*/
public interface ServiceHour extends Remote{
    /**
     * Get the remote object Hour by the client name.
     * @param clientName - the client name.
     * @return the remote object Hour.
     * @throws RemoteException
     */
    public Hour getHour(String clientName) throws RemoteException;
}
