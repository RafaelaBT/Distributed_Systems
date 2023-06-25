package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerRMIInterface extends Remote{
    public String join(String ip, String port, String path, String[] files) throws RemoteException;
    public ArrayList<PeerRMI> search(String fileName) throws Exception;
    public String update(String fileName) throws RemoteException;
}