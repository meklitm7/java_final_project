
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

            userDAO.addUser("Melat", "Donor", "melat@example.com", "1234");
            userDAO.login("melat@example.com", "1234");
            userDAO.listUsers();

            companyDAO.addCompany("Hope Orphanage", "Addis Ababa");
            companyDAO.listCompanies();

            needDAO.addNeed(1, "Food", "Rice and flour needed", "Pending");
            needDAO.listNeeds();
            needDAO.markNeedFulfilled(1);

            donationDAO.addDonation(1, "Item", "Rice", 10, 0.0, 1, "screenshot.png", "Pending");
            donationDAO.listDonations();
            donationDAO.approveDonation(1);
            donationDAO.rejectDonation(1);
            donationDAO.updateScreenshot(1, "updated_screenshot.png");
            donationDAO.getDonorHistory(1);

            volunteerDAO.addVolunteer("Meron", "0912345678", "Pending");
            volunteerDAO.listVolunteers();

            reportGen.generateMonthlyDonationsReport("monthly_donations.csv");
            reportGen.generateApprovedDonationsReport("approved_donations.csv");
            reportGen.generateCompanyActivityReport("company_activity.csv");
        }
    }
}
