package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NeedDAO {
    private Connection conn;

    public NeedDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addNeed(int orphanageId, String category, String description, String status) {
        String sql = "INSERT INTO Needs (orphanage_id, category, description, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orphanageId);
            stmt.setString(2, category);
            stmt.setString(3, description);
            stmt.setString(4, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding need: " + e.getMessage());
            return false;
        }
    }

    public void listNeeds() {
        String sql = "SELECT id, orphanage_id, category, description, status FROM Needs";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getInt("orphanage_id") + " | " +
                    rs.getString("category") + " | " +
                    rs.getString("description") + " | " +
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error listing needs: " + e.getMessage());
        }
    }

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
}
