package model;

public class Need {
    private int id;
    private int orphanageId;
    private String category;
    private String description;
    private String status;
    private String orphanageName;

    public Need(int id, int orphanageId, String category, String description, String status, String orphanageName) {
        this.id = id;
        this.orphanageId = orphanageId;
        this.category = category;
        this.description = description;
        this.status = status;
        this.orphanageName = orphanageName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getOrphanageId() { return orphanageId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getOrphanageName() { return orphanageName; }
    public void setStatus(String status) { this.status = status; }
}