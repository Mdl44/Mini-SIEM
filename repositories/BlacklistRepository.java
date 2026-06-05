package repositories;

import java.util.Optional;

public interface BlacklistRepository extends CrudRepository<String, String> {

    @Override
    default void update(String ip) {
        System.out.println("Update operation is not supported for simple blacklist strings.");
    }

    default void delete(String ip) {
        System.out.println("Delete operation is not supported in legacy file-based repository.");
    }
}