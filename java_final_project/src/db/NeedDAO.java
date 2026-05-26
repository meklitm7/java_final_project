package db;

import model.Need;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Need> getAllNeeds() {
        List<Need> needs = new ArrayList<>();
        String sql = "SELECT n.id, n.orphanage_id, n.category, n.description, n.status, c.name as orphanage_name " +
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
                    rs.getString("orphanage_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching needs: " + e.getMessage());
        }
        return needs;
    }

    public List<Need> searchNeeds(String query, String category) {
        List<Need> needs = new ArrayList<>();
        String sql = "SELECT n.id, n.orphanage_id, n.category, n.description, n.status, c.name as orphanage_name " +
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
                    rs.getString("orphanage_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error searching needs: " + e.getMessage());
        }
        return needs;
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