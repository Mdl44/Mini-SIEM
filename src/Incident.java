import java.time.LocalDateTime;

public class Incident {
    private int id;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String status; //OPEN, IN_PROGRESS, RESOLVED
    private LocalDateTime generationDate;
    private int assignedAnalyst; //Id ul analistului

    public Incident(int id, String severity, String status, LocalDateTime generationDate) {
        this.id = id;
        this.severity = severity;
        this.status = status;
        this.generationDate = generationDate;
        this.assignedAnalyst = 0;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSeverity() {
        return severity;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getGenerationDate() {
        return generationDate;
    }
    public void setGenerationDate(LocalDateTime generationDate) {
        this.generationDate = generationDate;
    }
    public int getAssignedAnalyst() {
        return assignedAnalyst;
    }
    public void setAssignedAnalyst(int assignedAnalyst) {
        this.assignedAnalyst = assignedAnalyst;
    }

    @Override
    public String toString() {
        return "[INCIDENT] ID: " + id + "| SEVERITY: " + severity + "| STATUS: " + status + " Analyst ID: " + assignedAnalyst;
    }
}
