package repositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileTrafficDataRepository implements TrafficDataRepository {

    private List<String> readLines(String filePath, String fallback) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("[WARNING] Could not load " + filePath + ". Using fallback.");
            List<String> defaultList = new ArrayList<>();
            defaultList.add(fallback);
            return defaultList;
        }
    }

    @Override
    public List<String> getFirstNames() {
        return readLines("resources/first_names.txt", "employee");
    }

    @Override
    public List<String> getDepartments() {
        return readLines("resources/departments.txt", "it");
    }

    @Override
    public List<String> getAttackerIPs() {
        return readLines("resources/attacker_ips.txt", "199.99.99.99");
    }

    @Override
    public List<String> getTargetAccounts() {
        return readLines("resources/target_accounts.txt", "root");
    }

    @Override
    public List<String> getInternalIPs() {
        return readLines("resources/internal_ips.txt", "10.0.0.15");
    }
}