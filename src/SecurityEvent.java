import java.time.LocalDateTime;
public abstract class SecurityEvent {
    private int id;
    private LocalDateTime timestamp;
    private String sourceSystem; //cine a trimis log-ul
    private int severity; // 1 - 10

    public SecurityEvent(int id, LocalDateTime timestamp, String sourceSystem, int severity) {
        this.id = id;
        this.timestamp =  timestamp;
        this.sourceSystem = sourceSystem;
        this.severity = severity;
    }

    public abstract String getEventType();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getSourceSystem() {
        return sourceSystem;
    }
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    public int getSeverity() {
        return severity;
    }
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "[" + getEventType() + "]" + " Time: " + timestamp.toString() + " Source: " + sourceSystem + " Severity: " + severity;
    }
}



