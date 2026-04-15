package repositories;

import models.MonitoredSystem;
import java.util.List;

public interface SystemRepository {
    List<MonitoredSystem> findAll();
    void add(MonitoredSystem system);
}