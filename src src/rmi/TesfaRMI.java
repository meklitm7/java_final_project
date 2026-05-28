package rmi;

import model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TesfaRMI extends Remote {
    // User operations
    User login(String email, String password) throws RemoteException;
    boolean registerUser(String name, String role, String email, String password, String telegramUsername) throws RemoteException;
    List<User> getUsersByRole(String role) throws RemoteException;

    // Donation operations
    boolean addDonation(int donorId, String type, String itemName, int quantity,
                       double amount, int companyId, String screenshot, String status) throws RemoteException;
    List<Donation> getPendingDonations() throws RemoteException;
    List<Donation> getDonorHistory(int donorId) throws RemoteException;
    boolean approveDonation(int donationId) throws RemoteException;
    boolean rejectDonation(int donationId, String reason) throws RemoteException;

    // Need operations
    boolean addNeed(int orphanageId, String category, String description, String status) throws RemoteException;
    List<Need> getAllNeeds() throws RemoteException;
    boolean markNeedFulfilled(int needId) throws RemoteException;

    // Company operations
    List<Company> getAllCompanies() throws RemoteException;
    boolean addCompany(String name, String location, String accountNumber,
                      String telebirrPhone, String adminTelegram, int adminId) throws RemoteException;

    // Volunteer operations
    List<Volunteer> getAllVolunteers() throws RemoteException;
    boolean addVolunteer(String name, String contact, String status) throws RemoteException;

    // Volunteer Task operations
    List<VolunteerTask> getAllVolunteerTasks() throws RemoteException;
    boolean addVolunteerTask(String name, String category, String description) throws RemoteException;
    boolean deleteVolunteerTask(int taskId) throws RemoteException;

    // Volunteer Task Assignment operations
    boolean assignTask(int volunteerId, int taskId, java.time.LocalDate date, int assignedBy) throws RemoteException;
    List<VolunteerTaskAssignment> getPendingTaskAssignments() throws RemoteException;
    List<VolunteerTaskAssignment> getTaskAssignmentsByVolunteer(int volunteerId) throws RemoteException;
    boolean approveTaskAssignment(int assignmentId) throws RemoteException;
    boolean rejectTaskAssignment(int assignmentId, String reason) throws RemoteException;

    // Notification operations
    List<Notification> getNotifications(int userId) throws RemoteException;
    boolean markNotificationRead(int notificationId) throws RemoteException;

    // Branch synchronization operations
    boolean syncNeedToBranch(Need need) throws RemoteException;
    boolean syncDonationToBranch(Donation donation) throws RemoteException;
    boolean syncVolunteerToBranch(Volunteer volunteer) throws RemoteException;
    boolean syncVolunteerTaskToBranch(VolunteerTask task) throws RemoteException;
    boolean syncTaskAssignmentToBranch(VolunteerTaskAssignment assignment) throws RemoteException;
}