package models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Incident implements Comparable<Incident>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String sourceIp;
    private final List<SecurityEvent> events;
    private final boolean isMalicious;

    private final Severity severity;
    private final LocalDateTime generationDate;

    public Incident(int id, String sourceIp, List<SecurityEvent> events, boolean isMalicious, Severity severity) {
        this.id = id;
        this.sourceIp = sourceIp;
        this.events = events;
        this.isMalicious = isMalicious;
        this.severity = severity;

        if (events != null && !events.isEmpty()) {
            this.generationDate = events.getFirst().getTimestamp();
        } else {
            this.generationDate = LocalDateTime.now();
        }
    }

    @Override
    public int compareTo(Incident otherIncident) {
        int thisImportance = getSeverityImportance(this.severity);
        int otherImportance = getSeverityImportance(otherIncident.getSeverity());

        if (thisImportance != otherImportance) {
            return Integer.compare(otherImportance, thisImportance);
        }
        return this.generationDate.compareTo(otherIncident.getGenerationDate());
    }

    private int getSeverityImportance(Severity severityLevel) {
        return switch (severityLevel) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    public int getId() { return id; }
    public String getSourceIp() { return sourceIp; }
    public List<SecurityEvent> getEvents() { return events; }
    public boolean isMalicious() { return isMalicious; }

    public Severity getSeverity() { return severity; }
    public LocalDateTime getGenerationDate() { return generationDate; }

    @Override
    public String toString() {
        return "[DOSSIER #" + id + "] Subject IP: " + sourceIp + " | Events contained: " + (events != null ? events.size() : 0) + " | Priority: " + severity;
    }
}