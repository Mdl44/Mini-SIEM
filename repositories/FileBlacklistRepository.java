package repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileBlacklistRepository implements BlacklistRepository {
    private final String filePath = "resources/blacklisted_ips.txt";
    private final Set<String> blacklistedIPs = new HashSet<>();

    public FileBlacklistRepository() {
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            blacklistedIPs.addAll(lines);
        } catch (IOException e) {
            System.out.println("[WARNING] No file found.");
        }
    }

    @Override
    public List<String> getAll() {
        return new ArrayList<>(blacklistedIPs);
    }

    @Override
    public boolean add(String ip) {
        if (blacklistedIPs.add(ip)) {
            try {
                Files.write(Paths.get(filePath), ("\n" + ip).getBytes(), StandardOpenOption.APPEND);
                return true;
            } catch (IOException e) {
                System.out.println("[ERROR] Couldn't add IP: .");
            }
        }
        return false;
    }
}