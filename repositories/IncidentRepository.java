package repositories;

import models.Incident;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository {
    void save(Incident incident);
    Optional<Incident> findById(int id);
    List<Incident> findAll();
    void update(Incident incident);
    void delete(int id);
}