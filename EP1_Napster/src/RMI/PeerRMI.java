package RMI;

import java.io.Serializable;

public class PeerRMI  implements Serializable {
    // Atributes
    public String ip;
    public String port;
    public String path;

    // Constructor
    public PeerRMI(String ip, String port, String path) {
        this.ip = ip;
        this.port = port;
        this.path = path;
    }

}
