package RMI;

import java.io.Serializable;

public class PeerRMI  implements Serializable {
    // Atributes
    private String ip;
    private int port;
    private String path;
    private String[] files;

    // Constructor
    public PeerRMI(String ip, int port, String path, String[] files) {
        this.ip = ip;
        this.port = port;
        this.path = path;
        this.files = files;
    }

    // Getters
    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String[] getFiles() {
        return files;
    }

    public String getPath() {
        return path;
    }

}
