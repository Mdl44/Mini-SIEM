package models;

import java.io.Serial;
import java.time.LocalDateTime;

public class NetworkEvent extends SecurityEvent {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String attackerIP;
    private final int port;
    private final String protocol;

    public NetworkEvent(int id, LocalDateTime timestamp, MonitoredSystem sourceSystem, String severity, String attackerIP, int port, String protocol) {
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