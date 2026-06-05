package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;
    private Connection connection;

    private String URL;
    private String USER;
    private String PASSWORD;

    private DatabaseConnectionManager() {
        loadConfig();

        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connection to PostgreSQL has been established.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Error connecting to database: " + e.getMessage());
        }
    }

    private void loadConfig() {
        try (java.io.FileInputStream input = new java.io.FileInputStream("resources/db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");
        } catch (Exception e) {
            System.out.println("[ERROR] No db.properties file found in resources folder !");
            e.printStackTrace();
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