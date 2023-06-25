package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerRMIImplements extends UnicastRemoteObject implements ServerRMIInterface{
    private final ConcurrentMap<String, ArrayList<PeerRMI>> peersFiles = new ConcurrentHashMap<>();
    private PeerRMI peer;

    public ServerRMIImplements() throws RemoteException {
        super();
    }

    @Override
    public String join(String ip, String port, String path, String[] files) throws RemoteException {
        peer = new PeerRMI(ip, port, path);
        System.out.println("\n-> JOINED <-");
        System.out.println("Peer "+peer.ip+":"+peer.port+" adicionado com os arquivos:");

        if (files.length > 0) {
            ArrayList<PeerRMI> peerList = new ArrayList<PeerRMI>();
            peerList.add(peer);

            for (String fileName : files) {
                if (peersFiles.putIfAbsent(fileName, peerList) != null) {
                    update(fileName);
                }
                System.out.println("- "+fileName+" ");
            }
        }

        return "JOIN_OK";
    }

    @Override
    public ArrayList<PeerRMI> search(String fileName) throws Exception {
        if (peer == null) {
            throw new Exception("EXTERN PEER DETECTED!");
        }
        System.out.println("\n-> SEARCHED <-");
        System.out.println("Peer "+peer.ip+":"+peer.port+" solicitou arquivo "+fileName+".");

        return peersFiles.computeIfPresent(fileName, (k, v) -> peersFiles.get(k));
    }
    
    @Override
    public String update(String fileName) throws RemoteException {
        peersFiles.computeIfPresent(fileName, (k, v) -> {
            for (PeerRMI i : v) {
                if (i.equals(peer)) {
                    return v;
                }
            }
            v.add(peer);
            return v;
        });

        return "UPDATE_OK";
    }

}
