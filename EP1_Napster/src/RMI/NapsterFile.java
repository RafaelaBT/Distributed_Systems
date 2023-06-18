package RMI;

import java.io.Serializable;

/** NapsterFile: serializable object class, i. e., which can be transferred using a byte string (marshalling/unmarshalling). */
public class NapsterFile implements Serializable {
    // NapsterFile atributes.
    public String fileName;

    // NapsterFile constructor.
    public NapsterFile(String fileName) {
        this.fileName = fileName;
    }
    
}
