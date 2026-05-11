package repositories;

import models.Achievement;
import models.SOCAnalyst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileAnalystRepository implements AnalystRepository {
    private final String filePath = "resources/analysts.txt";

    @Override
    public List<SOCAnalyst> findAll() {
        List<SOCAnalyst> roster = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] p = line.split(",");
                // id, name, email, password, rank, daysSurvived, maxLives, currentLives, maxSeniorCalls, currentSeniorCalls, credits
                if (p.length == 11) {
                    roster.add(new SOCAnalyst(Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6]), Integer.parseInt(p[7]), Integer.parseInt(p[8]), Integer.parseInt(p[9]), Integer.parseInt(p[10])));
                }
            }
        } catch (IOException e) {
            System.out.println("[WARNING] Could not read analysts data.");
        }
        return roster;
    }

    @Override
    public Optional<SOCAnalyst> findByUsername(String username) {
        List<SOCAnalyst> allAnalysts = findAll();

        for (SOCAnalyst analyst : allAnalysts) {
            if (analyst.getName().equalsIgnoreCase(username)) {
                return Optional.of(analyst);
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(SOCAnalyst analyst) {
        List<SOCAnalyst> all = findAll();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == analyst.getId()) {
                all.set(i, analyst); //update
                found = true;
                break;
            }
        }

        if (!found) {
            all.add(analyst);
        }
        updateAll(all);
    }

    @Override
    public void updateAll(List<SOCAnalyst> analysts) {
        try {
            List<String> lines = new ArrayList<>();
            for (SOCAnalyst a : analysts) {
                lines.add(a.getId() + "," + a.getName() + "," + a.getEmail() + "," + a.getPassword() + "," +
                        a.getRankLevel() + "," + a.getDaysSurvived() + "," + a.getMaxLives() + "," +
                        a.getCurrentLives() + "," + a.getMaxSeniorCalls() + "," + a.getCurrentSpecialHelps());
            }
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            System.out.println("[ERROR] Error while saving analysts data.");
        }
    }

    @Override
    public Achievement saveAchievement(int userId, String achievementName) {
        int dummyId = Math.abs(achievementName.hashCode());
        String desc = "";
        int bonus = 0;

        switch (achievementName) {
            case "First Blood": desc = "Successfully block your first malicious traffic."; bonus = 50; break;
            case "The Flash": desc = "Block a CRITICAL threat in under 3 seconds."; bonus = 150; break;
            case "Iron Wall": desc = "Survive a full week (7 days) with absolute perfection."; bonus = 500; break;
            case "Paranoia": desc = "Block 3 legitimate employees in a single shift. Trust no one."; bonus = 50; break;
            case "Coffee Addict": desc = "Purchase your first Cyber-Coffee from the Dark Web Shop."; bonus = 0; break;
            case "Bribery": desc = "Buy an extra Senior Call from the Dark Web Shop."; bonus = 0; break;
            case "SLA Breacher": desc = "Fail to respond to a CRITICAL threat in time. Watch it burn."; bonus = 0; break;
            case "Rich Analyst": desc = "Accumulate a balance of $500 SOC Credits."; bonus = 200; break;
            case "Cyber Legend": desc = "Get promoted to Senior Lead Analyst (Rank 3)."; bonus = 1000; break;
            case "You Are Fired": desc = "Lose all your health and get terminated from the SOC."; bonus = 0; break;
            default: desc = "Secret achievement unlocked."; bonus = 0; break;
        }

        Achievement newAchievement = new Achievement(dummyId, achievementName, desc, bonus);

        List<SOCAnalyst> allUsers = findAll();
        for (SOCAnalyst analyst : allUsers) {
            if (analyst.getId() == userId) {
                boolean hasIt = analyst.getAchievements().stream().anyMatch(a -> a.getName().equals(achievementName));
                if (!hasIt) {
                    analyst.getAchievements().add(newAchievement);
                    update(analyst);
                    return newAchievement;
                }
                return null;
            }
        }
        return null;
    }
}