package model;

import java.io.Serializable;

public class Need implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int orphanageId;
    private String category;
    private String description;
    private String status;
    private String orphanageName;
    private int quantity;

    public Need(int id, int orphanageId, String category, String description, String status, String orphanageName, int quantity) {
        this.id = id;
        this.orphanageId = orphanageId;
        this.category = category;
        this.description = description;
        this.status = status;
        this.orphanageName = orphanageName;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrphanageId() { return orphanageId; }
    public void setOrphanageId(int orphanageId) { this.orphanageId = orphanageId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrphanageName() { return orphanageName; }
    public void setOrphanageName(String orphanageName) { this.orphanageName = orphanageName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "Need{" +
               "id=" + id +
               ", orphanageId=" + orphanageId +
               ", category='" + category + '\'' +
               ", description='" + description + '\'' +
               ", status='" + status + '\'' +
               ", quantity=" + quantity +
               '}';
    }
}