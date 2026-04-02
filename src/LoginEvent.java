import java.time.LocalDateTime;

public class LoginEvent extends SecurityEvent {
    private String username;
    private String status;
    private String sourceIP;

    public LoginEvent(int id, LocalDateTime timestamp, String sourceSystem, int severity, String username, String status, String sourceIP) {
        super(id, timestamp, sourceSystem, severity);
        this.username = username;
        this.status = status;
        this.sourceIP = sourceIP;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSourceIP() {
        return sourceIP;
    }
    public void setSourceIP(String sourceIP) {
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
