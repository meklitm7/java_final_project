package model;

import java.io.Serializable;
import java.time.LocalDate;

public class VolunteerTaskAssignment implements Serializable {
    private static final long serialVersionUID = 1L;

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
    public void setId(int id) { this.id = id; }

    public int getVolunteerId() { return volunteerId; }
    public void setVolunteerId(int volunteerId) { this.volunteerId = volunteerId; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public LocalDate getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDate assignmentDate) { this.assignmentDate = assignmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAssignedBy() { return assignedBy; }
    public void setAssignedBy(int assignedBy) { this.assignedBy = assignedBy; }

    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getTaskCategory() { return taskCategory; }
    public void setTaskCategory(String taskCategory) { this.taskCategory = taskCategory; }

    @Override
    public String toString() {
        return "VolunteerTaskAssignment{" +
               "id=" + id +
               ", volunteerId=" + volunteerId +
               ", taskId=" + taskId +
               ", assignmentDate=" + assignmentDate +
               ", status='" + status + '\'' +
               ", volunteerName='" + volunteerName + '\'' +
               ", taskName='" + taskName + '\'' +
               '}';
    }
}