package repositories;

import models.Achievement;
import models.SOCAnalyst;
import java.util.List;
import java.util.Optional;

public interface AnalystRepository extends CrudRepository<SOCAnalyst, Integer> {
    Optional<SOCAnalyst> findByUsername(String username);
    void updateAll(List<SOCAnalyst> analysts);
    Achievement saveAchievement(int userId, String achievementName);

    default void update(SOCAnalyst analyst) {
        System.out.println("Update operation is not supported in legacy file-based repository.");
    }

    default void delete(Integer id) {
        System.out.println("Delete operation is not supported in legacy file-based repository.");
    }
}