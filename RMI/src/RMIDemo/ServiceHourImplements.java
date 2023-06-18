package RMIDemo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** ServiceHourImplements class: implement the ServiceHour methods.
 * Extends UnicastRemoteObject, i.e., this class export a remote object and obtain a stub that communicates to the remote object using the peer-to-peer communication (unicast).
 * Stub: client rotines wich appear as servers and look for the stub server when lauched.
 */
public class ServiceHourImplements extends UnicastRemoteObject implements ServiceHour{

    public ServiceHourImplements() throws RemoteException {
        super();
    }

    @Override
    public Hour getHour(String clientName) throws RemoteException {
        // Intance the serializable object serverHour.
        Hour serverHour = new Hour(clientName, System.currentTimeMillis());
        return serverHour;
    }

}
