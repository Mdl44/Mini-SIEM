import repositories.*;
import views.GameMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("[SYSTEM] Initializing SIEM Simulator Services...");
        System.out.println("[SYSTEM] Connecting to PostgreSQL Database...");

        AnalystRepository analystRepo = new DatabaseAnalystRepository();
        SystemRepository systemRepo = new DatabaseSystemRepository();
        BlacklistRepository blacklistRepo = new DatabaseBlacklistRepository();
        IncidentRepository incidentRepo = DatabaseIncidentRepository.getInstance();
        TrafficDataRepository trafficRepo = new FileTrafficDataRepository();

        GameMenu menu = new GameMenu(analystRepo, systemRepo, blacklistRepo, trafficRepo, incidentRepo);

        System.out.println("[SYSTEM] Boot sequence complete. Launching terminal...\n");

        menu.start();
    }
}