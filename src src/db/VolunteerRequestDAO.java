package db;

import model.VolunteerRequest;
import service.NotificationService;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VolunteerRequestDAO {
    private Connection conn;
    private NotificationService notificationService;

    public VolunteerRequestDAO(Connection conn) {
        this.conn = conn;
        this.notificationService = new NotificationService(conn);
    }

    // Add a new volunteer request
    public boolean addRequest(int volunteerId, String requestType, String title, String message) {
        String sql = "INSERT INTO VolunteerRequests (volunteer_id, request_type, title, message, status) " +
                     "VALUES (?, ?, ?, ?, 'Pending')";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, volunteerId);
            stmt.setString(2, requestType);
            stmt.setString(3, title);
            stmt.setString(4, message);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Notify admins about the new request
                notifyAdminsAboutNewRequest(volunteerId, requestType, title);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error adding volunteer request: " + e.getMessage());
            return false;
        }
    }

    // Helper method to notify admins about new requests
    private void notifyAdminsAboutNewRequest(int volunteerId, String requestType, String title) {
        try {
            // Get volunteer name
            String volunteerName = "Unknown";
            String sql = "SELECT name FROM Volunteers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, volunteerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    volunteerName = rs.getString("name");
                }
            }

            // Notify all admins
            String adminQuery = "SELECT id FROM Users WHERE role = 'Admin'";
            try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                ResultSet adminRs = adminStmt.executeQuery();
                while (adminRs.next()) {
                    int adminId = adminRs.getInt("id");
                    String message = "New request from volunteer " + volunteerName + ": " +
                                   requestType + " - " + title;
                    notificationService.createNotification(adminId, message, "NEW_VOLUNTEER_REQUEST");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error notifying admins about new request: " + e.getMessage());
        }
    }

    // Other methods (getRequestsByVolunteer, getPendingRequests, etc.) can be added here
    // ...
}