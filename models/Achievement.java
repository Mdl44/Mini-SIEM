package models;

import java.io.Serializable;

public class Achievement implements Serializable {
    private final int id;
    private final String name;
    private final String description;
    private final int bonusCredits;

    public Achievement(int id, String name, String description, int bonusCredits) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.bonusCredits = bonusCredits;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBonusCredits() { return bonusCredits; }

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}