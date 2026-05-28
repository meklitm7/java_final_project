package rmi;

import db.*;
import model.*;
import service.NotificationService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TesfaRMIServer extends UnicastRemoteObject implements TesfaRMI {
    private Connection conn;
    private NotificationService notificationService;

    public TesfaRMIServer() throws RemoteException {
        super();
        try {
            // Connect to the database
            this.conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tesfa",
                "root",
                "Meron123@"
            );
            this.notificationService = new NotificationService(conn);
            System.out.println("RMI Server: Database connection established.");
        } catch (SQLException e) {
            System.err.println("RMI Server: Database connection failed: " + e.getMessage());
            throw new RemoteException("Database connection failed", e);
        }
    }

    // User operations
    @Override
    public User login(String email, String password) throws RemoteException {
        try {
            UserDAO userDAO = new UserDAO(conn);
            User user = userDAO.getUserByEmail(email);
            if (user != null && userDAO.login(email, password)) {
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("RMI login error: " + e.getMessage());
            throw new RemoteException("Login failed", e);
        }
    }

    @Override
    public boolean registerUser(String name, String role, String email, String password, String telegramUsername) throws RemoteException {
        try {
            UserDAO userDAO = new UserDAO(conn);
            return userDAO.addUser(name, role, email, password, telegramUsername);
        } catch (Exception e) {
            System.err.println("RMI register error: " + e.getMessage());
            throw new RemoteException("Registration failed", e);
        }
    }

    @Override
    public List<User> getUsersByRole(String role) throws RemoteException {
        try {
            UserDAO userDAO = new UserDAO(conn);
            return userDAO.getUsersByRole(role);
        } catch (Exception e) {
            System.err.println("RMI getUsersByRole error: " + e.getMessage());
            throw new RemoteException("Failed to get users by role", e);
        }
    }

    // Donation operations
    @Override
    public boolean addDonation(int donorId, String type, String itemName, int quantity,
                              double amount, int companyId, String screenshot, String status) throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            return donationDAO.addDonation(donorId, type, itemName, quantity, amount, companyId, screenshot, status);
        } catch (Exception e) {
            System.err.println("RMI addDonation error: " + e.getMessage());
            throw new RemoteException("Failed to add donation", e);
        }
    }

    @Override
    public List<Donation> getPendingDonations() throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            return donationDAO.getPendingDonations();
        } catch (Exception e) {
            System.err.println("RMI getPendingDonations error: " + e.getMessage());
            throw new RemoteException("Failed to get pending donations", e);
        }
    }

    @Override
    public List<Donation> getDonorHistory(int donorId) throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            return donationDAO.getDonorHistory(donorId);
        } catch (Exception e) {
            System.err.println("RMI getDonorHistory error: " + e.getMessage());
            throw new RemoteException("Failed to get donor history", e);
        }
    }

    @Override
    public boolean approveDonation(int donationId) throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            return donationDAO.approveDonation(donationId);
        } catch (Exception e) {
            System.err.println("RMI approveDonation error: " + e.getMessage());
            throw new RemoteException("Failed to approve donation", e);
        }
    }

    @Override
    public boolean rejectDonation(int donationId, String reason) throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            return donationDAO.rejectDonation(donationId, reason);
        } catch (Exception e) {
            System.err.println("RMI rejectDonation error: " + e.getMessage());
            throw new RemoteException("Failed to reject donation", e);
        }
    }

    // Need operations
    @Override
    public boolean addNeed(int orphanageId, String category, String description, String status) throws RemoteException {
        try {
            NeedDAO needDAO = new NeedDAO(conn);
            return needDAO.addNeed(orphanageId, category, description, status);
        } catch (Exception e) {
            System.err.println("RMI addNeed error: " + e.getMessage());
            throw new RemoteException("Failed to add need", e);
        }
    }

    @Override
    public List<Need> getAllNeeds() throws RemoteException {
        try {
            NeedDAO needDAO = new NeedDAO(conn);
            return needDAO.getAllNeeds();
        } catch (Exception e) {
            System.err.println("RMI getAllNeeds error: " + e.getMessage());
            throw new RemoteException("Failed to get all needs", e);
        }
    }

    @Override
    public boolean markNeedFulfilled(int needId) throws RemoteException {
        try {
            NeedDAO needDAO = new NeedDAO(conn);
            return needDAO.markNeedFulfilled(needId);
        } catch (Exception e) {
            System.err.println("RMI markNeedFulfilled error: " + e.getMessage());
            throw new RemoteException("Failed to mark need as fulfilled", e);
        }
    }

    // Company operations
    @Override
    public List<Company> getAllCompanies() throws RemoteException {
        try {
            CompanyDAO companyDAO = new CompanyDAO(conn);
            return companyDAO.listCompanies();
        } catch (Exception e) {
            System.err.println("RMI getAllCompanies error: " + e.getMessage());
            throw new RemoteException("Failed to get all companies", e);
        }
    }

    @Override
    public boolean addCompany(String name, String location, String accountNumber,
                              String telebirrPhone, String adminTelegram, int adminId) throws RemoteException {
        try {
            CompanyDAO companyDAO = new CompanyDAO(conn);
            return companyDAO.addCompany(name, location, accountNumber, telebirrPhone, adminTelegram, adminId);
        } catch (Exception e) {
            System.err.println("RMI addCompany error: " + e.getMessage());
            throw new RemoteException("Failed to add company", e);
        }
    }

    // Volunteer operations
    @Override
    public List<Volunteer> getAllVolunteers() throws RemoteException {
        try {
            VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
            return volunteerDAO.getAllVolunteers();
        } catch (Exception e) {
            System.err.println("RMI getAllVolunteers error: " + e.getMessage());
            throw new RemoteException("Failed to get all volunteers", e);
        }
    }
    

    @Override
    public boolean addVolunteer(String name, String contact, String status) throws RemoteException {
        try {
            VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
            return volunteerDAO.addVolunteer(name, contact, status);
        } catch (Exception e) {
            System.err.println("RMI addVolunteer error: " + e.getMessage());
            throw new RemoteException("Failed to add volunteer", e);
        }
    }

    // Volunteer Task operations
    @Override
    public List<VolunteerTask> getAllVolunteerTasks() throws RemoteException {
        try {
            VolunteerTaskDAO taskDAO = new VolunteerTaskDAO(conn);
            return taskDAO.getAllTasks();
        } catch (Exception e) {
            System.err.println("RMI getAllVolunteerTasks error: " + e.getMessage());
            throw new RemoteException("Failed to get all volunteer tasks", e);
        }
    }

    @Override
    public boolean addVolunteerTask(String name, String category, String description) throws RemoteException {
        try {
            VolunteerTaskDAO taskDAO = new VolunteerTaskDAO(conn);
            return taskDAO.addTask(name, category, description);
        } catch (Exception e) {
            System.err.println("RMI addVolunteerTask error: " + e.getMessage());
            throw new RemoteException("Failed to add volunteer task", e);
        }
    }

    @Override
    public boolean deleteVolunteerTask(int taskId) throws RemoteException {
        try {
            VolunteerTaskDAO taskDAO = new VolunteerTaskDAO(conn);
            return taskDAO.deleteTask(taskId);
        } catch (Exception e) {
            System.err.println("RMI deleteVolunteerTask error: " + e.getMessage());
            throw new RemoteException("Failed to delete volunteer task", e);
        }
    }

    // Volunteer Task Assignment operations
    @Override
    public boolean assignTask(int volunteerId, int taskId, LocalDate date, int assignedBy) throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            return assignmentDAO.assignTask(volunteerId, taskId, date, assignedBy);
        } catch (Exception e) {
            System.err.println("RMI assignTask error: " + e.getMessage());
            throw new RemoteException("Failed to assign task", e);
        }
    }

    @Override
    public List<VolunteerTaskAssignment> getPendingTaskAssignments() throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            return assignmentDAO.getPendingAssignments();
        } catch (Exception e) {
            System.err.println("RMI getPendingTaskAssignments error: " + e.getMessage());
            throw new RemoteException("Failed to get pending task assignments", e);
        }
    }

    @Override
    public List<VolunteerTaskAssignment> getTaskAssignmentsByVolunteer(int volunteerId) throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            return assignmentDAO.getAssignmentsByVolunteer(volunteerId);
        } catch (Exception e) {
            System.err.println("RMI getTaskAssignmentsByVolunteer error: " + e.getMessage());
            throw new RemoteException("Failed to get task assignments by volunteer", e);
        }
    }

    @Override
    public boolean approveTaskAssignment(int assignmentId) throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            return assignmentDAO.approveAssignment(assignmentId);
        } catch (Exception e) {
            System.err.println("RMI approveTaskAssignment error: " + e.getMessage());
            throw new RemoteException("Failed to approve task assignment", e);
        }
    }

    @Override
    public boolean rejectTaskAssignment(int assignmentId, String reason) throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            return assignmentDAO.rejectAssignment(assignmentId, reason);
        } catch (Exception e) {
            System.err.println("RMI rejectTaskAssignment error: " + e.getMessage());
            throw new RemoteException("Failed to reject task assignment", e);
        }
    }

    // Notification operations
    @Override
    public List<Notification> getNotifications(int userId) throws RemoteException {
        try {
            NotificationDAO notificationDAO = new NotificationDAO(conn);
            return notificationDAO.getNotificationsByUser(userId);
        } catch (Exception e) {
            System.err.println("RMI getNotifications error: " + e.getMessage());
            throw new RemoteException("Failed to get notifications", e);
        }
    }

    @Override
    public boolean markNotificationRead(int notificationId) throws RemoteException {
        try {
            NotificationDAO notificationDAO = new NotificationDAO(conn);
            return notificationDAO.markAsRead(notificationId);
        } catch (Exception e) {
            System.err.println("RMI markNotificationRead error: " + e.getMessage());
            throw new RemoteException("Failed to mark notification as read", e);
        }
    }

    // Branch synchronization operations
    @Override
    public boolean syncNeedToBranch(Need need) throws RemoteException {
        try {
            NeedDAO needDAO = new NeedDAO(conn);
            // Check if need already exists (based on ID or unique fields)
            // If not, add it to the database
            // This is a simplified version - you might need more sophisticated conflict resolution
            return needDAO.addNeed(need.getOrphanageId(), need.getCategory(), need.getDescription(), need.getStatus());
        } catch (Exception e) {
            System.err.println("RMI syncNeedToBranch error: " + e.getMessage());
            throw new RemoteException("Failed to sync need to branch", e);
        }
    }

    @Override
    public boolean syncDonationToBranch(Donation donation) throws RemoteException {
        try {
            DonationDAO donationDAO = new DonationDAO(conn);
            // Check if donation already exists
            // If not, add it to the database
            return donationDAO.addDonation(donation);
        } catch (Exception e) {
            System.err.println("RMI syncDonationToBranch error: " + e.getMessage());
            throw new RemoteException("Failed to sync donation to branch", e);
        }
    }

    @Override
    public boolean syncVolunteerToBranch(Volunteer volunteer) throws RemoteException {
        try {
            VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
            // Check if volunteer already exists
            // If not, add it to the database
            return volunteerDAO.addVolunteer(volunteer.getName(), volunteer.getContact(), volunteer.getStatus());
        } catch (Exception e) {
            System.err.println("RMI syncVolunteerToBranch error: " + e.getMessage());
            throw new RemoteException("Failed to sync volunteer to branch", e);
        }
    }

    @Override
    public boolean syncVolunteerTaskToBranch(VolunteerTask task) throws RemoteException {
        try {
            VolunteerTaskDAO taskDAO = new VolunteerTaskDAO(conn);
            // Check if task already exists
            // If not, add it to the database
            return taskDAO.addTask(task.getName(), task.getCategory(), task.getDescription());
        } catch (Exception e) {
            System.err.println("RMI syncVolunteerTaskToBranch error: " + e.getMessage());
            throw new RemoteException("Failed to sync volunteer task to branch", e);
        }
    }

    @Override
    public boolean syncTaskAssignmentToBranch(VolunteerTaskAssignment assignment) throws RemoteException {
        try {
            VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
            // Check if assignment already exists
            // If not, add it to the database
            return assignmentDAO.assignTask(
                assignment.getVolunteerId(),
                assignment.getTaskId(),
                assignment.getAssignmentDate(),
                assignment.getAssignedBy()
            );
        } catch (Exception e) {
            System.err.println("RMI syncTaskAssignmentToBranch error: " + e.getMessage());
            throw new RemoteException("Failed to sync task assignment to branch", e);
        }
    }
}