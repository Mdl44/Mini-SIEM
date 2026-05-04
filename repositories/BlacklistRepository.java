package repositories;

import java.util.List;

public interface BlacklistRepository {
    List<String> getAll();
    boolean add(String ip);

    default boolean remove(String ip) {
        System.out.println("Remove operation is not supported in legacy file-based repository.");
        return false;
    }
}