package models;

import java.util.ArrayList;
import java.util.List;

public class SOCAnalyst extends User {
    private final String password;
    private int rank;
    private int daysSurvived;
    private int maxLives;
    private int currentLives;
    private int maxSeniorCalls;
    private int currentSeniorCalls;
    private int credits;
    private List<Achievement> achievements = new ArrayList<>();

    public SOCAnalyst(int id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
        this.rank = 1;
        this.daysSurvived = 0;
        this.maxLives = 3;
        this.currentLives = 3;
        this.maxSeniorCalls = 1;
        this.currentSeniorCalls = 1;
        this.credits = 0;
    }

    public SOCAnalyst(int id, String name, String email, String password, int rank, int daysSurvived, int maxLives, int currentLives, int maxSeniorCalls, int currentSeniorCalls, int credits) {
        super(id, name, email);
        this.password = password;
        this.rank = rank;
        this.daysSurvived = daysSurvived;
        this.maxLives = maxLives;
        this.currentLives = currentLives;
        this.maxSeniorCalls = maxSeniorCalls;
        this.currentSeniorCalls = currentSeniorCalls;
        this.credits = credits;
    }

    public int getCredits() { return credits; }
    public void addCredits(int amount) { this.credits += amount; }
    public boolean spendCredits(int amount) {
        if (this.credits >= amount) {
            this.credits -= amount;
            return true;
        }
        return false;
    }

    public void restoreHealth() {
        if (currentLives < maxLives) currentLives++;
    }
    public void addExtraSeniorCall() {
        currentSeniorCalls++;
    }

    public void recordSuccessfulDay() {
        this.daysSurvived++;
        if (this.daysSurvived % 2 == 0) {
            this.rank++;
            this.maxLives++;
            this.maxSeniorCalls++;
            System.out.println("\n[PROMOTION] " + getName() + " reached rank " + rank + " (" + getRankName() + ")!");
        }
        this.currentSeniorCalls = this.maxSeniorCalls;
    }

    public void recordFlawlessDay() {
        recordSuccessfulDay();
    }

    public void loseLife() {
        if (this.currentLives > 0) {
            this.currentLives--;
        }
    }

    @Override
    public void printDashboard() {
        System.out.println("\n--- ANALYST PROFILE: " + getName().toUpperCase() + " ---");
        System.out.println("Rank: Lvl " + getRankLevel() + " | Health: " + getCurrentLives() + "/" + getMaxLives());
        System.out.println("Senior Help: " + getCurrentSpecialHelps() + " | Balance: $" + getCredits());

        System.out.print("Achievements: ");
        if (achievements.isEmpty()) {
            System.out.println("[None]");
        } else {
            String names = achievements.stream().map(Achievement::getName).collect(java.util.stream.Collectors.joining(", "));
            System.out.println("[" + names + "]");
        }
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

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    public void useSpecialHelp() {
        if (currentSeniorCalls > 0) currentSeniorCalls--;
    }

    @Override
    public String toString() {
        return super.toString() + " [Rank: " + getRankName() + ", HP: " + currentLives + ", $: " + credits + "]";
    }
}