package db;

import model.Need;
import service.NotificationService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NeedDAO {
    private Connection conn;
    private NotificationService notificationService;

    public NeedDAO(Connection conn) {
        this.conn = conn;
        this.notificationService = new NotificationService(conn);
    }

    // Original synchronous method
    public boolean addNeed(int orphanageId, String category, String description, String status) {
        String sql = "INSERT INTO Needs (orphanage_id, category, description, status, quantity) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orphanageId);
            stmt.setString(2, category);
            stmt.setString(3, description);
            stmt.setString(4, status);
            boolean success = stmt.executeUpdate() > 0;

            // Notify admins and donors about the new need
            if (success) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int needId = rs.getInt(1);
                    // Notify all admins
                    String adminQuery = "SELECT id FROM Users WHERE role = 'Admin'";
                    try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                        ResultSet adminRs = adminStmt.executeQuery();
                        while (adminRs.next()) {
                            int adminId = adminRs.getInt("id");
                            notificationService.notifyNewNeed(adminId, needId, category + ": " + description);
                        }
                    }
                    // Notify all donors
                    String donorQuery = "SELECT id FROM Users WHERE role = 'Donor'";
                    try (PreparedStatement donorStmt = conn.prepareStatement(donorQuery)) {
                        ResultSet donorRs = donorStmt.executeQuery();
                        while (donorRs.next()) {
                            int donorId = donorRs.getInt("id");
                            notificationService.notifyNewNeed(donorId, needId, category + ": " + description);
                        }
                    }
                }
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error adding need: " + e.getMessage());
            return false;
        }
    }

    // Background task version
    public void addNeedInBackground(int orphanageId, String category, String description, String status,
                                   Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                boolean success = addNeed(orphanageId, category, description, status);
                if (success) {
                    if (onSuccess != null) {
                        javafx.application.Platform.runLater(onSuccess);
                    }
                } else {
                    if (onFailure != null) {
                        javafx.application.Platform.runLater(onFailure);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in addNeedInBackground: " + e.getMessage());
                if (onFailure != null) {
                    javafx.application.Platform.runLater(onFailure);
                }
            }
        }).start();
    }

    public List<Need> getAllNeeds() {
        List<Need> needs = new ArrayList<>();
        String sql = "SELECT n.id, n.orphanage_id, n.category, n.description, n.status, c.name as orphanage_name, n.quantity " +
                     "FROM Needs n JOIN Companies c ON n.orphanage_id = c.id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                needs.add(new Need(
                    rs.getInt("id"),
                    rs.getInt("orphanage_id"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("orphanage_name"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching needs: " + e.getMessage());
        }
        return needs;
    }

    // Corrected searchNeeds method
    public List<Need> searchNeeds(String query, String category) {
        List<Need> needs = new ArrayList<>();
        String sql = "SELECT n.id, n.orphanage_id, n.category, n.description, n.status, c.name as orphanage_name, n.quantity " +
                     "FROM Needs n JOIN Companies c ON n.orphanage_id = c.id " +
                     "WHERE (n.description LIKE ? OR n.category LIKE ?) " +
                     "AND (? = 'All' OR n.category = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, category);
            stmt.setString(4, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                needs.add(new Need(
                    rs.getInt("id"),
                    rs.getInt("orphanage_id"),
                    rs.getString("category"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("orphanage_name"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error searching needs: " + e.getMessage());
        }
        return needs;
    }

    // Original synchronous method
    public boolean markNeedFulfilled(int needId) {
        String sql = "UPDATE Needs SET status = 'Fulfilled' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, needId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marking need fulfilled: " + e.getMessage());
            return false;
        }
    }

    // Background task version
    public void markNeedFulfilledInBackground(int needId, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                boolean success = markNeedFulfilled(needId);
                if (success) {
                    if (onSuccess != null) {
                        javafx.application.Platform.runLater(onSuccess);
                    }
                } else {
                    if (onFailure != null) {
                        javafx.application.Platform.runLater(onFailure);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in markNeedFulfilledInBackground: " + e.getMessage());
                if (onFailure != null) {
                    javafx.application.Platform.runLater(onFailure);
                }
            }
        }).start();
    }
}