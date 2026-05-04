package repositories;

import db.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBlacklistRepository implements BlacklistRepository {

    private Connection connection;

    public DatabaseBlacklistRepository() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    @Override
    public boolean add(String ip) {
        String sql = "INSERT INTO blacklisted_ips (ip_address) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.executeUpdate();
            System.out.println("[DB] IP added to blacklist: " + ip);
            return true;
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to blacklist IP: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAll() {
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
    public boolean remove(String ip) {
        String sql = "DELETE FROM blacklisted_ips WHERE ip_address = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] IP removed from blacklist: " + ip);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to remove IP: " + e.getMessage());
            return false;
        }
    }
}