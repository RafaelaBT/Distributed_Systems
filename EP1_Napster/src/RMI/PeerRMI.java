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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PeerRMI that = (PeerRMI) obj;
        return ip.equals(that.ip) && port.equals(that.port) && path.equals(that.path);
    }

}
