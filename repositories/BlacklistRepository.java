package repositories;

import java.util.List;

public interface BlacklistRepository {
    List<String> getAll();
    boolean add(String ip);
}