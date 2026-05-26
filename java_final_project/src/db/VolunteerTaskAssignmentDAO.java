// src/db/VolunteerTaskAssignmentDAO.java
package db;

import model.VolunteerTaskAssignment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VolunteerTaskAssignmentDAO {
    private Connection conn;

    public VolunteerTaskAssignmentDAO(Connection conn) {
        this.conn = conn;
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

    public boolean approveAssignment(int assignmentId) {
        String sql = "UPDATE VolunteerTaskAssignments SET status = 'Approved' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error approving assignment: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectAssignment(int assignmentId) {
        String sql = "UPDATE VolunteerTaskAssignments SET status = 'Rejected' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error rejecting assignment: " + e.getMessage());
            return false;
        }
    }
}