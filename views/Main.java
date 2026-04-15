import repositories.*;
import views.GameMenu;

void main() {
    AnalystRepository analystRepo = new FileAnalystRepository();
    SystemRepository systemRepo = new FileSystemRepository();
    BlacklistRepository blacklistRepo = new FileBlacklistRepository();
    TrafficDataRepository trafficRepo = new FileTrafficDataRepository();

    GameMenu menu = new GameMenu(analystRepo, systemRepo, blacklistRepo, trafficRepo);
    menu.start();
}