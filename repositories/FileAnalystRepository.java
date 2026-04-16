package repositories;

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
                // id, name, email, password, rank, daysSurvived, maxLives, currentLives, maxSeniorCalls, currentSeniorCalls
                if (p.length == 10) {
                    roster.add(new SOCAnalyst(Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6]), Integer.parseInt(p[7]), Integer.parseInt(p[8]), Integer.parseInt(p[9])));
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
                all.set(i, analyst);
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
}