package repositories;
import db.DatabaseConnectionManager;
import models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseIncidentRepository implements IncidentRepository {

    private Connection connection;

    public DatabaseIncidentRepository() {
        this.connection = DatabaseConnectionManager.getInstance().getConnection();
    }

    @Override
    public void save(Incident incident) {
        String insertIncidentSql = "INSERT INTO incidents (source_ip, is_malicious, severity, generation_date) VALUES (?, ?, ?, ?)";
        String insertEventSql = "INSERT INTO security_events (incident_id, system_id, event_time, severity, event_type, network_attacker_ip, network_port, network_protocol, login_username, login_status, login_source_ip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            int realIncidentId = -1;

            try (PreparedStatement incidentStmt = connection.prepareStatement(insertIncidentSql, Statement.RETURN_GENERATED_KEYS)) {
                incidentStmt.setString(1, incident.getSourceIp());
                incidentStmt.setBoolean(2, incident.isMalicious());
                incidentStmt.setString(3, incident.getSeverity().name());
                incidentStmt.setTimestamp(4, Timestamp.valueOf(incident.getGenerationDate()));
                incidentStmt.executeUpdate();

                try (ResultSet generatedKeys = incidentStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        realIncidentId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating incident failed, no ID obtained.");
                    }
                }
            }

            if (incident.getEvents() != null && !incident.getEvents().isEmpty()) {
                try (PreparedStatement eventStmt = connection.prepareStatement(insertEventSql)) {
                    for (SecurityEvent event : incident.getEvents()) {

                        eventStmt.setInt(1, realIncidentId);
                        eventStmt.setInt(2, event.getSourceSystem().getId());
                        eventStmt.setTimestamp(3, Timestamp.valueOf(event.getTimestamp()));
                        eventStmt.setString(4, event.getSeverity().name());
                        eventStmt.setString(5, event.getEventType());

                        eventStmt.setNull(6, Types.VARCHAR);
                        eventStmt.setNull(7, Types.INTEGER);
                        eventStmt.setNull(8, Types.VARCHAR);
                        eventStmt.setNull(9, Types.VARCHAR);
                        eventStmt.setNull(10, Types.VARCHAR);
                        eventStmt.setNull(11, Types.VARCHAR);

                        if (event instanceof NetworkEvent net) {
                            eventStmt.setString(6, net.getAttackerIP());
                            eventStmt.setInt(7, net.getPort());
                            eventStmt.setString(8, net.getProtocol());
                        } else if (event instanceof LoginEvent log) {
                            eventStmt.setString(9, log.getUsername());
                            eventStmt.setString(10, log.getStatus());
                            eventStmt.setString(11, log.getSourceIP());
                        }

                        eventStmt.executeUpdate();
                    }
                }
            }

            connection.commit();
            System.out.println("[DB] Dossier saved successfully with  ID: #" + realIncidentId + " (" + incident.getEvents().size() + " events attached)");

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("[ERROR] Failed to save dossier: " + e.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Optional<Incident> findById(int id) {
        String sql = "SELECT * FROM incidents WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(buildIncidentFromDB(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to find dossier: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Incident> findAll() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                incidents.add(buildIncidentFromDB(rs));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to load dossiers: " + e.getMessage());
        }
        return incidents;
    }

    @Override
    public void update(Incident incident) {
        String sql = "UPDATE incidents SET source_ip = ?, is_malicious = ?, severity = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, incident.getSourceIp());
            pstmt.setBoolean(2, incident.isMalicious());
            pstmt.setString(3, incident.getSeverity().name());
            pstmt.setInt(4, incident.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] Dossier updated successfully: #" + incident.getId());
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to update dossier: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM incidents WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[DB] Dossier and its evidence deleted successfully: #" + id);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to delete dossier: " + e.getMessage());
        }
    }

    private Incident buildIncidentFromDB(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String sourceIp = rs.getString("source_ip");
        boolean isMalicious = rs.getBoolean("is_malicious");
        Severity severity = Severity.valueOf(rs.getString("severity"));
        List<SecurityEvent> events = fetchEventsForIncident(id);

        return new Incident(id, sourceIp, events, isMalicious, severity);
    }

    private List<SecurityEvent> fetchEventsForIncident(int incidentId) {
        List<SecurityEvent> events = new ArrayList<>();

        String sql = "SELECT e.*, s.system_name, s.ip_address AS sys_ip, s.os_type, s.criticality_level " +
                "FROM security_events e " +
                "JOIN monitored_systems s ON e.system_id = s.id " +
                "WHERE e.incident_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, incidentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDateTime time = rs.getTimestamp("event_time").toLocalDateTime();
                    Severity severity = Severity.valueOf(rs.getString("severity"));
                    String type = rs.getString("event_type");

                    MonitoredSystem system = new MonitoredSystem(
                            rs.getInt("system_id"),
                            rs.getString("system_name"),
                            rs.getString("sys_ip"),
                            rs.getString("os_type"),
                            Severity.valueOf(rs.getString("criticality_level"))
                    );

                    if ("NETWORK".equals(type)) {
                        events.add(new NetworkEvent(
                                id, time, system, severity,
                                rs.getString("network_attacker_ip"),
                                rs.getInt("network_port"),
                                rs.getString("network_protocol")
                        ));
                    } else if ("LOGIN".equals(type)) {
                        events.add(new LoginEvent(
                                id, time, system, severity,
                                rs.getString("login_username"),
                                rs.getString("login_status"),
                                rs.getString("login_source_ip")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to read events for dossier #" + incidentId + ": " + e.getMessage());
        }
        return events;
    }
}