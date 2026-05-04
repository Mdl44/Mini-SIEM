package services;

import models.Incident;
import models.MonitoredSystem;
import repositories.BlacklistRepository;
import repositories.SystemRepository;

import java.util.*;

public class SIEMService {
    private final SystemRepository systemRepository;
    private final BlacklistRepository blacklistRepository;
    private final TreeSet<Incident> activeIncidents = new TreeSet<>(); //sorteaza incidentele dupa severitate, pentru a trata mai intai cele mai severe cazuri

    public SIEMService(SystemRepository systemRepository, BlacklistRepository blacklistRepository) {
        this.systemRepository = systemRepository;
        this.blacklistRepository = blacklistRepository;
    }

    public void loadDailyQueue(List<Incident> generatedSessions) {
        activeIncidents.clear();
        activeIncidents.addAll(generatedSessions);
        AuditService.getInstance().logAction("SHIFT_STARTED_WITH_" + generatedSessions.size() + "_CASES");
        System.out.println("[SYSTEM] Desk populated...");
    }

    public Incident claimNextIncident() {
        if (activeIncidents.isEmpty()) {
            return null;
        }
        AuditService.getInstance().logAction("CLAIM_INCIDENT");
        return activeIncidents.pollFirst();
    }

    public int getRemainingIncidentsCount() {
        return activeIncidents.size();
    }

    public void printBlacklist() {
        System.out.println("\n--- OFFICIAL BLACKLIST ---");
        for (String ip : blacklistRepository.getAll()) {
            System.out.println("- " + ip);
        }
        System.out.println("--------------------------\n");
    }

    public void printMonitoredSystems() {
        System.out.println("\n--- MONITORED SYSTEMS INVENTORY ---");
        for (MonitoredSystem sys : systemRepository.findAll()) {
            System.out.println("IP: " + sys.getIpAddress() + " | Name: " + sys.getName() + " | Priority: " + sys.getImportance());
        }
        System.out.println("-----------------------------------\n");
    }

    public void addBlacklistIp(String ip) {
        boolean added = blacklistRepository.add(ip);
        if (added) {
            System.out.println("[SUCCESS] IP " + ip + " permanently added to the official blacklist.");
            AuditService.getInstance().logAction("BLACKLIST_ADD");
        } else {
            System.out.println("[INFO] IP is already on the blacklist.");
        }
    }


    public void addMonitoredSystem(MonitoredSystem system) {
        systemRepository.add(system);
        AuditService.getInstance().logAction("SYSTEM_ADD");
    }

    public List<MonitoredSystem> getMonitoredSystems() {
        return systemRepository.findAll();
    }
}