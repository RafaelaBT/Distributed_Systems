package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerRMIInterface extends Remote{
    public String join(String ip, int port, String path, String[] files) throws RemoteException;
}
