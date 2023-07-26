import java.sql.Timestamp;

public class Mensagem {
    private final String type;

    private String ip;
    private Integer port;

    private String key;
    private String value;
    private Timestamp timestamp;

    public Mensagem (String type) {
        this.type = type;
    }

    public Mensagem (String type, String ip, Integer port) {
        this.type = type;
        this.ip = ip;
        this.port = port;
    }

    public Mensagem (String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Mensagem (String type, String key, Timestamp timestamp) {
        this.type = type;
        this.key = key;
        this.timestamp = timestamp;
    }

    public Mensagem (String type, String key, String value, Timestamp timestamp) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    
}
