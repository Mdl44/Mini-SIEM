package repositories;

import models.Achievement;
import models.SOCAnalyst;
import java.util.List;
import java.util.Optional;

public interface AnalystRepository {
    List<SOCAnalyst> findAll();
    Optional<SOCAnalyst> findByUsername(String username);
    void save(SOCAnalyst analyst);
    void updateAll(List<SOCAnalyst> analysts);
    Achievement saveAchievement(int userId, String achievementName);

    default void update(SOCAnalyst analyst) {
        System.out.println("Update operation is not supported in legacy file-based repository.");
    }

    default void delete(int id) {
        System.out.println("Delete operation is not supported in legacy file-based repository.");
    }
}