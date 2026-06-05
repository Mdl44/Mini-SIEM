package repositories;

import db.DatabaseConnectionManager;
import java.sql.Connection;

public abstract class AbstractDatabaseRepository<T, ID> implements CrudRepository<T, ID> {
    protected final Connection connection;

    public AbstractDatabaseRepository() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }
}