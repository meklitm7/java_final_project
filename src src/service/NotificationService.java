package service;

import db.NotificationDAO;
import model.Notification;
import java.sql.Connection;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;

    public NotificationService(Connection conn) {
        this.notificationDAO = new NotificationDAO(conn);
    }

    // Create a notification
    public boolean createNotification(int userId, String message, String type) {
        return notificationDAO.addNotification(userId, message, type);
    }

    // Get all notifications for a user
    public List<Notification> getNotifications(int userId) {
        return notificationDAO.getNotificationsByUser(userId);
    }

    // Get unread notifications for a user
    public List<Notification> getUnreadNotifications(int userId) {
        return notificationDAO.getUnreadNotifications(userId);
    }

    // Mark a notification as read
    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    // Mark all notifications for a user as read
    public boolean markAllAsRead(int userId) {
        return notificationDAO.markAllAsRead(userId);
    }

    // Delete a notification
    public boolean deleteNotification(int notificationId) {
        return notificationDAO.deleteNotification(notificationId);
    }

    // Helper methods for specific notification types
    public boolean notifyDonationApproved(int userId, int donationId) {
        String message = "Your donation #" + donationId + " has been approved!";
        return createNotification(userId, message, "DONATION_APPROVED");
    }

    public boolean notifyDonationRejected(int userId, int donationId, String reason) {
        String message = "Your donation #" + donationId + " has been rejected. Reason: " + reason;
        return createNotification(userId, message, "DONATION_REJECTED");
    }

    public boolean notifyVolunteerApproved(int userId) {
        String message = "Your volunteer request has been approved!";
        return createNotification(userId, message, "VOLUNTEER_APPROVED");
    }

    public boolean notifyVolunteerRejected(int userId, String reason) {
        String message = "Your volunteer request has been rejected. Reason: " + reason;
        return createNotification(userId, message, "VOLUNTEER_REJECTED");
    }

    public boolean notifyNewNeed(int userId, int needId, String needTitle) {
        String message = "A new need has been posted: " + needTitle;
        return createNotification(userId, message, "NEW_NEED");
    }
}