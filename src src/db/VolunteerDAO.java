package db;

import model.Volunteer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerDAO {
    private Connection conn;

    public VolunteerDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addVolunteer(String name, String contact, String status) {
        String sql = "INSERT INTO Volunteers (name, contact, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.setString(3, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding volunteer: " + e.getMessage());
            return false;
        }
    }

    public List<Volunteer> getAllVolunteers() {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM Volunteers";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                volunteers.add(new Volunteer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching volunteers: " + e.getMessage());
        }
        return volunteers;
    }

    public String getVolunteerStatus(int volunteerId) {
        String sql = "SELECT status FROM Volunteers WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching volunteer status: " + e.getMessage());
        }
        return "Unknown";
    }
}