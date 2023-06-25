package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerRMIInterface extends Remote{
    public void peerInfo(String ip, String port, String path) throws RemoteException;
    public String join(String[] files) throws RemoteException;
    public ArrayList<PeerRMI> search(String fileName) throws RemoteException;
    public String update(String fileName) throws RemoteException;
}