package models;
import java.time.LocalDateTime;

public class NetworkEvent extends SecurityEvent {

    private final String attackerIP;
    private final int port;
    private final String protocol;

    public NetworkEvent(int id, LocalDateTime timestamp, MonitoredSystem sourceSystem, Severity severity, String attackerIP, int port, String protocol) {
        super(id, timestamp, sourceSystem, severity);
        this.attackerIP = attackerIP;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public String getEventType() {
        return "NETWORK";
    }

    @Override
    public String toString() {
        return super.toString() + " | Source IP: " + attackerIP + " | Target Port: " + port + " | Protocol: " + protocol;
    }
}