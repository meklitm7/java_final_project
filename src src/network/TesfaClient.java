package network;

import model.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class TesfaClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static int currentUserId = 0;

    public static void connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to Tesfa Server");
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from Tesfa Server");
        } catch (IOException e) {
            System.err.println("Disconnection error: " + e.getMessage());
        }
    }

    public static Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error sending request: " + e.getMessage());
            return new Response(false, "Communication error: " + e.getMessage(), null);
        }
    }

    // User operations
    public static User login(String email, String password) {
        List<String> credentials = new ArrayList<>();
        credentials.add(email);
        credentials.add(password);

        Request request = new Request(Request.RequestType.LOGIN, credentials, 0);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            currentUserId = ((User) response.getData()).getId();
            return (User) response.getData();
        }
        return null;
    }

    public static boolean register(String name, String role, String email, String password, String telegramUsername) {
        List<String> userData = new ArrayList<>();
        userData.add(name);
        userData.add(role);
        userData.add(email);
        userData.add(password);
        userData.add(telegramUsername);

        Request request = new Request(Request.RequestType.REGISTER, userData, 0);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    // Donation operations
    public static boolean addDonation(int donorId, String type, String itemName, int quantity,
                                     double amount, int companyId, String screenshot, String status) {
        List<Object> donationData = new ArrayList<>();
        donationData.add(donorId);
        donationData.add(type);
        donationData.add(itemName);
        donationData.add(quantity);
        donationData.add(amount);
        donationData.add(companyId);
        donationData.add(screenshot);
        donationData.add(status);

        Request request = new Request(Request.RequestType.ADD_DONATION, donationData, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static List<Donation> getPendingDonations() {
        Request request = new Request(Request.RequestType.GET_DONATIONS, "Pending", currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Donation> donations = (List<Donation>) response.getData();
            return donations;
        }
        return new ArrayList<>();
    }

    public static List<Donation> getDonorHistory(int donorId) {
        Request request = new Request(Request.RequestType.GET_DONATIONS, donorId, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Donation> donations = (List<Donation>) response.getData();
            return donations;
        }
        return new ArrayList<>();
    }

    public static boolean approveDonation(int donationId) {
        Request request = new Request(Request.RequestType.APPROVE_DONATION, donationId, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static boolean rejectDonation(int donationId, String reason) {
        List<Object> data = new ArrayList<>();
        data.add(donationId);
        data.add(reason);

        Request request = new Request(Request.RequestType.REJECT_DONATION, data, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    // Need operations
    public static boolean addNeed(int orphanageId, String category, String description, String status) {
        List<Object> needData = new ArrayList<>();
        needData.add(orphanageId);
        needData.add(category);
        needData.add(description);
        needData.add(status);

        Request request = new Request(Request.RequestType.ADD_NEED, needData, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static List<Need> getAllNeeds() {
        Request request = new Request(Request.RequestType.GET_NEEDS, null, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Need> needs = (List<Need>) response.getData();
            return needs;
        }
        return new ArrayList<>();
    }

    // User operations
    public static List<User> getUsersByRole(String role) {
        Request request = new Request(Request.RequestType.GET_USERS_BY_ROLE, role, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<User> users = (List<User>) response.getData();
            return users;
        }
        return new ArrayList<>();
    }

    // Company operations
    public static List<Company> getCompanies() {
        Request request = new Request(Request.RequestType.GET_COMPANIES, null, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Company> companies = (List<Company>) response.getData();
            return companies;
        }
        return new ArrayList<>();
    }

    // Volunteer operations
    public static List<model.Volunteer> getVolunteers() {
        Request request = new Request(Request.RequestType.GET_VOLUNTEERS, null, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<model.Volunteer> volunteers = (List<model.Volunteer>) response.getData();
            return volunteers;
        }
        return new ArrayList<>();
    }

    // Volunteer Task operations
    public static List<model.VolunteerTask> getVolunteerTasks() {
        Request request = new Request(Request.RequestType.GET_VOLUNTEER_TASKS, null, currentUserId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<model.VolunteerTask> tasks = (List<model.VolunteerTask>) response.getData();
            return tasks;
        }
        return new ArrayList<>();
    }

    public static boolean assignTask(int volunteerId, int taskId, java.time.LocalDate date, int assignedBy) {
        List<Object> taskData = new ArrayList<>();
        taskData.add(volunteerId);
        taskData.add(taskId);
        taskData.add(date);
        taskData.add(assignedBy);

        Request request = new Request(Request.RequestType.ASSIGN_TASK, taskData, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static boolean approveTaskAssignment(int assignmentId) {
        Request request = new Request(Request.RequestType.APPROVE_TASK_ASSIGNMENT, assignmentId, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static boolean rejectTaskAssignment(int assignmentId, String reason) {
        List<Object> data = new ArrayList<>();
        data.add(assignmentId);
        data.add(reason);

        Request request = new Request(Request.RequestType.REJECT_TASK_ASSIGNMENT, data, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    // Notification operations
    public static List<Notification> getNotifications(int userId) {
        Request request = new Request(Request.RequestType.GET_NOTIFICATIONS, null, userId);
        Response response = sendRequest(request);

        if (response.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<Notification> notifications = (List<Notification>) response.getData();
            return notifications;
        }
        return new ArrayList<>();
    }

    public static boolean markNotificationRead(int notificationId) {
        Request request = new Request(Request.RequestType.MARK_NOTIFICATION_READ, notificationId, currentUserId);
        Response response = sendRequest(request);
        return response.isSuccess();
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}