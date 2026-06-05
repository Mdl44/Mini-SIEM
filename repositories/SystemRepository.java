package repositories;

import models.MonitoredSystem;

public interface SystemRepository extends CrudRepository<MonitoredSystem, Integer> {
    void delete(String ipAddress);
}