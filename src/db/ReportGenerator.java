package db;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportGenerator {
    private Connection conn;

    public ReportGenerator(Connection conn) {
        this.conn = conn;
    }

    public void generateMonthlyDonationsReport(String filePath) {
        String sql = "SELECT id, donor_id, type, item_name, quantity, amount, company_id, status FROM Donations WHERE MONTH(CURDATE()) = MONTH(NOW())";
        writeReport(sql, filePath);
    }

    public void generateApprovedDonationsReport(String filePath) {
        String sql = "SELECT id, donor_id, type, item_name, quantity, amount, company_id, status FROM Donations WHERE status = 'Approved'";
        writeReport(sql, filePath);
    }

    public void generateCompanyActivityReport(String filePath) {
        String sql = "SELECT id, name, location FROM Companies";
        writeReport(sql, filePath);
    }

    private void writeReport(String sql, String filePath) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             FileWriter writer = new FileWriter(filePath)) {

            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    row.append(rs.getString(i));
                    if (i < columnCount) row.append(",");
                }
                writer.write(row.toString() + "\n");
            }
            System.out.println("Report generated: " + filePath);
        } catch (IOException | java.sql.SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}
