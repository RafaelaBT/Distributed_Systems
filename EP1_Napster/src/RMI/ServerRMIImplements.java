package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class ServerRMIImplements extends UnicastRemoteObject implements ServerRMIInterface{

    public ServerRMIImplements() throws RemoteException {
        super();
    }

    @Override
    public String join(String ip, int port, String path, String[] files) throws RemoteException {
        ConcurrentHashMap<String, PeerRMI[]> m = new ConcurrentHashMap<String, PeerRMI[]>();

        PeerRMI peerList[] = {new PeerRMI(ip, port, path)};

        if (files.length > 0) {
            for (String fileName : files) {
                m.putIfAbsent(fileName, peerList);
                /* Para ter acesso aos itens
                for (PeerRMI p : m.get(fileName)) {
                    System.out.println(p.ip);
                }*/
            }
        }
        else {
            m.putIfAbsent("", peerList);
        }
        
        return "JOIN_OK";
    }
}
