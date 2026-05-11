package services;

import models.*;
import repositories.TrafficDataRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficSimulator {
    private final Random random = new Random();
    private int eventIdCounter = 1;
    private int incidentIdCounter = 1;

    private final List<String> firstNames;
    private final List<String> departments;
    private final List<String> attackerIPs;
    private final List<String> targetAccounts;
    private final List<String> internalIPs;

    public TrafficSimulator(TrafficDataRepository dataRepository) {
        this.firstNames = dataRepository.getFirstNames();
        this.departments = dataRepository.getDepartments();
        this.attackerIPs = dataRepository.getAttackerIPs();
        this.targetAccounts = dataRepository.getTargetAccounts();
        this.internalIPs = dataRepository.getInternalIPs();
    }

    public List<Incident> generateDailyQueue(List<MonitoredSystem> systems, int totalSessions, int playerLevel) {
        List<Incident> dailyQueue = new ArrayList<>();
        if (systems == null || systems.isEmpty()) return dailyQueue;

        int wEasy = Math.max(3, 35 - (playerLevel * 8));
        int wClumsy = Math.max(2, 20 - (playerLevel * 5));
        int wRemoteWorker = 5;

        int wBruteForce = 10 + playerLevel;
        int wPortScan = 10 + playerLevel;
        int wDDoS = 5 + (playerLevel * 2);

        int wCompromised = 5 + (playerLevel * 4);
        int wInsiderThreat = 5 + (playerLevel * 4);

        int wSneaky = (playerLevel >= 2) ? (10 + (playerLevel * 5)) : 0;

        int totalWeight = wEasy + wClumsy + wRemoteWorker + wBruteForce + wPortScan + wDDoS + wCompromised + wInsiderThreat + wSneaky;

        for (int i = 0; i < totalSessions; i++) {
            MonitoredSystem targetSystem = systems.get(random.nextInt(systems.size()));

            int roll = random.nextInt(totalWeight);
            int currentBound = 0;

            currentBound += wEasy;
            if (roll < currentBound) { dailyQueue.add(createEmployeeSession(targetSystem, false)); continue; }

            currentBound += wClumsy;
            if (roll < currentBound) { dailyQueue.add(createEmployeeSession(targetSystem, true)); continue; }

            currentBound += wRemoteWorker;
            if (roll < currentBound) { dailyQueue.add(createRemoteWorkerSession(targetSystem)); continue; }

            currentBound += wBruteForce;
            if (roll < currentBound) { dailyQueue.add(createBruteForceSession(targetSystem)); continue; }

            currentBound += wPortScan;
            if (roll < currentBound) { dailyQueue.add(createPortScanSession(targetSystem)); continue; }

            currentBound += wDDoS;
            if (roll < currentBound) { dailyQueue.add(createDDoSSession(targetSystem)); continue; }

            currentBound += wCompromised;
            if (roll < currentBound) { dailyQueue.add(createCompromisedAccountSession(targetSystem)); continue; }

            currentBound += wInsiderThreat;
            if (roll < currentBound) { dailyQueue.add(createInsiderThreatSession(targetSystem)); continue; }

            if (random.nextBoolean()) {
                dailyQueue.add(createSneakyInsiderSession(targetSystem));
            } else {
                dailyQueue.add(createSneakyBlacklistSession(targetSystem));
            }
        }
        return dailyQueue;
    }

    private Incident createSneakyInsiderSession(MonitoredSystem system) {
        String ip = internalIPs.get(random.nextInt(internalIPs.size()));
        String name = firstNames.get(random.nextInt(firstNames.size()));
        String dept = random.nextBoolean() ? "hr" : "sales";
        String username = name + "." + dept;

        List<SecurityEvent> events = new ArrayList<>();

        LocalDateTime baseTime = LocalDateTime.now().withHour(random.nextInt(3) + 2).withMinute(random.nextInt(59));
        events.add(new LoginEvent(eventIdCounter++, baseTime, system, Severity.MEDIUM, username, "SUCCESS", ip));

        return new Incident(incidentIdCounter++, ip, events, true, Severity.MEDIUM);
    }

    private Incident createSneakyBlacklistSession(MonitoredSystem system) {
        String ip = attackerIPs.get(random.nextInt(attackerIPs.size()));
        List<SecurityEvent> events = new ArrayList<>();

        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(120));
        int port = random.nextBoolean() ? 80 : 443;

        Severity fakeSeverity = random.nextBoolean() ? Severity.LOW : Severity.MEDIUM;
        events.add(new NetworkEvent(eventIdCounter++, baseTime, system, fakeSeverity, ip, port, "TCP"));

        return new Incident(incidentIdCounter++, ip, events, true, fakeSeverity);
    }

    private Incident createEmployeeSession(MonitoredSystem system, boolean isClumsy) {
        String ip = internalIPs.get(random.nextInt(internalIPs.size()));
        String username = getRandomEmployee();
        List<SecurityEvent> events = new ArrayList<>();

        int failedAttempts = isClumsy ? (random.nextInt(3) + 1) : 0;
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(120));

        for (int i = 0; i < failedAttempts; i++) {
            events.add(new LoginEvent(eventIdCounter++, baseTime.plusSeconds(i * 10L), system, Severity.LOW, username, "FAILED", ip));
        }
        events.add(new LoginEvent(eventIdCounter++, baseTime.plusSeconds(failedAttempts * 10L + 5), system, Severity.LOW, username, "SUCCESS", ip));
        return new Incident(incidentIdCounter++, ip, events, false, isClumsy ? Severity.MEDIUM : Severity.LOW);
    }

    private Incident createBruteForceSession(MonitoredSystem system) {
        String ip = attackerIPs.get(random.nextInt(attackerIPs.size()));
        String targetAccount = targetAccounts.get(random.nextInt(targetAccounts.size()));
        List<SecurityEvent> events = new ArrayList<>();

        int attempts = random.nextInt(4) + 3;
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(120));

        for (int i = 0; i < attempts; i++) {
            events.add(new LoginEvent(eventIdCounter++, baseTime.plusSeconds(i * 2L), system, Severity.HIGH, targetAccount, "FAILED", ip));
        }
        return new Incident(incidentIdCounter++, ip, events, true, Severity.HIGH);
    }

    private Incident createPortScanSession(MonitoredSystem system) {
        String ip = attackerIPs.get(random.nextInt(attackerIPs.size()));
        List<SecurityEvent> events = new ArrayList<>();
        int[] commonPorts = {21, 22, 23, 25, 53, 80, 110, 443, 1433, 3306, 3389, 5432, 8080};
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(120));

        List<Integer> selectedPorts = new ArrayList<>();
        while (selectedPorts.size() < 3) {
            int randomPort = commonPorts[random.nextInt(commonPorts.length)];
            if (!selectedPorts.contains(randomPort)) {
                selectedPorts.add(randomPort);
            }
        }

        for (int i = 0; i < selectedPorts.size(); i++) {
            events.add(new NetworkEvent(eventIdCounter++, baseTime.plusSeconds(i), system, Severity.CRITICAL, ip, selectedPorts.get(i), "TCP"));
        }
        return new Incident(incidentIdCounter++, ip, events, true, Severity.CRITICAL);
    }

    private Incident createCompromisedAccountSession(MonitoredSystem system) {
        String ip = attackerIPs.get(random.nextInt(attackerIPs.size()));
        String validUsername = getRandomEmployee();
        List<SecurityEvent> events = new ArrayList<>();
        events.add(new LoginEvent(eventIdCounter++, LocalDateTime.now().minusMinutes(random.nextInt(120)), system, Severity.LOW, validUsername, "SUCCESS", ip));
        return new Incident(incidentIdCounter++, ip, events, true, Severity.LOW);
    }

    private Incident createInsiderThreatSession(MonitoredSystem system) {
        String ip = internalIPs.get(random.nextInt(internalIPs.size()));
        List<SecurityEvent> events = new ArrayList<>();
        events.add(new NetworkEvent(eventIdCounter++, LocalDateTime.now().minusMinutes(random.nextInt(120)), system, Severity.HIGH, ip, 22, "TCP"));
        return new Incident(incidentIdCounter++, ip, events, true, Severity.HIGH);
    }

    private Incident createDDoSSession(MonitoredSystem system) {
        String ip = attackerIPs.get(random.nextInt(attackerIPs.size()));
        List<SecurityEvent> events = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(random.nextInt(120));
        for (int i = 0; i < 10; i++) {
            events.add(new NetworkEvent(eventIdCounter++, baseTime.plusNanos(i * 100000000), system, Severity.CRITICAL, ip, 80, "TCP"));
        }
        return new Incident(incidentIdCounter++, ip, events, true, Severity.CRITICAL);
    }

    private Incident createRemoteWorkerSession(MonitoredSystem system) {
        String ip = (random.nextInt(200) + 1) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
        String executive = "exec.mgmt";
        List<SecurityEvent> events = new ArrayList<>();
        events.add(new LoginEvent(eventIdCounter++, LocalDateTime.now().minusMinutes(random.nextInt(120)), system, Severity.MEDIUM, executive, "SUCCESS", ip));
        return new Incident(incidentIdCounter++, ip, events, false, Severity.MEDIUM);
    }

    private String getRandomEmployee() {
        String name = firstNames.get(random.nextInt(firstNames.size()));
        String dept = departments.get(random.nextInt(departments.size()));
        return name + "." + dept;
    }
}