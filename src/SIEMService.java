import java.time.LocalDateTime;
import java.util.*;
public class SIEMService {
    private Map<Integer, User> users;
    private List<SecurityEvent> allLogs;
    private List<MonitoredSystem> monitoredSystems;
    private List<DetectionRule> rules;
    private List<IoCBlackList> blacklist;
    private Set<Incident> activeIncidents;
    private List<InvestigationReport> reports;

    public SIEMService() {
        users = new HashMap<>();
        allLogs = new ArrayList<>();
        monitoredSystems = new ArrayList<>();
        rules = new ArrayList<>();
        blacklist = new ArrayList<>();
        activeIncidents = new HashSet<>();
        reports = new ArrayList<>();
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }
    public void addSecurityEvent(SecurityEvent securityEvent) {
        allLogs.add(securityEvent);
    }
    public void addRule(DetectionRule rule) {
        rules.add(rule);
    }
    public void detectBruteForce() {
        System.out.println("Detecting Brute Force...");

        Map<String, Integer> failedAttempsPerIp = new HashMap<>();
        for (SecurityEvent event : allLogs) {
            if(event instanceof LoginEvent) {
                LoginEvent loginEvent = (LoginEvent) event;
                if(loginEvent.getStatus().equals("FAILED")) {
                    String ipSuspect = loginEvent.getSourceIP();
                    failedAttempsPerIp.put(ipSuspect, failedAttempsPerIp.getOrDefault(ipSuspect, 0) + 1);
                }
            }
        }
        int threshold = 5;

        for (Map.Entry<String, Integer> entry : failedAttempsPerIp.entrySet()) {
            String ip =  entry.getKey();
            int failedCount = entry.getValue();

            if(failedCount >= threshold) {
                System.out.println("Detected Brute Force: " + ip);

                int newIncidentId = activeIncidents.size() + 1;
                Incident newIncident = new Incident(newIncidentId, "HIGH", "OPEN", LocalDateTime.now());

                activeIncidents.add(newIncident);
            }
        }

        System.out.println("Scan finished.");
    }
    public void checkBlacklist() {
        Set<String>badIp = new HashSet<>();
        for (IoCBlackList ioc : blacklist) {
            if(ioc.getIndicatorType().equals("IP")) {
                badIp.add(ioc.getIndicatorValue());
            }
        }

        Set<String>blackListedIPs = new HashSet<>();

        for (SecurityEvent event : allLogs) {
            String ipToCheck = null;
            if(event instanceof LoginEvent) {
                ipToCheck = ((LoginEvent) event).getSourceIP();
            }
            else if(event instanceof NetworkEvent) {
                ipToCheck = ((NetworkEvent) event).getDestinationIP();
            }

            if(ipToCheck != null && badIp.contains(ipToCheck)) {
                if(!blackListedIPs.contains(ipToCheck)) {
                    System.out.println("Traffic detected from blacklisted IP: " + ipToCheck);

                    int newIncidentId = activeIncidents.size() + 1;
                    Incident criticalIncident = new Incident(newIncidentId, "CRITICAL", "OPEN", LocalDateTime.now());
                    activeIncidents.add(criticalIncident);

                    blackListedIPs.add(ipToCheck);
                }
            }
        }
        System.out.println("Scan finished.");
    }
    public void assignIncident(Incident incident) {
        System.out.println("Assigning incident to " + incident.getId());

        SOCAnalyst availableAnalyst = null;

        for (User user : users.values()) {
            if (user instanceof SOCAnalyst) {
                SOCAnalyst socAnalyst = (SOCAnalyst) user;
                if (socAnalyst.canAcceptTask()) {
                    availableAnalyst = socAnalyst;
                    break;
                }
            }
        }
        if(availableAnalyst == null) {
            System.out.println("Failed to assign incident " + incident.getId());
            return;
        }

        incident.setAssignedAnalyst(availableAnalyst.getId());
        incident.setStatus("IN_PROGRESS");
        availableAnalyst.assignTask();

        System.out.println("Assigned incident " + incident.getId() + "to " + availableAnalyst.getName());
    }
    public List<SecurityEvent> searchAttackTraces(String ipSuspect) {
        List<SecurityEvent> traces = new ArrayList<>();

        for(SecurityEvent event : allLogs) {
            if(event instanceof LoginEvent) {
                LoginEvent loginEvent = (LoginEvent) event;
                if(loginEvent.getSourceIP().equals(ipSuspect)) {
                    traces.add(loginEvent);
                }
            }
        }
        return traces;
    }
    public Map<String, Integer> calculateTrafficStatistics() {
        System.out.println("Calculating traffic statistics...");

        Map<String, Integer> stats = new HashMap<>();

        for(SecurityEvent event : allLogs) {
            String systemName = event.getSourceSystem();

            stats.put(systemName, stats.getOrDefault(systemName, 0) + 1);

            for(Map.Entry<String, Integer> entry : stats.entrySet()) {
                System.out.println("System: " + entry.getKey() + "| Logs: " + entry.getValue());
            }
        }
        return stats;
    }
}
