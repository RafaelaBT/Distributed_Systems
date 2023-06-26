package RMI;

import java.io.Serializable;

/**
 * Peer class. Instaces of this class are serializables objects, i. e., it can be transferred using a byte string (process called marshalling/unmarshalling).
 */
public class PeerRMI  implements Serializable {
    // Peer attributes
    public String ip;
    public String port;
    public String path;

    // PeerRMI constructor - sets initial values for PeerRMI object attributes.
    public PeerRMI(String ip, String port, String path) {
        this.ip = ip;
        this.port = port;
        this.path = path;
    }

    // Overrides the equals method to compare two PeerRMI objects.
    // Reference: https://www.baeldung.com/java-comparing-objects
    @Override
    public boolean equals(Object obj) {
        // Returns true if the entities are equals.
        if (obj == this) {
            return true;
        }
        // Returns false if the entities are not equals or the objects are from a different class.
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Compare each attibute value from the objects and returns the result.
        PeerRMI that = (PeerRMI) obj;
        return ip.equals(that.ip) && port.equals(that.port) && path.equals(that.path);
    }

}
