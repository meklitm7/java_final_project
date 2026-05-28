package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class VolunteerRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int volunteerId;
    private String requestType;
    private String title;
    private String message;
    private String status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private String volunteerName;

    // Constructor, getters, and setters
    public VolunteerRequest(int id, int volunteerId, String requestType, String title,
                           String message, String status, String adminResponse,
                           LocalDateTime createdAt, LocalDateTime respondedAt, String volunteerName) {
        this.id = id;
        this.volunteerId = volunteerId;
        this.requestType = requestType;
        this.title = title;
        this.message = message;
        this.status = status;
        this.adminResponse = adminResponse;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
        this.volunteerName = volunteerName;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }
}