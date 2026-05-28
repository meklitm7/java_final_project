// src/db/VolunteerTaskDAO.java
package db;

import model.VolunteerTask;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerTaskDAO {
    private Connection conn;

    public VolunteerTaskDAO(Connection conn) {
        this.conn = conn;
    }

    public List<VolunteerTask> getAllTasks() {
        List<VolunteerTask> tasks = new ArrayList<>();
        String sql = "SELECT id, name, category, description FROM VolunteerTasks";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new VolunteerTask(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching tasks: " + e.getMessage());
        }
        return tasks;
    }

    public List<VolunteerTask> getTasksByCategory(String category) {
        List<VolunteerTask> tasks = new ArrayList<>();
        String sql = "SELECT id, name, category, description FROM VolunteerTasks WHERE category = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new VolunteerTask(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching tasks by category: " + e.getMessage());
        }
        return tasks;
    }

    public boolean addTask(String name, String category, String description) {
        String sql = "INSERT INTO VolunteerTasks (name, category, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM VolunteerTasks WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }
}