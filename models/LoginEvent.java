package models;

import java.io.Serial;
import java.time.LocalDateTime;

public class LoginEvent extends SecurityEvent {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String status;
    private final String sourceIP;

    public LoginEvent(int id, LocalDateTime timestamp, MonitoredSystem sourceSystem, String severity, String username, String status, String sourceIP) {
        super(id, timestamp, sourceSystem, severity);
        this.username = username;
        this.status = status;
        this.sourceIP = sourceIP;
    }

    @Override
    public String getEventType() {
        return "LOGIN";
    }

    @Override
    public String toString() {
        return super.toString() + " | User: " + username + " | Status: " + status + " | IP: " + sourceIP;
    }
}
