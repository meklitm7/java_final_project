package db;

import model.Donation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {
    private Connection conn;

    public DonationDAO(Connection conn) {
        this.conn = conn;
    }

    // ===== ORIGINAL METHOD (keeps existing code working) =====
    public boolean addDonation(int donorId, String type, String itemName, int quantity,
            double amount, int companyId, String screenshot, String status) {
        String sql = "INSERT INTO Donations (donor_id, type, item_name, quantity, amount, company_id, screenshot, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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

    // ===== NEW METHOD (for DonationForm.java) =====
    public boolean addDonation(Donation donation) {
        return addDonation(
                donation.getDonorId(),
                donation.getType(),
                donation.getItemName(),
                donation.getQuantity(),
                donation.getAmount(),
                donation.getCompanyId(),
                donation.getScreenshot(),
                donation.getStatus());
    }

    public List<Donation> getPendingDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM Donations WHERE status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                donations.add(new Donation(
                        rs.getInt("id"),
                        rs.getInt("donor_id"),
                        rs.getString("type"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("amount"),
                        rs.getInt("company_id"),
                        rs.getString("screenshot"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending donations: " + e.getMessage());
        }
        return donations;
    }

    public List<Donation> getDonorHistory(int donorId) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM Donations WHERE donor_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                donations.add(new Donation(
                        rs.getInt("id"),
                        rs.getInt("donor_id"),
                        rs.getString("type"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("amount"),
                        rs.getInt("company_id"),
                        rs.getString("screenshot"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching donor history: " + e.getMessage());
        }
        return donations;
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
}