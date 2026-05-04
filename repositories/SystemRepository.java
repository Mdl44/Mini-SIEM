package repositories;

import models.MonitoredSystem;
import java.util.List;

public interface SystemRepository {
    List<MonitoredSystem> findAll();
    void add(MonitoredSystem system);

    default void update(MonitoredSystem system) {
        System.out.println("Update operation is not supported in legacy file-based repository.");
    }

    default void delete(String ipAddress) {
        System.out.println("Delete operation is not supported in legacy file-based repository.");
    }
}