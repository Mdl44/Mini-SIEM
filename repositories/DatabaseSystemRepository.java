package repositories;

import models.MonitoredSystem;
import models.Severity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseSystemRepository extends AbstractDatabaseRepository<MonitoredSystem, Integer> implements SystemRepository {

    private static DatabaseSystemRepository instance;

    private DatabaseSystemRepository() {
        super();
    }

    public static DatabaseSystemRepository getInstance() {
        if (instance == null) {
            instance = new DatabaseSystemRepository();
        }
        return instance;
    }

    @Override
    public void save(MonitoredSystem system) {
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
    public Optional<MonitoredSystem> findById(Integer id) {
        String sql = "SELECT * FROM monitored_systems WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(new MonitoredSystem(rs.getInt("id"), rs.getString("system_name"), rs.getString("ip_address"), rs.getString("os_type"), Severity.valueOf(rs.getString("criticality_level"))));
            }
        } catch (SQLException e) { }
        return Optional.empty();
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM monitored_systems WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    @Override
    public List<MonitoredSystem> findAll() {
        List<MonitoredSystem> systems = new ArrayList<>();
        String sql = "SELECT * FROM monitored_systems";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                systems.add(new MonitoredSystem(rs.getInt("id"), rs.getString("system_name"), rs.getString("ip_address"), rs.getString("os_type"), Severity.valueOf(rs.getString("criticality_level"))));
            }
        } catch (SQLException e) { }
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
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }

    @Override
    public void delete(String ipAddress) {
        String sql = "DELETE FROM monitored_systems WHERE ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ipAddress);
            pstmt.executeUpdate();
        } catch (SQLException e) { }
    }
}