package repositories;

import db.DatabaseConnectionManager;
import models.Achievement;
import models.SOCAnalyst;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAnalystRepository implements AnalystRepository {

    private final Connection connection;

    public DatabaseAnalystRepository() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    @Override
    public void save(SOCAnalyst analyst) {
        String sql = "INSERT INTO users (username, email, password, rank_level, days_survived, max_hp, current_hp, max_senior_calls, current_senior_calls, soc_credits) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, analyst.getName());
            pstmt.setString(2, analyst.getEmail());
            pstmt.setString(3, analyst.getPassword());
            pstmt.setInt(4, analyst.getRankLevel());
            pstmt.setInt(5, analyst.getDaysSurvived());
            pstmt.setInt(6, analyst.getMaxLives());
            pstmt.setInt(7, analyst.getCurrentLives());
            pstmt.setInt(8, analyst.getMaxSeniorCalls());
            pstmt.setInt(9, analyst.getCurrentSpecialHelps());
            pstmt.setInt(10, analyst.getCredits());

            pstmt.executeUpdate();
            System.out.println("[DB] Analyst saved successfully: " + analyst.getName());
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to save analyst: " + e.getMessage());
        }
    }

    @Override
    public List<SOCAnalyst> findAll() {
        List<SOCAnalyst> analysts = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int rank = rs.getInt("rank_level");
                int daysSurvived = rs.getInt("days_survived");
                int maxLives = rs.getInt("max_hp");
                int currentLives = rs.getInt("current_hp");
                int maxSeniorCalls = rs.getInt("max_senior_calls");
                int currentSeniorCalls = rs.getInt("current_senior_calls");
                int credits = rs.getInt("soc_credits");

                SOCAnalyst analyst = new SOCAnalyst(id, name, email, password, rank, daysSurvived, maxLives, currentLives, maxSeniorCalls, currentSeniorCalls, credits);
                analyst.setAchievements(loadAchievements(analyst.getId()));
                analysts.add(analyst);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to read analysts: " + e.getMessage());
        }
        return analysts;
    }

    @Override
    public Optional<SOCAnalyst> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    int rank = rs.getInt("rank_level");
                    int daysSurvived = rs.getInt("days_survived");
                    int maxLives = rs.getInt("max_hp");
                    int currentLives = rs.getInt("current_hp");
                    int maxSeniorCalls = rs.getInt("max_senior_calls");
                    int currentSeniorCalls = rs.getInt("current_senior_calls");
                    int credits = rs.getInt("soc_credits");

                    SOCAnalyst analyst = new SOCAnalyst(id, name, email, password, rank, daysSurvived, maxLives, currentLives, maxSeniorCalls, currentSeniorCalls, credits);
                    analyst.setAchievements(loadAchievements(analyst.getId()));
                    return Optional.of(analyst);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to find analyst: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void update(SOCAnalyst analyst) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, rank_level = ?, days_survived = ?, max_hp = ?, current_hp = ?, max_senior_calls = ?, current_senior_calls = ?, soc_credits = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, analyst.getName());
            pstmt.setString(2, analyst.getEmail());
            pstmt.setString(3, analyst.getPassword());
            pstmt.setInt(4, analyst.getRankLevel());
            pstmt.setInt(5, analyst.getDaysSurvived());
            pstmt.setInt(6, analyst.getMaxLives());
            pstmt.setInt(7, analyst.getCurrentLives());
            pstmt.setInt(8, analyst.getMaxSeniorCalls());
            pstmt.setInt(9, analyst.getCurrentSpecialHelps());
            pstmt.setInt(10, analyst.getCredits());
            pstmt.setInt(11, analyst.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] Analyst updated successfully: " + analyst.getName());
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update analyst: " + e.getMessage());
        }
    }

    @Override
    public void updateAll(List<SOCAnalyst> analysts) {
        for (SOCAnalyst analyst : analysts) {
            update(analyst);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] Analyst with ID #" + id + " deleted successfully.");
            } else {
                System.out.println("[DB] No analyst found with ID #" + id);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to delete analyst: " + e.getMessage());
        }
    }

    @Override
    public Achievement saveAchievement(int userId, String achievementName) {
        String findIdSql = "SELECT id, name, description, bonus_credits FROM achievements WHERE name = ?";
        String linkSql = "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";
        String updateCreditsSql = "UPDATE users SET soc_credits = soc_credits + ? WHERE id = ?";

        Achievement newAchievement = null;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psFind = connection.prepareStatement(findIdSql)) {
                psFind.setString(1, achievementName);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    int achievementId = rs.getInt("id");
                    int bonus = rs.getInt("bonus_credits");

                    newAchievement = new Achievement(
                            achievementId,
                            rs.getString("name"),
                            rs.getString("description"),
                            bonus
                    );
                    try (PreparedStatement psLink = connection.prepareStatement(linkSql)) {
                        psLink.setInt(1, userId);
                        psLink.setInt(2, achievementId);
                        psLink.executeUpdate();
                    }

                    if (bonus > 0) {
                        try (PreparedStatement psCredits = connection.prepareStatement(updateCreditsSql)) {
                            psCredits.setInt(1, bonus);
                            psCredits.setInt(2, userId);
                            psCredits.executeUpdate();
                        }
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("[WARNING] Achievement already unlocked or error: " + e.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return newAchievement;
    }

    private List<Achievement> loadAchievements(int userId) {
        List<Achievement> medals = new ArrayList<>();
        String sql = "SELECT a.* FROM achievements a " +
                "JOIN user_achievements ua ON a.id = ua.achievement_id " +
                "WHERE ua.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medals.add(new Achievement(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("bonus_credits")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load achievements: " + e.getMessage());
        }
        return medals;
    }
}