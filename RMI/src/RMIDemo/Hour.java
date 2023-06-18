package RMIDemo;

import java.io.Serializable;

/** Hour class: contains the client name and the server timestamp.
 * Implements Serializable, i. e., intances of this class are objects that can be transferred using a byte string (marshalling/unmarshalling).
*/
public class Hour implements Serializable{
    // Hour atributes.
    public String clientName;
    public long timestamp;

    // Hour constructor.
    public Hour(String clientName, long timestamp) {
        this.clientName = clientName;
        this.timestamp = timestamp;
    }
}
