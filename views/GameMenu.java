package views;

import models.*;
import repositories.*;
import services.AuditService;
import services.SIEMService;
import services.TrafficSimulator;

import java.util.*;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class GameMenu {
    private final Scanner scanner;
    private final SIEMService siemService;
    private final TrafficSimulator trafficSimulator;
    private final AnalystRepository analystRepo;
    private final IncidentRepository incidentRepo;

    public GameMenu(AnalystRepository analystRepo, SystemRepository systemRepo, BlacklistRepository blacklistRepo, TrafficDataRepository trafficRepo, IncidentRepository incidentRepo) {
        this.incidentRepo = incidentRepo;
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
            System.out.println("3. View Wall of Fame (Leaderboard)");
            System.out.println("4. Exit");
            System.out.print("Command: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String user = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine().trim();

                    Optional<SOCAnalyst> foundUser = analystRepo.findByUsername(user);

                    if (foundUser.isPresent() && foundUser.get().getPassword().equals(pass)) {
                        SOCAnalyst loggedIn = foundUser.get();

                        if (loggedIn.getCurrentLives() <= 0) {
                            System.out.println("\n[ACCESS DENIED] Account terminated due to poor performance.");
                        } else {
                            AuditService.getInstance().logAction("LOGIN_ANALYST_" + loggedIn.getName());
                            analystLoop(loggedIn);
                            analystRepo.update(loggedIn);
                        }
                    } else {
                        System.out.println("\n[ERROR] Invalid credentials.");
                    }
                    break;
                case "2":
                    Admin admin = new Admin(1, "SuperAdmin", "root@soc.local");
                    AuditService.getInstance().logAction("ADMIN_LOGIN_SUCCESS");
                    adminLoop(admin);
                    break;
                case "3":
                    showLeaderboard();
                    break;
                case "4":
                    System.out.println("System shutting down. Stay secure!");
                    return;
                default:
                    System.out.println("[ERROR] Invalid command.");
            }
        }
    }

    private void unlockAchievement(SOCAnalyst player, String achievementName) {
        boolean hasIt = player.getAchievements().stream().anyMatch(a -> a.getName().equals(achievementName));
        if (!hasIt) {
            Achievement awarded = analystRepo.saveAchievement(player.getId(), achievementName);
            if (awarded != null) {
                player.getAchievements().add(awarded);
                player.addCredits(awarded.getBonusCredits());

                System.out.println("\n==================================================");
                System.out.println("[ACHIEVEMENT UNLOCKED] " + awarded.getName());
                System.out.println("   " + awarded.getDescription());
                if (awarded.getBonusCredits() > 0) {
                    System.out.println("   Bonus Awarded: $" + awarded.getBonusCredits());
                }
                System.out.println("==================================================\n");
            }
        }
    }

    private void showLeaderboard() {
        System.out.println("\n==================================================");
        System.out.println("            SOC WALL OF FAME (TOP 5)              ");
        System.out.println("==================================================");

        List<SOCAnalyst> allAnalysts = analystRepo.findAll();

        allAnalysts.sort((a, b) -> {
            if (b.getDaysSurvived() != a.getDaysSurvived()) {
                return Integer.compare(b.getDaysSurvived(), a.getDaysSurvived());
            }
            if (b.getRankLevel() != a.getRankLevel()) {
                return Integer.compare(b.getRankLevel(), a.getRankLevel());
            }
            return Integer.compare(b.getCredits(), a.getCredits());
        });

        if (allAnalysts.isEmpty()) {
            System.out.println("No records found. The SOC is completely empty.");
        } else {
            int limit = Math.min(5, allAnalysts.size());
            for (int i = 0; i < limit; i++) {
                SOCAnalyst p = allAnalysts.get(i);
                System.out.printf("%d. %-15s | Lvl: %-2d | Survived: %-3d days | Net Worth: $%d\n",
                        (i + 1), p.getName().toUpperCase(), p.getRankLevel(), p.getDaysSurvived(), p.getCredits());
            }
        }
        System.out.println("==================================================\n");
    }

    private void analystLoop(SOCAnalyst player) {
        player.printDashboard();
        System.out.println("Press ENTER to begin your shift...");
        scanner.nextLine();

        List<MonitoredSystem> systems = siemService.getMonitoredSystems();
        List<Incident> dailySessions = trafficSimulator.generateDailyQueue(systems, 10, player.getRankLevel());
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

        long startTime = System.currentTimeMillis();

        while (true) {
            System.out.println("\nDesk Tools: [inspect] | [systems] | [blacklist] | [add_blacklist] | [ask_senior] (" + player.getCurrentSpecialHelps() + " left)");
            System.out.println("Decisions:  [allow] | [block]");
            System.out.print("Action> ");

            String action = scanner.nextLine().trim().toLowerCase();
            long timeTakenSeconds = (System.currentTimeMillis() - startTime) / 1000;

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
                        AuditService.getInstance().logAction("SENIOR_ANALYST_CALLED");
                        incidentRepo.save(currentCase);
                        return new InvestigationReport(reportId, currentCase.getId(), player.getId(), seniorDecision, isThreat);
                    } else {
                        System.out.println("[ERROR] You have no Senior Calls left!");
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
                case "block":
                    String finalDecision = action.toUpperCase() + "ED";
                    int maxAllowedTime = (currentCase.getSeverity() == Severity.CRITICAL) ? 20 : 30;

                    if (timeTakenSeconds > maxAllowedTime) {
                        System.out.println("\n[SLA BREACH] You took " + timeTakenSeconds + " seconds to respond!");
                        System.out.println("[DISASTER] The critical attack overwhelmed the system before you could act.");

                        unlockAchievement(player, "SLA Breacher");

                        AuditService.getInstance().logAction("SLA_BREACH_CRITICAL");
                        incidentRepo.save(currentCase);
                        return new InvestigationReport(reportId, currentCase.getId(), player.getId(), "ALLOWED", true);
                    }

                    if (currentCase.getSeverity() == Severity.CRITICAL && timeTakenSeconds <= 3) {
                        unlockAchievement(player, "The Flash");
                    }
                    if (action.equals("block") && currentCase.isMalicious()) {
                        unlockAchievement(player, "First Blood");
                    }

                    System.out.println("[STAMP] Traffic " + finalDecision + " in " + timeTakenSeconds + " seconds. Next!");
                    AuditService.getInstance().logAction(action.toUpperCase() + "_TRAFFIC");
                    incidentRepo.save(currentCase);
                    return new InvestigationReport(reportId, currentCase.getId(), player.getId(), finalDecision, currentCase.isMalicious());
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
        int shiftIncome = 50;

        for (InvestigationReport report : reports) {
            if (!report.isCorrect()) {
                System.out.println("Case #" + report.getIncidentId() + " -> " + report.getGradingOutcome());
                mistakes++;
                shiftIncome -= 25;

                if (report.getGradingOutcome().contains("False Positive")) {
                    shiftStatistics.put("False Positive (Blocked Employee)", shiftStatistics.get("False Positive (Blocked Employee)") + 1);
                } else {
                    shiftStatistics.put("False Negative (Missed Hacker)", shiftStatistics.get("False Negative (Missed Hacker)") + 1);
                }
            } else {
                shiftStatistics.put("Flawless Decisions", shiftStatistics.get("Flawless Decisions") + 1);
                shiftIncome += 10;
            }
        }

        System.out.println("\n--- SHIFT STATISTICS ---");
        for (Map.Entry<String, Integer> entry : shiftStatistics.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("------------------------");

        if (shiftStatistics.get("False Positive (Blocked Employee)") >= 3) {
            unlockAchievement(player, "Paranoia");
        }

        if (shiftIncome < 0) shiftIncome = 0;

        System.out.println("\n[FINANCIAL DEPT] Base Salary: $50");
        System.out.println("[FINANCIAL DEPT] Performance Adjustments: " + (shiftIncome - 50));
        System.out.println("[FINANCIAL DEPT] Total payout for today: $" + shiftIncome);
        player.addCredits(shiftIncome);

        if (player.getCredits() >= 500) {
            unlockAchievement(player, "Rich Analyst");
        }

        if (mistakes == 0) {
            System.out.println("\nFlawless shift! Your supervisor is pleased.");
            player.recordFlawlessDay();

            if (player.getDaysSurvived() >= 7) {
                unlockAchievement(player, "Iron Wall");
            }
        } else {
            System.out.println("\nYou made " + mistakes + " critical mistakes.");
            for (int i = 0; i < mistakes; i++) {
                player.loseLife();
            }
            System.out.println("Health penalty applied. Remaining warnings: " + player.getCurrentLives());

            if (player.getCurrentLives() <= 0) {
                System.out.println("\n[TERMINATED] You have lost all lives. Please hand in your badge.");
                unlockAchievement(player, "You Are Fired");
                analystRepo.update(player);
                return;
            } else {
                player.recordSuccessfulDay();
            }
        }

        if (player.getRankLevel() >= 3) {
            unlockAchievement(player, "Cyber Legend");
        }

        if (player.getDaysSurvived() > 0 && player.getDaysSurvived() % 7 == 0) {
            int weeksCompleted = player.getDaysSurvived() / 7;
            System.out.println("\n==================================================");
            System.out.println("   CONGRATULATIONS! YOU SURVIVED ANOTHER HELL WEEK! ");
            System.out.println("   You are a true Veteran. Weeks completed: " + weeksCompleted + "   ");
            System.out.println("==================================================");
        }

        secureArchiveShift(reports, player.getName());
        socShopMenu(player);

        System.out.println("Press ENTER to return to the locker room...");
        scanner.nextLine();
    }

    private void socShopMenu(SOCAnalyst player) {
        label:
        while (true) {
            System.out.println("\n==================================================");
            System.out.println("           THE DARK WEB SOC SHOP                  ");
            System.out.println("           Your Balance: $" + player.getCredits());
            System.out.println("==================================================");
            System.out.println("1. Buy Cyber-Coffee (Restores 1 HP) - $100");
            System.out.println("2. Bribe a Senior (Grants 1 Extra Call) - $150");
            System.out.println("3. Exit Shop and Go Home");
            System.out.print("Selection: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (player.getCurrentLives() >= player.getMaxLives()) {
                        System.out.println("[!] You are already at max health.");
                    } else if (player.spendCredits(100)) {
                        player.restoreHealth();
                        System.out.println("[SUCCESS] Drank Cyber-Coffee. Health is now " + player.getCurrentLives() + "/" + player.getMaxLives());
                        unlockAchievement(player, "Coffee Addict");
                        AuditService.getInstance().logAction("BOUGHT_HP");
                    } else {
                        System.out.println("[DECLINED] Insufficient funds!");
                    }
                    break;
                case "2":
                    if (player.spendCredits(150)) {
                        player.addExtraSeniorCall();
                        System.out.println("[SUCCESS] Bribed a senior. You have " + player.getCurrentSpecialHelps() + " senior calls for the next shift.");
                        unlockAchievement(player, "Bribery");
                        AuditService.getInstance().logAction("BOUGHT_SENIOR_CALL");
                    } else {
                        System.out.println("[DECLINED] Insufficient funds!");
                    }
                    break;
                case "3":
                    System.out.println("Leaving the shop...");
                    break label;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
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

                    MonitoredSystem newSys = new MonitoredSystem(0, name, ip, os, Severity.valueOf(importance));
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

                    SOCAnalyst newUser = new SOCAnalyst(0, newName, newName + "@soc.local", newPass);
                    analystRepo.save(newUser);
                    AuditService.getInstance().logAction("ADMIN_CREATED_ANALYST_" + newName);
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
                case "6":
                    System.out.print("Enter Analyst ID to terminate: ");
                    try {
                        int idToDelete = Integer.parseInt(scanner.nextLine().trim());
                        analystRepo.delete(idToDelete);
                    } catch (NumberFormatException e) {
                        System.out.println("[ERROR] Invalid ID format. Please enter a numeric value.");
                    }
                    break;
                default:
                    System.out.println("[ERROR] Invalid command.");
                    break;
            }
        }
    }
}