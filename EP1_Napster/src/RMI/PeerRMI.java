package RMI;

import java.io.Serializable;

public class PeerRMI  implements Serializable {
    // Atributes
    public String ip;
    public int port;
    public String path;

    // Constructor
    public PeerRMI(String ip, int port, String path) {
        this.ip = ip;
        this.port = port;
        this.path = path;
    }

}
