// src/model/VolunteerTaskAssignment.java
package model;

import java.time.LocalDate;

public class VolunteerTaskAssignment {
    private int id;
    private int volunteerId;
    private int taskId;
    private LocalDate assignmentDate;
    private String status;
    private int assignedBy;
    private String volunteerName;
    private String taskName;
    private String taskCategory;

    public VolunteerTaskAssignment(int id, int volunteerId, int taskId, LocalDate assignmentDate,
                                  String status, int assignedBy, String volunteerName,
                                  String taskName, String taskCategory) {
        this.id = id;
        this.volunteerId = volunteerId;
        this.taskId = taskId;
        this.assignmentDate = assignmentDate;
        this.status = status;
        this.assignedBy = assignedBy;
        this.volunteerName = volunteerName;
        this.taskName = taskName;
        this.taskCategory = taskCategory;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getVolunteerId() { return volunteerId; }
    public int getTaskId() { return taskId; }
    public LocalDate getAssignmentDate() { return assignmentDate; }
    public String getStatus() { return status; }
    public int getAssignedBy() { return assignedBy; }
    public String getVolunteerName() { return volunteerName; }
    public String getTaskName() { return taskName; }
    public String getTaskCategory() { return taskCategory; }
    public void setStatus(String status) { this.status = status; }
}