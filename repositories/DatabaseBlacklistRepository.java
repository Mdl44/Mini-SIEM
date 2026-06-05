package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseBlacklistRepository extends AbstractDatabaseRepository<String, String> implements BlacklistRepository {

    private static DatabaseBlacklistRepository instance;

    private DatabaseBlacklistRepository() {
        super();
    }

    public static DatabaseBlacklistRepository getInstance() {
        if (instance == null) {
            instance = new DatabaseBlacklistRepository();
        }
        return instance;
    }

    @Override
    public void save(String ip) {
        String sql = "INSERT INTO blacklisted_ips (ip_address) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.executeUpdate();
            System.out.println("[DB] IP added to blacklist: " + ip);
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to blacklist IP: " + e.getMessage());
        }
    }

    @Override
    public List<String> findAll() {
        List<String> blacklistedIps = new ArrayList<>();
        String sql = "SELECT ip_address FROM blacklisted_ips";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                blacklistedIps.add(rs.getString("ip_address"));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to read blacklist: " + e.getMessage());
        }
        return blacklistedIps;
    }

    @Override
    public Optional<String> findById(String ip) {
        String sql = "SELECT ip_address FROM blacklisted_ips WHERE ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("ip_address"));
                }
            }
        } catch (SQLException e) {}
        return Optional.empty();
    }

    @Override
    public void delete(String ip) {
        String sql = "DELETE FROM blacklisted_ips WHERE ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] IP removed from blacklist: " + ip);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to remove IP: " + e.getMessage());
        }
    }

    @Override
    public void update(String ip) {
    }
}