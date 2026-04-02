public class InvestigationReport {
    private int id;
    private int incidentId;
    private int analystId;
    private String conclusion;
    private int resolutionTimeMinutes;

    public InvestigationReport(int id, int incidentId, int analystId, String conclusion, int resolutionTimeMinutes) {
        this.id = id;
        this.incidentId = incidentId;
        this.analystId = analystId;
        this.conclusion = conclusion;
        this.resolutionTimeMinutes = resolutionTimeMinutes;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIncidentId() {
        return incidentId;
    }
    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }
    public int getAnalystId() {
        return analystId;
    }
    public void setAnalystId(int analystId) {
        this.analystId = analystId;
    }
    public String getConclusion() {
        return conclusion;
    }
    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
    public int getResolutionTimeMinutes() {
        return resolutionTimeMinutes;
    }
    public void setResolutionTimeMinutes(int resolutionTimeMinutes) {
        this.resolutionTimeMinutes = resolutionTimeMinutes;
    }

    @Override
    public String toString() {
        return "[REPORT] ID: " + id + "| INCIDENT: " + incidentId + "| ANALYST: " + analystId + "| CONCLUSION: " + conclusion + "| RESOLUTION MINUTES: " + resolutionTimeMinutes;
    }
}
