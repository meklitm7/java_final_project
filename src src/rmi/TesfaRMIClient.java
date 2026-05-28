package rmi;

import model.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class TesfaRMIClient {
    private static TesfaRMI rmiServer;
    private static boolean connected = false;

    public static void connect(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            rmiServer = (TesfaRMI) registry.lookup("TesfaRMI");
            connected = true;
            System.out.println("Connected to Tesfa RMI Server");
        } catch (Exception e) {
            System.err.println("RMI Client exception: " + e.toString());
            e.printStackTrace();
            connected = false;
        }
    }

    public static boolean isConnected() {
        return connected && rmiServer != null;
    }

    public static void disconnect() {
        rmiServer = null;
        connected = false;
        System.out.println("Disconnected from Tesfa RMI Server");
    }

    // User operations
    public static User login(String email, String password) {
        try {
            if (isConnected()) {
                return rmiServer.login(email, password);
            }
            return null;
        } catch (Exception e) {
            System.err.println("RMI login error: " + e.getMessage());
            return null;
        }
    }

    public static boolean registerUser(String name, String role, String email, String password, String telegramUsername) {
        try {
            if (isConnected()) {
                return rmiServer.registerUser(name, role, email, password, telegramUsername);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI register error: " + e.getMessage());
            return false;
        }
    }

    public static List<User> getUsersByRole(String role) {
        try {
            if (isConnected()) {
                return rmiServer.getUsersByRole(role);
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getUsersByRole error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // Donation operations
    public static boolean addDonation(int donorId, String type, String itemName, int quantity,
                                      double amount, int companyId, String screenshot, String status) {
        try {
            if (isConnected()) {
                return rmiServer.addDonation(donorId, type, itemName, quantity, amount, companyId, screenshot, status);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI addDonation error: " + e.getMessage());
            return false;
        }
    }

    public static List<Donation> getPendingDonations() {
        try {
            if (isConnected()) {
                return rmiServer.getPendingDonations();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getPendingDonations error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static List<Donation> getDonorHistory(int donorId) {
        try {
            if (isConnected()) {
                return rmiServer.getDonorHistory(donorId);
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getDonorHistory error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean approveDonation(int donationId) {
        try {
            if (isConnected()) {
                return rmiServer.approveDonation(donationId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI approveDonation error: " + e.getMessage());
            return false;
        }
    }

    public static boolean rejectDonation(int donationId, String reason) {
        try {
            if (isConnected()) {
                return rmiServer.rejectDonation(donationId, reason);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI rejectDonation error: " + e.getMessage());
            return false;
        }
    }

    // Need operations
    public static boolean addNeed(int orphanageId, String category, String description, String status) {
        try {
            if (isConnected()) {
                return rmiServer.addNeed(orphanageId, category, description, status);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI addNeed error: " + e.getMessage());
            return false;
        }
    }

    public static List<Need> getAllNeeds() {
        try {
            if (isConnected()) {
                return rmiServer.getAllNeeds();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getAllNeeds error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean markNeedFulfilled(int needId) {
        try {
            if (isConnected()) {
                return rmiServer.markNeedFulfilled(needId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI markNeedFulfilled error: " + e.getMessage());
            return false;
        }
    }

    // Company operations
    public static List<Company> getAllCompanies() {
        try {
            if (isConnected()) {
                return rmiServer.getAllCompanies();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getAllCompanies error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean addCompany(String name, String location, String accountNumber,
                                     String telebirrPhone, String adminTelegram, int adminId) {
        try {
            if (isConnected()) {
                return rmiServer.addCompany(name, location, accountNumber, telebirrPhone, adminTelegram, adminId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI addCompany error: " + e.getMessage());
            return false;
        }
    }

    // Volunteer operations
    public static List<Volunteer> getAllVolunteers() {
        try {
            if (isConnected()) {
                return rmiServer.getAllVolunteers();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getAllVolunteers error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean addVolunteer(String name, String contact, String status) {
        try {
            if (isConnected()) {
                return rmiServer.addVolunteer(name, contact, status);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI addVolunteer error: " + e.getMessage());
            return false;
        }
    }

    // Volunteer Task operations
    public static List<VolunteerTask> getAllVolunteerTasks() {
        try {
            if (isConnected()) {
                return rmiServer.getAllVolunteerTasks();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getAllVolunteerTasks error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean addVolunteerTask(String name, String category, String description) {
        try {
            if (isConnected()) {
                return rmiServer.addVolunteerTask(name, category, description);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI addVolunteerTask error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteVolunteerTask(int taskId) {
        try {
            if (isConnected()) {
                return rmiServer.deleteVolunteerTask(taskId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI deleteVolunteerTask error: " + e.getMessage());
            return false;
        }
    }

    // Volunteer Task Assignment operations
    public static boolean assignTask(int volunteerId, int taskId, java.time.LocalDate date, int assignedBy) {
        try {
            if (isConnected()) {
                return rmiServer.assignTask(volunteerId, taskId, date, assignedBy);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI assignTask error: " + e.getMessage());
            return false;
        }
    }

    public static List<VolunteerTaskAssignment> getPendingTaskAssignments() {
        try {
            if (isConnected()) {
                return rmiServer.getPendingTaskAssignments();
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getPendingTaskAssignments error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static List<VolunteerTaskAssignment> getTaskAssignmentsByVolunteer(int volunteerId) {
        try {
            if (isConnected()) {
                return rmiServer.getTaskAssignmentsByVolunteer(volunteerId);
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getTaskAssignmentsByVolunteer error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean approveTaskAssignment(int assignmentId) {
        try {
            if (isConnected()) {
                return rmiServer.approveTaskAssignment(assignmentId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI approveTaskAssignment error: " + e.getMessage());
            return false;
        }
    }

    public static boolean rejectTaskAssignment(int assignmentId, String reason) {
        try {
            if (isConnected()) {
                return rmiServer.rejectTaskAssignment(assignmentId, reason);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI rejectTaskAssignment error: " + e.getMessage());
            return false;
        }
    }

    // Notification operations
    public static List<Notification> getNotifications(int userId) {
        try {
            if (isConnected()) {
                return rmiServer.getNotifications(userId);
            }
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("RMI getNotifications error: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public static boolean markNotificationRead(int notificationId) {
        try {
            if (isConnected()) {
                return rmiServer.markNotificationRead(notificationId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI markNotificationRead error: " + e.getMessage());
            return false;
        }
    }

    // Branch synchronization operations
    public static boolean syncNeedToBranch(Need need) {
        try {
            if (isConnected()) {
                return rmiServer.syncNeedToBranch(need);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI syncNeedToBranch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean syncDonationToBranch(Donation donation) {
        try {
            if (isConnected()) {
                return rmiServer.syncDonationToBranch(donation);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI syncDonationToBranch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean syncVolunteerToBranch(Volunteer volunteer) {
        try {
            if (isConnected()) {
                return rmiServer.syncVolunteerToBranch(volunteer);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI syncVolunteerToBranch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean syncVolunteerTaskToBranch(VolunteerTask task) {
        try {
            if (isConnected()) {
                return rmiServer.syncVolunteerTaskToBranch(task);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI syncVolunteerTaskToBranch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean syncTaskAssignmentToBranch(VolunteerTaskAssignment assignment) {
        try {
            if (isConnected()) {
                return rmiServer.syncTaskAssignmentToBranch(assignment);
            }
            return false;
        } catch (Exception e) {
            System.err.println("RMI syncTaskAssignmentToBranch error: " + e.getMessage());
            return false;
        }
    }
}