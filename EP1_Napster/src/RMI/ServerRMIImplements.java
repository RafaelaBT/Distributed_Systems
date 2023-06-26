package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implements the RMI server methods.
 * Extends UnicastRemoteObject which export a remote object and obtain a stub that communicates to the remote object using the peer-to-peer communication.
 */
public class ServerRMIImplements extends UnicastRemoteObject implements ServerRMIInterface{
    // Creates a concurrentHashMap (data structure) to keep the filenames (keys) and the peers list (values) who owns them.
    private final ConcurrentMap<String, ArrayList<PeerRMI>> peersFiles = new ConcurrentHashMap<>();

    /**
     * RMI server constructor. Inherit the UnicastRemoteObject constructor which creates and export a new UnicasRemoteObject.
     * @throws RemoteException
     */
    public ServerRMIImplements() throws RemoteException {
        super();
    }

    @Override
    public String join(String[] files, PeerRMI peer) throws RemoteException {
        // Note: All souts of this method are to print the joined peer files on the server console.
        System.out.println("\n-> JOINED <-");
        System.out.println("Peer "+peer.ip+":"+peer.port+" adicionado com os arquivos:");

        /* Execute if there is at least one file in the file list.
        * Note: does not insert the peer in the map if the peer doesn't have a file (not necessary). */
        if (files.length > 0) {
            // Creates a empty peer list and inserts the peer who wants join the server.
            ArrayList<PeerRMI> peerList = new ArrayList<PeerRMI>();
            peerList.add(peer);

            // Loops through each filename in the file list.
            for (String fileName : files) {
                // If the file is not in the map, inserts the filename and the peer list, otherwise calls the update method.
                if (peersFiles.putIfAbsent(fileName, peerList) != null) {
                    update(fileName, peer);
                }
                System.out.println("- "+fileName+" ");
            }
        }

        return "JOIN_OK";
    }

    @Override
    public ArrayList<PeerRMI> search(String fileName, PeerRMI peer) throws RemoteException {
        // Note: All souts of this method are to print the filename requested by the peer on the server console.
        System.out.println("\n-> SEARCHED <-");
        System.out.println("Peer "+peer.ip+":"+peer.port+" solicitou arquivo "+fileName+".");

        // If the file is in the map, it returns the peer list, otherwise it returns null.
        return peersFiles.computeIfPresent(fileName, (k, v) -> peersFiles.get(k));
    }
    
    @Override
    public String update(String fileName, PeerRMI peer) throws RemoteException {
        peersFiles.computeIfPresent(fileName, (k, v) -> {
            /* If the file is in the map, loops through each peer in the peer list.
            * Insert the peer if it is not already in the list. */
            for (PeerRMI i : v) {
                /* Compares the values (and not the indentities) from each peer in the list with the peer received.
                * If the objects are equals, returns the peer list (do not insert). */
                if (i.equals(peer)) {
                    return v;
                }
            }
            v.add(peer);
            return v;
        });

        return "UPDATE_OK";
    }

    // Note: use the computeIfPresent method to avoid the consistecy problem in the concurrentHashMap, like reading a old value with the get method.

}
