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

    private final String severity;
    private final LocalDateTime generationDate;

    public Incident(int id, String sourceIp, List<SecurityEvent> events, boolean isMalicious, String severity) {
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
        int thisWeight = getSeverityWeight(this.severity);
        int otherWeight = getSeverityWeight(otherIncident.getSeverity());

        if (thisWeight != otherWeight) {
            return Integer.compare(otherWeight, thisWeight);
        }
        return this.generationDate.compareTo(otherIncident.getGenerationDate());
    }

    private int getSeverityWeight(String severityLevel) {
        return switch (severityLevel.toUpperCase()) {
            case "CRITICAL" -> 4;
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }

    public int getId() { return id; }
    public String getSourceIp() { return sourceIp; }
    public List<SecurityEvent> getEvents() { return events; }
    public boolean isMalicious() { return isMalicious; }

    public String getSeverity() { return severity; }
    public LocalDateTime getGenerationDate() { return generationDate; }

    @Override
    public String toString() {
        return "[DOSSIER #" + id + "] Subject IP: " + sourceIp + " | Events contained: " + (events != null ? events.size() : 0) + " | Priority: " + severity;
    }
}