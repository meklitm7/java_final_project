package db;

import model.VolunteerTaskAssignment;
import service.NotificationService;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VolunteerTaskAssignmentDAO {
    private Connection conn;
    private NotificationService notificationService;

    public VolunteerTaskAssignmentDAO(Connection conn) {
        this.conn = conn;
        this.notificationService = new NotificationService(conn);
    }

    public boolean assignTask(int volunteerId, int taskId, LocalDate date, int assignedBy) {
        String sql = "INSERT INTO VolunteerTaskAssignments (volunteer_id, task_id, assignment_date, status, assigned_by) " +
                     "VALUES (?, ?, ?, 'Pending', ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, taskId);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setInt(4, assignedBy);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error assigning task: " + e.getMessage());
            return false;
        }
    }

    public List<VolunteerTaskAssignment> getPendingAssignments() {
        List<VolunteerTaskAssignment> assignments = new ArrayList<>();
        String sql = "SELECT a.id, a.volunteer_id, a.task_id, a.assignment_date, a.status, a.assigned_by, " +
                     "v.name as volunteer_name, t.name as task_name, t.category as task_category " +
                     "FROM VolunteerTaskAssignments a " +
                     "JOIN Volunteers v ON a.volunteer_id = v.id " +
                     "JOIN VolunteerTasks t ON a.task_id = t.id " +
                     "WHERE a.status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(new VolunteerTaskAssignment(
                    rs.getInt("id"),
                    rs.getInt("volunteer_id"),
                    rs.getInt("task_id"),
                    rs.getDate("assignment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getInt("assigned_by"),
                    rs.getString("volunteer_name"),
                    rs.getString("task_name"),
                    rs.getString("task_category")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending assignments: " + e.getMessage());
        }
        return assignments;
    }

    // New method to get all assignments
    public List<VolunteerTaskAssignment> getAllAssignments() {
        List<VolunteerTaskAssignment> assignments = new ArrayList<>();
        String sql = "SELECT a.id, a.volunteer_id, a.task_id, a.assignment_date, a.status, a.assigned_by, " +
                     "v.name as volunteer_name, t.name as task_name, t.category as task_category " +
                     "FROM VolunteerTaskAssignments a " +
                     "JOIN Volunteers v ON a.volunteer_id = v.id " +
                     "JOIN VolunteerTasks t ON a.task_id = t.id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(new VolunteerTaskAssignment(
                    rs.getInt("id"),
                    rs.getInt("volunteer_id"),
                    rs.getInt("task_id"),
                    rs.getDate("assignment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getInt("assigned_by"),
                    rs.getString("volunteer_name"),
                    rs.getString("task_name"),
                    rs.getString("task_category")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all assignments: " + e.getMessage());
        }
        return assignments;
    }

    public List<VolunteerTaskAssignment> getAssignmentsByVolunteer(int volunteerId) {
        List<VolunteerTaskAssignment> assignments = new ArrayList<>();
        String sql = "SELECT a.id, a.volunteer_id, a.task_id, a.assignment_date, a.status, a.assigned_by, " +
                     "v.name as volunteer_name, t.name as task_name, t.category as task_category " +
                     "FROM VolunteerTaskAssignments a " +
                     "JOIN Volunteers v ON a.volunteer_id = v.id " +
                     "JOIN VolunteerTasks t ON a.task_id = t.id " +
                     "WHERE a.volunteer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(new VolunteerTaskAssignment(
                    rs.getInt("id"),
                    rs.getInt("volunteer_id"),
                    rs.getInt("task_id"),
                    rs.getDate("assignment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getInt("assigned_by"),
                    rs.getString("volunteer_name"),
                    rs.getString("task_name"),
                    rs.getString("task_category")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching assignments: " + e.getMessage());
        }
        return assignments;
    }

    // Original synchronous method
    public boolean approveAssignment(int assignmentId) {
        String sql = "UPDATE VolunteerTaskAssignments SET status = 'Approved' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                String volunteerQuery = "SELECT volunteer_id FROM VolunteerTaskAssignments WHERE id = ?";
                try (PreparedStatement volunteerStmt = conn.prepareStatement(volunteerQuery)) {
                    volunteerStmt.setInt(1, assignmentId);
                    ResultSet rs = volunteerStmt.executeQuery();
                    if (rs.next()) {
                        int volunteerId = rs.getInt("volunteer_id");
                        notificationService.notifyVolunteerApproved(volunteerId);
                    }
                }
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error approving assignment: " + e.getMessage());
            return false;
        }
    }

    // Background task version
    public void approveAssignmentInBackground(int assignmentId, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                boolean success = approveAssignment(assignmentId);
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
                System.out.println("Error in approveAssignmentInBackground: " + e.getMessage());
                if (onFailure != null) {
                    javafx.application.Platform.runLater(onFailure);
                }
            }
        }).start();
    }

    // Original synchronous method
    public boolean rejectAssignment(int assignmentId, String reason) {
        String sql = "UPDATE VolunteerTaskAssignments SET status = 'Rejected' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            boolean success = stmt.executeUpdate() > 0;

            if (success) {
                String volunteerQuery = "SELECT volunteer_id FROM VolunteerTaskAssignments WHERE id = ?";
                try (PreparedStatement volunteerStmt = conn.prepareStatement(volunteerQuery)) {
                    volunteerStmt.setInt(1, assignmentId);
                    ResultSet rs = volunteerStmt.executeQuery();
                    if (rs.next()) {
                        int volunteerId = rs.getInt("volunteer_id");
                        notificationService.notifyVolunteerRejected(volunteerId, reason);
                    }
                }
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error rejecting assignment: " + e.getMessage());
            return false;
        }
    }

    // Background task version
    public void rejectAssignmentInBackground(int assignmentId, String reason, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                boolean success = rejectAssignment(assignmentId, reason);
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
                System.out.println("Error in rejectAssignmentInBackground: " + e.getMessage());
                if (onFailure != null) {
                    javafx.application.Platform.runLater(onFailure);
                }
            }
        }).start();
    }
}