package repositories;

import models.SOCAnalyst;
import java.util.List;
import java.util.Optional;

public interface AnalystRepository {
    List<SOCAnalyst> findAll();
    Optional<SOCAnalyst> findByUsername(String username);
    void save(SOCAnalyst analyst);
    void updateAll(List<SOCAnalyst> analysts);
}