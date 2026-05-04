package repositories;

import db.DatabaseConnectionManager;
import models.MonitoredSystem;
import models.Severity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSystemRepository implements SystemRepository {

    private final Connection connection;

    public DatabaseSystemRepository() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    @Override
    public void add(MonitoredSystem system) {
        String sql = "INSERT INTO monitored_systems (system_name, ip_address, os_type, criticality_level) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, system.getName());
            pstmt.setString(2, system.getIpAddress());
            pstmt.setString(3, system.getOsType());
            pstmt.setString(4, system.getImportance().name());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    system.setId(generatedKeys.getInt(1));
                    System.out.println("[DB] System added successfully with DB generated ID: " + system.getId());
                }
            }

        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to add system: " + e.getMessage());
        }
    }

    @Override
    public List<MonitoredSystem> findAll() {
        List<MonitoredSystem> systems = new ArrayList<>();
        String sql = "SELECT * FROM monitored_systems";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("system_name");
                String ipAddress = rs.getString("ip_address");
                String osType = rs.getString("os_type");
                Severity importance = Severity.valueOf(rs.getString("criticality_level"));

                systems.add(new MonitoredSystem(id, name, ipAddress, osType, importance));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to read systems: " + e.getMessage());
        }
        return systems;
    }

    @Override
    public void update(MonitoredSystem system) {
        String sql = "UPDATE monitored_systems SET system_name = ?, ip_address = ?, os_type = ?, criticality_level = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, system.getName());
            pstmt.setString(2, system.getIpAddress());
            pstmt.setString(3, system.getOsType());
            pstmt.setString(4, system.getImportance().name());
            pstmt.setInt(5, system.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] System updated successfully: " + system.getName());
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update system: " + e.getMessage());
        }
    }

    @Override
    public void delete(String ipAddress) {
        String sql = "DELETE FROM monitored_systems WHERE ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] System deleted successfully: " + ipAddress);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to delete system: " + e.getMessage());
        }
    }
}