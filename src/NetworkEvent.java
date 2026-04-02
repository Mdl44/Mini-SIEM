import java.time.LocalDateTime;

public class NetworkEvent extends SecurityEvent {
    private String destinationIP;
    private int port;
    private String protocol; // TCP / UDP

    public NetworkEvent(int id, LocalDateTime timestamp, String sourceSystem, int severity, String destinationIP, int port, String protocol) {
        super(id, timestamp, sourceSystem, severity);
        this.destinationIP = destinationIP;
        this.port = port;
        this.protocol = protocol;
    }

    public String getDestinationIP() {
        return destinationIP;
    }
    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getEventType() {
        return "NETWORK";
    }

    @Override
    public String toString() {
        return super.toString() + " | Destination IP: " + destinationIP + " | Port: " + port + " | Protocol: " + protocol;
    }
}
