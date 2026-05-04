package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private Connection connection;

    private final String URL = "jdbc:postgresql://localhost:5432/siem_db";
    private final String USER = "java_admin";
    private final String PASSWORD = "44128163";

    private DatabaseConnectionManager() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connection to PostgreSQL has been established.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Error connecting to database: " + e.getMessage());
        }
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
