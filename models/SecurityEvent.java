package models;
import java.time.LocalDateTime;

public abstract class SecurityEvent {
    private int id;
    private final LocalDateTime timestamp;
    private final MonitoredSystem sourceSystem; // echipamentul care l a generat
    private final Severity severity; // LOW, MEDIUM, HIGH, CRITICAL

    public SecurityEvent(int id, LocalDateTime timestamp, MonitoredSystem sourceSystem, Severity severity) {
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

    @Override
    public String toString() {
        return "[" + getEventType() + "]" + " Time: " + timestamp.toString() + " | System: " + sourceSystem.getName() + " | Severity: " + severity;
    }
}