package network;

import db.*;
import model.*;
import service.NotificationService;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TesfaServer {
    private static final int PORT = 12345;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static Connection conn;

    public static void main(String[] args) {
        // Initialize database connection
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tesfa",
                "root",
                "Meron123@"
            );
            System.out.println("Server database connection established.");
        } catch (SQLException e) {
            System.err.println("Server database connection failed: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Tesfa Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, conn));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Connection conn;

        public ClientHandler(Socket socket, Connection conn) {
            this.clientSocket = socket;
            this.conn = conn;
        }

        @Override
        public void run() {
            try (
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                while (true) {
                    // Read request from client
                    Request request = (Request) in.readObject();
                    System.out.println("Received request: " + request.getType());

                    // Process request and send response
                    Response response = processRequest(request);
                    out.writeObject(response);
                    out.flush();
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private Response processRequest(Request request) {
            try {
                switch (request.getType()) {
                    case LOGIN:
                        return handleLogin(request);
                    case REGISTER:
                        return handleRegister(request);
                    case ADD_DONATION:
                        return handleAddDonation(request);
                    case GET_DONATIONS:
                        return handleGetDonations(request);
                    case APPROVE_DONATION:
                        return handleApproveDonation(request);
                    case REJECT_DONATION:
                        return handleRejectDonation(request);
                    case ADD_NEED:
                        return handleAddNeed(request);
                    case GET_NEEDS:
                        return handleGetNeeds(request);
                    case GET_USERS_BY_ROLE:
                        return handleGetUsersByRole(request);
                    case GET_COMPANIES:
                        return handleGetCompanies(request);
                    case GET_VOLUNTEERS:
                        return handleGetVolunteers(request);
                    case GET_VOLUNTEER_TASKS:
                        return handleGetVolunteerTasks(request);
                    case ASSIGN_TASK:
                        return handleAssignTask(request);
                    case APPROVE_TASK_ASSIGNMENT:
                        return handleApproveTaskAssignment(request);
                    case REJECT_TASK_ASSIGNMENT:
                        return handleRejectTaskAssignment(request);
                    case GET_NOTIFICATIONS:
                        return handleGetNotifications(request);
                    case MARK_NOTIFICATION_READ:
                        return handleMarkNotificationRead(request);
                    default:
                        return new Response(false, "Unknown request type", null);
                }
            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
                e.printStackTrace();
                return new Response(false, "Server error: " + e.getMessage(), null);
            }
        }

        // Request handlers
        private Response handleLogin(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<String> credentials = (List<String>) request.getData();
                String email = credentials.get(0);
                String password = credentials.get(1);

                UserDAO userDAO = new UserDAO(conn);
                User user = userDAO.getUserByEmail(email);

                if (user != null && userDAO.login(email, password)) {
                    return new Response(true, "Login successful", user);
                } else {
                    return new Response(false, "Invalid credentials", null);
                }
            } catch (Exception e) {
                return new Response(false, "Login error: " + e.getMessage(), null);
            }
        }

        private Response handleRegister(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<String> userData = (List<String>) request.getData();
                String name = userData.get(0);
                String role = userData.get(1);
                String email = userData.get(2);
                String password = userData.get(3);
                String telegramUsername = userData.get(4);

                UserDAO userDAO = new UserDAO(conn);
                boolean success = userDAO.addUser(name, role, email, password, telegramUsername);

                if (success) {
                    return new Response(true, "Registration successful", null);
                } else {
                    return new Response(false, "Registration failed", null);
                }
            } catch (Exception e) {
                return new Response(false, "Registration error: " + e.getMessage(), null);
            }
        }

        private Response handleAddDonation(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<Object> donationData = (List<Object>) request.getData();
                int donorId = (int) donationData.get(0);
                String type = (String) donationData.get(1);
                String itemName = (String) donationData.get(2);
                int quantity = (int) donationData.get(3);
                double amount = (double) donationData.get(4);
                int companyId = (int) donationData.get(5);
                String screenshot = (String) donationData.get(6);
                String status = (String) donationData.get(7);

                DonationDAO donationDAO = new DonationDAO(conn);
                boolean success = donationDAO.addDonation(donorId, type, itemName, quantity, amount, companyId, screenshot, status);

                if (success) {
                    return new Response(true, "Donation added successfully", null);
                } else {
                    return new Response(false, "Failed to add donation", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error adding donation: " + e.getMessage(), null);
            }
        }

        private Response handleGetDonations(Request request) {
            try {
                String status = (String) request.getData();
                DonationDAO donationDAO = new DonationDAO(conn);

                List<Donation> donations;
                if ("Pending".equals(status)) {
                    donations = donationDAO.getPendingDonations();
                } else if ("All".equals(status)) {
                    // For simplicity, we'll need to implement getAllDonations in DonationDAO
                    donations = donationDAO.getAllDonations();
                } else {
                    // Get donations by donor ID
                    int donorId = request.getUserId();
                    donations = donationDAO.getDonorHistory(donorId);
                }

                return new Response(true, "Donations retrieved", donations);
            } catch (Exception e) {
                return new Response(false, "Error getting donations: " + e.getMessage(), null);
            }
        }

        private Response handleApproveDonation(Request request) {
            try {
                int donationId = (int) request.getData();
                DonationDAO donationDAO = new DonationDAO(conn);
                boolean success = donationDAO.approveDonation(donationId);

                if (success) {
                    return new Response(true, "Donation approved", null);
                } else {
                    return new Response(false, "Failed to approve donation", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error approving donation: " + e.getMessage(), null);
            }
        }

        private Response handleRejectDonation(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<Object> data = (List<Object>) request.getData();
                int donationId = (int) data.get(0);
                String reason = (String) data.get(1);

                DonationDAO donationDAO = new DonationDAO(conn);
                boolean success = donationDAO.rejectDonation(donationId, reason);

                if (success) {
                    return new Response(true, "Donation rejected", null);
                } else {
                    return new Response(false, "Failed to reject donation", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error rejecting donation: " + e.getMessage(), null);
            }
        }

        private Response handleAddNeed(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<Object> needData = (List<Object>) request.getData();
                int orphanageId = (int) needData.get(0);
                String category = (String) needData.get(1);
                String description = (String) needData.get(2);
                String status = (String) needData.get(3);

                NeedDAO needDAO = new NeedDAO(conn);
                boolean success = needDAO.addNeed(orphanageId, category, description, status);

                if (success) {
                    return new Response(true, "Need added successfully", null);
                } else {
                    return new Response(false, "Failed to add need", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error adding need: " + e.getMessage(), null);
            }
        }

        private Response handleGetNeeds(Request request) {
            try {
                NeedDAO needDAO = new NeedDAO(conn);
                List<Need> needs = needDAO.getAllNeeds();
                return new Response(true, "Needs retrieved", needs);
            } catch (Exception e) {
                return new Response(false, "Error getting needs: " + e.getMessage(), null);
            }
        }

        private Response handleGetUsersByRole(Request request) {
            try {
                String role = (String) request.getData();
                UserDAO userDAO = new UserDAO(conn);
                List<User> users = userDAO.getUsersByRole(role);
                return new Response(true, "Users retrieved", users);
            } catch (Exception e) {
                return new Response(false, "Error getting users: " + e.getMessage(), null);
            }
        }

        private Response handleGetCompanies(Request request) {
            try {
                CompanyDAO companyDAO = new CompanyDAO(conn);
                List<Company> companies = companyDAO.listCompanies();
                return new Response(true, "Companies retrieved", companies);
            } catch (Exception e) {
                return new Response(false, "Error getting companies: " + e.getMessage(), null);
            }
        }

        private Response handleGetVolunteers(Request request) {
            try {
                VolunteerDAO volunteerDAO = new VolunteerDAO(conn);
                List<model.Volunteer> volunteers = volunteerDAO.getAllVolunteers();
                return new Response(true, "Volunteers retrieved", volunteers);
            } catch (Exception e) {
                return new Response(false, "Error getting volunteers: " + e.getMessage(), null);
            }
        }

        private Response handleGetVolunteerTasks(Request request) {
            try {
                VolunteerTaskDAO taskDAO = new VolunteerTaskDAO(conn);
                List<model.VolunteerTask> tasks = taskDAO.getAllTasks();
                return new Response(true, "Tasks retrieved", tasks);
            } catch (Exception e) {
                return new Response(false, "Error getting tasks: " + e.getMessage(), null);
            }
        }

        private Response handleAssignTask(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<Object> taskData = (List<Object>) request.getData();
                int volunteerId = (int) taskData.get(0);
                int taskId = (int) taskData.get(1);
                java.time.LocalDate date = (java.time.LocalDate) taskData.get(2);
                int assignedBy = (int) taskData.get(3);

                VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
                boolean success = assignmentDAO.assignTask(volunteerId, taskId, date, assignedBy);

                if (success) {
                    return new Response(true, "Task assigned successfully", null);
                } else {
                    return new Response(false, "Failed to assign task", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error assigning task: " + e.getMessage(), null);
            }
        }

        private Response handleApproveTaskAssignment(Request request) {
            try {
                int assignmentId = (int) request.getData();
                VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
                boolean success = assignmentDAO.approveAssignment(assignmentId);

                if (success) {
                    return new Response(true, "Task assignment approved", null);
                } else {
                    return new Response(false, "Failed to approve task assignment", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error approving task assignment: " + e.getMessage(), null);
            }
        }

        private Response handleRejectTaskAssignment(Request request) {
            try {
                @SuppressWarnings("unchecked")
                List<Object> data = (List<Object>) request.getData();
                int assignmentId = (int) data.get(0);
                String reason = (String) data.get(1);

                VolunteerTaskAssignmentDAO assignmentDAO = new VolunteerTaskAssignmentDAO(conn);
                boolean success = assignmentDAO.rejectAssignment(assignmentId, reason);

                if (success) {
                    return new Response(true, "Task assignment rejected", null);
                } else {
                    return new Response(false, "Failed to reject task assignment", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error rejecting task assignment: " + e.getMessage(), null);
            }
        }

        private Response handleGetNotifications(Request request) {
            try {
                int userId = request.getUserId();
                NotificationDAO notificationDAO = new NotificationDAO(conn);
                List<Notification> notifications = notificationDAO.getNotificationsByUser(userId);
                return new Response(true, "Notifications retrieved", notifications);
            } catch (Exception e) {
                return new Response(false, "Error getting notifications: " + e.getMessage(), null);
            }
        }

        private Response handleMarkNotificationRead(Request request) {
            try {
                int notificationId = (int) request.getData();
                NotificationDAO notificationDAO = new NotificationDAO(conn);
                boolean success = notificationDAO.markAsRead(notificationId);

                if (success) {
                    return new Response(true, "Notification marked as read", null);
                } else {
                    return new Response(false, "Failed to mark notification as read", null);
                }
            } catch (Exception e) {
                return new Response(false, "Error marking notification as read: " + e.getMessage(), null);
            }
        }
    }
}