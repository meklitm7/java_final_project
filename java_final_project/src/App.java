
import db.DBConnection;
import db.UserDAO;
import db.CompanyDAO;
import db.NeedDAO;
import db.DonationDAO;
import db.VolunteerDAO;
import db.ReportGenerator;

public class App {
    public static void main(String[] args) {
        System.out.println("Tesfa Donation System starting...");
        var conn = DBConnection.connect();

        if (conn != null) {
            UserDAO userDAO = new UserDAO(conn);
            CompanyDAO companyDAO = new CompanyDAO(conn);
            NeedDAO needDAO = new NeedDAO(conn);
            DonationDAO donationDAO = new DonationDAO(conn);
            VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
            ReportGenerator reportGen = new ReportGenerator(conn);

            // Test inserts removed to avoid Duplicate entry errors.
            // Run database seed logic separately if needed.

        }
    }
}
