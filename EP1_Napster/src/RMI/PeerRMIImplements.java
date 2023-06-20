package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PeerRMIImplements extends UnicastRemoteObject implements PeerRMIInterface{

    public PeerRMIImplements() throws RemoteException {
        super();
    }

    @Override
    public String join(String ip, int port, String path, String[] files) throws RemoteException {
        PeerRMI peer = new PeerRMI(ip, port, path, files);
        return "JOIN_OK";
    }
}
