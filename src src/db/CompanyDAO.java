package db;

import model.Company;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    private Connection conn;

    public CompanyDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addCompany(String name, String location, String accountNumber,
            String telebirrPhone, String adminTelegram, int adminId) {
        String sql = "INSERT INTO Companies (name, location, account_number, telebirr_phone, admin_telegram, admin_id) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, location);
            stmt.setString(3, accountNumber);
            stmt.setString(4, telebirrPhone);
            stmt.setString(5, adminTelegram);
            stmt.setInt(6, adminId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding company: " + e.getMessage());
            return false;
        }
    }

    public List<Company> listCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT id, name, location, account_number, telebirr_phone, admin_telegram, admin_id FROM Companies";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Use admin's name as owner name
                String adminName = getAdminName(rs.getInt("admin_id"));
                companies.add(new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("account_number"),
                        adminName, // accountOwnerName = admin name
                        rs.getString("telebirr_phone"),
                        adminName, // telebirrOwnerName = admin name
                        rs.getString("admin_telegram"),
                        rs.getInt("admin_id")));
            }
        } catch (SQLException e) {
            System.out.println("Error listing companies: " + e.getMessage());
        }
        return companies;
    }

    public Company getCompanyByName(String name) {
        String sql = "SELECT id, name, location, account_number, telebirr_phone, admin_telegram, admin_id " +
                "FROM Companies WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String adminName = getAdminName(rs.getInt("admin_id"));
                return new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("account_number"),
                        adminName,
                        rs.getString("telebirr_phone"),
                        adminName,
                        rs.getString("admin_telegram"),
                        rs.getInt("admin_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching company: " + e.getMessage());
        }
        return null;
    }

    public Company getCompanyById(int id) {
        String sql = "SELECT id, name, location, account_number, telebirr_phone, admin_telegram, admin_id " +
                "FROM Companies WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String adminName = getAdminName(rs.getInt("admin_id"));
                return new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("account_number"),
                        adminName,
                        rs.getString("telebirr_phone"),
                        adminName,
                        rs.getString("admin_telegram"),
                        rs.getInt("admin_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching company by ID: " + e.getMessage());
        }
        return null;
    }

    // Helper method to get admin name
    private String getAdminName(int adminId) {
        String sql = "SELECT name FROM Users WHERE id = ? AND role = 'Admin'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin name: " + e.getMessage());
        }
        return "Unknown Admin";
    }
}