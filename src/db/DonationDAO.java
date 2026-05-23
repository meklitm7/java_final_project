package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DonationDAO {
    private Connection conn;

    public DonationDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addDonation(int donorId, String type, String itemName, int quantity, double amount, int companyId, String screenshot, String status) {
        String sql = "INSERT INTO Donations (donor_id, type, item_name, quantity, amount, company_id, screenshot, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            stmt.setString(2, type);
            stmt.setString(3, itemName);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, amount);
            stmt.setInt(6, companyId);
            stmt.setString(7, screenshot);
            stmt.setString(8, status);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding donation: " + e.getMessage());
            return false;
        }
    }

    public void listDonations() {
        String sql = "SELECT id, donor_id, type, item_name, quantity, amount, company_id, status FROM Donations";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getInt("donor_id") + " | " +
                    rs.getString("type") + " | " +
                    rs.getString("item_name") + " | " +
                    rs.getInt("quantity") + " | " +
                    rs.getDouble("amount") + " | " +
                    rs.getInt("company_id") + " | " +
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error listing donations: " + e.getMessage());
        }
    }

    public boolean approveDonation(int donationId) {
        String sql = "UPDATE Donations SET status = 'Approved' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error approving donation: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectDonation(int donationId) {
        String sql = "UPDATE Donations SET status = 'Rejected' WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error rejecting donation: " + e.getMessage());
            return false;
        }
    }

    public boolean updateScreenshot(int donationId, String screenshotPath) {
        String sql = "UPDATE Donations SET screenshot = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, screenshotPath);
            stmt.setInt(2, donationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating screenshot: " + e.getMessage());
            return false;
        }
    }

    public void getDonorHistory(int donorId) {
        String sql = "SELECT * FROM Donations WHERE donor_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("type") + " | " +
                    rs.getString("item_name") + " | " +
                    rs.getInt("quantity") + " | " +
                    rs.getDouble("amount") + " | " +
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving donor history: " + e.getMessage());
        }
    }
}
