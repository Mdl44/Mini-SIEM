package repositories;

import models.MonitoredSystem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileSystemRepository implements SystemRepository {
    private final String filePath = "resources/monitored_systems.txt";

    @Override
    public List<MonitoredSystem> findAll() {
        List<MonitoredSystem> systems = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    systems.add(new MonitoredSystem(Integer.parseInt(parts[0].trim()), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("[WARNING] Couldn't load monitored systems.");
        }
        return systems;
    }

    @Override
    public void add(MonitoredSystem system) {
        try {
            String line = String.format("%d,%s,%s,%s,%s\n", system.getId(), system.getName(), system.getIpAddress(), system.getOsType(), system.getImportance());
            Files.write(Paths.get(filePath), line.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("[ERROR] Couldn't save monitored systems.");
        }
    }
}