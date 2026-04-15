package models;

import java.io.Serial;
import java.io.Serializable;

public class InvestigationReport implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int incidentId;
    private final int analystId;
    private final String playerDecision;
    private final boolean wasMalicious;

    private boolean isCorrect;
    private String gradingOutcome;

    public InvestigationReport(int id, int incidentId, int analystId, String playerDecision, boolean wasMalicious) {
        this.id = id;
        this.incidentId = incidentId;
        this.analystId = analystId;
        this.playerDecision = playerDecision.toUpperCase();
        this.wasMalicious = wasMalicious;

        calculateGrading();
    }

    private void calculateGrading() {
        if (playerDecision.equals("BLOCKED") && wasMalicious) {
            this.isCorrect = true;
            this.gradingOutcome = "True Positive (Threat Neutralized)";
        } else if (playerDecision.equals("ALLOWED") && !wasMalicious) {
            this.isCorrect = true;
            this.gradingOutcome = "True Negative (Valid Traffic Allowed)";
        } else if (playerDecision.equals("BLOCKED")) {
            this.isCorrect = false;
            this.gradingOutcome = "False Positive (Employee Blocked)";
        } else if (playerDecision.equals("ALLOWED")) {
            this.isCorrect = false;
            this.gradingOutcome = "False Negative (Threat Bypassed)";
        } else {
            this.isCorrect = false;
            this.gradingOutcome = "Invalid Decision";
        }
    }

    public int getId() { return id; }
    public int getIncidentId() { return incidentId; }
    public boolean isCorrect() { return isCorrect; }
    public String getGradingOutcome() { return gradingOutcome; }

    @Override
    public String toString() {
        return "[REPORT ID: " + id + "] Incident: " + incidentId + " | Decision: " + playerDecision + " | Analyst: " + analystId +
                " | Outcome: " + gradingOutcome + " | Correct: " + (isCorrect ? "YES" : "NO");
    }
}