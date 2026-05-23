import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDAO {
    private Connection conn;

    public CompanyDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addCompany(String name, String location) {
        String sql = "INSERT INTO Companies (name, location) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, location);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding company: " + e.getMessage());
            return false;
        }
    }

    public void listCompanies() {
        String sql = "SELECT id, name, location FROM Companies";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("location")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error listing companies: " + e.getMessage());
        }
    }
}
