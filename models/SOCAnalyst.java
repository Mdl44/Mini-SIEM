package models;

import java.io.Serial;
import java.io.Serializable;

public class SOCAnalyst extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String password;
    private int rank; // 1 = junior, 2 = mid, 3 = senior
    private int daysSurvived;
    private int maxLives;
    private int currentLives;
    private int maxSeniorCalls;
    private int currentSeniorCalls;

    public SOCAnalyst(int id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
        this.rank = 1;
        this.daysSurvived = 0;
        this.maxLives = 3;
        this.currentLives = 3;
        this.maxSeniorCalls = 1;
        this.currentSeniorCalls = 1;
    }

    public SOCAnalyst(int id, String name, String email, String password, int rank, int daysSurvived, int maxLives, int currentLives, int maxSeniorCalls, int currentSeniorCalls) {
        super(id, name, email);
        this.password = password;
        this.rank = rank;
        this.daysSurvived = daysSurvived;
        this.maxLives = maxLives;
        this.currentLives = currentLives;
        this.maxSeniorCalls = maxSeniorCalls;
        this.currentSeniorCalls = currentSeniorCalls;
    }

    public void recordSuccessfulDay() {
        this.daysSurvived++;

        if (this.daysSurvived % 2 == 0) { // la 2 zile = rank up
            this.rank++;
            this.maxLives++;
            this.maxSeniorCalls++;
            System.out.println("\nLEVEL UP! " + getName() + " reached rank " + rank + " (" + getRankName() + ")!");
        }

        this.currentSeniorCalls = this.maxSeniorCalls;
    }

    public void recordFlawlessDay() {
        recordSuccessfulDay();

        if (this.currentLives < this.maxLives) {
            this.currentLives++;
            System.out.println("[BONUS] Flawless day. A warning was removed. (HP: " + this.currentLives + "/" + this.maxLives + ")");
        }
    }

    public void loseLife() {
        if (this.currentLives > 0) {
            this.currentLives--;
        }
    }

    @Override
    public void printDashboard() {
        System.out.println("\n--- ANALYST PROFILE: " + getName().toUpperCase() + " ---");
        System.out.println("Rank: " + getRankName() + " (Lvl " + rank + ")");
        System.out.println("Health: " + currentLives + "/" + maxLives);
        System.out.println("Senior Help: " + currentSeniorCalls + "/" + maxSeniorCalls);
        System.out.println("Experience: " + daysSurvived + " days survived");
        System.out.println("---------------------------------------");
    }

    private String getRankName() {
        return switch (rank) {
            case 1 -> "Junior Analyst";
            case 2 -> "Mid-Level Analyst";
            case 3 -> "Senior Lead Analyst";
            default -> "Cyber Legend";
        };
    }

    public String getPassword() { return password; }
    public int getRankLevel() { return rank; }
    public int getDaysSurvived() { return daysSurvived; }
    public int getMaxLives() { return maxLives; }
    public int getCurrentLives() { return currentLives; }
    public int getMaxSeniorCalls() { return maxSeniorCalls; }
    public int getCurrentSpecialHelps() { return currentSeniorCalls; }

    public void useSpecialHelp() {
        if (currentSeniorCalls > 0) currentSeniorCalls--;
    }

    @Override
    public String toString() {
        return super.toString() + " [Rank: " + getRankName() + ", HP: " + currentLives + "]";
    }
}