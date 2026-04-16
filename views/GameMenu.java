package views;

import models.*;
import services.SIEMService;
import services.TrafficSimulator;
import repositories.AnalystRepository;
import repositories.SystemRepository;
import repositories.BlacklistRepository;
import repositories.TrafficDataRepository;

import java.util.*;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class GameMenu {
    private final Scanner scanner;
    private final SIEMService siemService;
    private final TrafficSimulator trafficSimulator;
    private final AnalystRepository analystRepo;

    public GameMenu(AnalystRepository analystRepo, SystemRepository systemRepo, BlacklistRepository blacklistRepo, TrafficDataRepository trafficRepo) {
        this.scanner = new Scanner(System.in);
        this.analystRepo = analystRepo;
        this.siemService = new SIEMService(systemRepo, blacklistRepo);
        this.trafficSimulator = new TrafficSimulator(trafficRepo);
    }

    private void secureArchiveShift(List<InvestigationReport> reports, String analystName) {
        String fileName = "resources/audit_" + analystName + "_" + System.currentTimeMillis() + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(reports);
            System.out.println("[AUDIT] Shift reports securely archived to binary storage.");
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to archive shift reports: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("==================================================");
        System.out.println("                  SIEM SIMULATOR                  ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\nSelect your role:");
            System.out.println("1. Login as SOC Analyst (Start Shift)");
            System.out.println("2. Login as Admin (System Configuration)");
            System.out.println("3. Exit");
            System.out.print("Command: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String user = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine().trim();

                    Optional<SOCAnalyst> foundUser = analystRepo.findByUsername(user); //pentru NullPointerException

                    if (foundUser.isPresent() && foundUser.get().getPassword().equals(pass)) {
                        SOCAnalyst loggedIn = foundUser.get();

                        if (loggedIn.getCurrentLives() <= 0) {
                            System.out.println("\n[ACCESS DENIED] Account terminated due to poor performance.");
                        } else {
                            analystLoop(loggedIn);
                            analystRepo.save(loggedIn); // salvez progresul
                        }
                    } else {
                        System.out.println("\n[ERROR] Invalid credentials.");
                    }
                    break;
                case "2":
                    Admin admin = new Admin(1, "SuperAdmin", "root@soc.local");
                    adminLoop(admin);
                    break;
                case "3":
                    System.out.println("System shutting down.");
                    return;
                default:
                    System.out.println("[ERROR] Invalid command.");
            }
        }
    }

    private void analystLoop(SOCAnalyst player) {
        player.printDashboard();
        System.out.println("Press ENTER to begin your shift...");
        scanner.nextLine();

        List<MonitoredSystem> systems = siemService.getMonitoredSystems();
        List<Incident> dailySessions = trafficSimulator.generateDailyQueue(systems, 10, player.getRankLevel()); //generez "dosarele"
        siemService.loadDailyQueue(dailySessions);

        List<InvestigationReport> shiftReports = new ArrayList<>();
        int reportIdCounter = 1;

        while (siemService.getRemainingIncidentsCount() > 0) {
            Incident currentCase = siemService.claimNextIncident();
            shiftReports.add(investigateCase(currentCase, reportIdCounter++, player));
        }

        evaluateShift(shiftReports, player);
    }

    private InvestigationReport investigateCase(Incident currentCase, int reportId, SOCAnalyst player) {
        System.out.println("\n==================================================");
        System.out.println("NEXT IN LINE (Queue: " + siemService.getRemainingIncidentsCount() + ")");
        System.out.println(currentCase);
        System.out.println("==================================================");

        while (true) {
            System.out.println("\nDesk Tools: [inspect] | [systems] | [blacklist] | [add_blacklist] | [ask_senior] (" + player.getCurrentSpecialHelps() + " left)");
            System.out.println("Decisions:  [allow] | [block]");
            System.out.print("Action> ");

            String action = scanner.nextLine().trim().toLowerCase();

            switch (action) {
                case "inspect":
                    System.out.println("\n--- PROVIDED TRAFFIC LOGS ---");
                    for (SecurityEvent event : currentCase.getEvents()) {
                        System.out.println(event);
                    }
                    System.out.println("-----------------------------");
                    break;
                case "ask_senior":
                    if (player.getCurrentSpecialHelps() > 0) {
                        player.useSpecialHelp();
                        boolean isThreat = currentCase.isMalicious();
                        String seniorDecision = isThreat ? "BLOCKED" : "ALLOWED";

                        System.out.println("[SENIOR ANALYST] Let me look... This is " + (isThreat ? "a clear threat." : "a false alarm."));
                        System.out.println("[STAMP] Senior has " + seniorDecision + " the traffic for you.");

                        return new InvestigationReport(reportId, currentCase.getId(), player.getId(), seniorDecision, isThreat);
                    } else {
                        System.out.println("[ERROR] You have no Senior Calls left for today! You are on your own.");
                    }
                    break;
                case "systems":
                    siemService.printMonitoredSystems();
                    break;
                case "blacklist":
                    siemService.printBlacklist();
                    break;
                case "add_blacklist":
                    siemService.addBlacklistIp(currentCase.getSourceIp());
                    break;
                case "allow":
                    System.out.println("[STAMP] Traffic Allowed. Next!");
                    return new InvestigationReport(reportId, currentCase.getId(), player.getId(), "ALLOWED", currentCase.isMalicious());
                case "block":
                    System.out.println("[STAMP] Traffic Blocked. Next!");
                    return new InvestigationReport(reportId, currentCase.getId(), player.getId(), "BLOCKED", currentCase.isMalicious());
                default:
                    System.out.println("[ERROR] Invalid desk command.");
            }
        }
    }

    private void evaluateShift(List<InvestigationReport> reports, SOCAnalyst player) {
        System.out.println("\n==================================================");
        System.out.println("               SHIFT END REPORT                   ");
        System.out.println("==================================================");

        Map<String, Integer> shiftStatistics = new HashMap<>();
        shiftStatistics.put("Flawless Decisions", 0);
        shiftStatistics.put("False Positive (Blocked Employee)", 0);
        shiftStatistics.put("False Negative (Missed Hacker)", 0);

        int mistakes = 0;

        for (InvestigationReport report : reports) {
            if (!report.isCorrect()) {
                System.out.println("Case #" + report.getIncidentId() + " -> " + report.getGradingOutcome());
                mistakes++;

                if (report.getGradingOutcome().contains("False Positive")) {
                    shiftStatistics.put("False Positive (Blocked Employee)", shiftStatistics.get("False Positive (Blocked Employee)") + 1);
                } else {
                    shiftStatistics.put("False Negative (Missed Hacker)", shiftStatistics.get("False Negative (Missed Hacker)") + 1);
                }
            } else {
                shiftStatistics.put("Flawless Decisions", shiftStatistics.get("Flawless Decisions") + 1);
            }
        }

        System.out.println("\n--- SHIFT STATISTICS ---");
        for (Map.Entry<String, Integer> entry : shiftStatistics.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("------------------------");

        if (mistakes == 0) {
            System.out.println("Flawless shift! Your supervisor is pleased.");
            player.recordFlawlessDay();
        } else {
            System.out.println("\nYou made " + mistakes + " critical mistakes.");
            for (int i = 0; i < mistakes; i++) {
                player.loseLife();
            }
            System.out.println("Health penalty applied. Remaining warnings: " + player.getCurrentLives());

            if (player.getCurrentLives() <= 0) {
                System.out.println("\n[TERMINATED] You have lost all lives. Please hand in your badge.");
            } else {
                player.recordSuccessfulDay();
            }
        }

        if (player.getDaysSurvived() > 0 && player.getDaysSurvived() % 7 == 0) {
            int weeksCompleted = player.getDaysSurvived() / 7;

            System.out.println("\n==================================================");
            System.out.println("   CONGRATULATIONS! YOU SURVIVED ANOTHER HELL WEEK! ");
            System.out.println("   You are a true Veteran. Weeks completed: " + weeksCompleted + "   ");
            System.out.println("==================================================");

            analystRepo.save(player);
        }

        secureArchiveShift(reports, player.getName());

        System.out.println("Press ENTER to return to the locker room...");
        scanner.nextLine();
    }

    private void adminLoop(Admin admin) {
        admin.printDashboard();
        label:
        while (true) {
            System.out.println("\n[Admin Menu]");
            System.out.println("1. View monitored systems");
            System.out.println("2. Add new system");
            System.out.println("3. Create new Analyst profile");
            System.out.println("4. View Analyst roster");
            System.out.println("5. Logout");
            System.out.print("Command: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    siemService.printMonitoredSystems();
                    break;
                case "2":
                    System.out.print("System Name (e.g., WebServer): ");
                    String name = scanner.nextLine().trim();

                    String ip;
                    while (true) {
                        System.out.print("IP Address (IPv4): ");
                        ip = scanner.nextLine().trim();

                        if (!ip.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")) {
                            System.out.println("[ERROR] Invalid IP format. Try again.");
                            continue;
                        }

                        boolean duplicateIp = false;
                        for (MonitoredSystem sys : siemService.getMonitoredSystems()) {
                            if (sys.getIpAddress().equals(ip)) {
                                duplicateIp = true;
                                break;
                            }
                        }

                        if (duplicateIp) {
                            System.out.println("[ERROR] IP Address is already in use by another system! Try again.");
                            continue;
                        }

                        break;
                    }
                    System.out.print("Operating System (e.g., Windows, Linux, macOS): ");
                    String os = scanner.nextLine().trim();
                    String importance;
                    while (true) {
                        System.out.print("Importance (LOW/MEDIUM/HIGH/CRITICAL): ");
                        importance = scanner.nextLine().trim().toUpperCase();
                        if (importance.equals("LOW") || importance.equals("MEDIUM") ||
                                importance.equals("HIGH") || importance.equals("CRITICAL")) {
                            break;
                        }
                        System.out.println("[ERROR] Invalid priority level. Try again.");
                    }

                    List<MonitoredSystem> currentSystems = siemService.getMonitoredSystems();
                    int newSysId = 1;
                    for (MonitoredSystem sys : currentSystems) {
                        if (sys.getId() >= newSysId) {
                            newSysId = sys.getId() + 1;
                        }
                    }

                    MonitoredSystem newSys = new MonitoredSystem(newSysId, name, ip, os, importance);
                    siemService.addMonitoredSystem(newSys);
                    System.out.println("[SUCCESS] System added.");
                    break;
                case "3": {
                    String newName;
                    while (true) {
                        System.out.print("New Analyst Username: ");
                        newName = scanner.nextLine().trim();

                        if (analystRepo.findByUsername(newName).isPresent()) {
                            System.out.println("[ERROR] This username is already taken! Choose another one.");
                        } else {
                            break;
                        }
                    }

                    System.out.print("New Password: ");
                    String newPass = scanner.nextLine().trim();
                    List<SOCAnalyst> currentRoster = analystRepo.findAll();
                    int newAnalystId = 1;
                    for (SOCAnalyst analyst : currentRoster) {
                        if (analyst.getId() >= newAnalystId) {
                            newAnalystId = analyst.getId() + 1;
                        }
                    }
                    SOCAnalyst newUser = new SOCAnalyst(newAnalystId, newName, newName + "@soc.local", newPass);
                    analystRepo.save(newUser);
                    System.out.println("[SUCCESS] New analyst profile created. They can now login from the main menu.");
                    break;
                }
                case "4": {
                    System.out.println("\n--- SOC ANALYST ROSTER ---");
                    List<SOCAnalyst> currentRoster = analystRepo.findAll();

                    if (currentRoster.isEmpty()) {
                        System.out.println("No analysts in the database.");
                    } else {
                        for (SOCAnalyst analyst : currentRoster) {
                            System.out.println("ID: " + analyst.getId() +
                                    " | Username: " + analyst.getName() +
                                    " | Lvl: " + analyst.getRankLevel() +
                                    " | HP: " + analyst.getCurrentLives() + "/" + analyst.getMaxLives() +
                                    " | Experience: " + analyst.getDaysSurvived() + " shifts");
                        }
                    }
                    System.out.println("--------------------------");
                    break;
                }
                case "5":
                    break label;
                default:
                    System.out.println("[ERROR] Invalid command.");
                    break;
            }
        }
    }
}